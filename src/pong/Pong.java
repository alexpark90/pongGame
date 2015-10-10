
package pong;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.fixedfunc.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;

/**
 *  File name: Pong.java
 *  Date: Dec.12.2014
 *
 *  Description: This is a logical main class for Pong game.
 *              It has a ball, 2 paddles(one for player, one for a computer).
 *              It is implementing all game logic, redering visuals and sounds,
 *              and listeing keyboard events.
 * 
 * @author Alexis Park
 */

public class Pong implements GLEventListener
{
    ////////////////////////////////// FIELDS //////////////////////////////////
    
    ////////////////////// CONSTANTS
    // screen size
    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;
    // frame per sec
    private static final int FPS = 60;
    // ball
    private static final float BALL_SPEED = 300;
    private static final float BALL_RADIUS = 5;
    // paddles
    private static final float PADDLE_SPEED = 250;
    private static final float PADDLE_WIDTH = 6;
    private static final float PADDLE_HEIGHT = 50;
    // frequency of the computer's reacting movement
    private static final float AI_UPDATE_TIME = 0.3f;
    // the score point to win the game
    private static final int MAX_SCORE = 5;

    // to visualize
    private static GLCanvas canvas;
    private static FPSAnimator animator;
    private static GLU glu;
    private static Frame frame;

    //////////////////////// MEMBER VARIABLES
    private float gameTime;     // sec
    private float frameTime;    // sec
    private long  prevTime;     // nano sec
    private float readyTime;
    private float aiTime;
    
    // to store the current screen resolution
    private int screenWidth;
    private int screenHeight;
    
    // to store scores of each player
    private int playerScore;
    private int computerScore;
    
    // declare objects needed in the game
    private Ball ball;
    private Paddle player;
    private Paddle computer;
    private Sound sound;
    private TextRenderer textRenderer;

    // group for game state
    public enum GameState { MENU, START, READY, GAME }
    
    // set the initial game state to MENU
    private GameState gameState = GameState.MENU;
    
    //////////////////////////////// CONSTRUCTOR ///////////////////////////////
    public Pong()
    {
        System.out.println("Now Starting Pong Game...");

        // initialize game logic and gl part 
        initPong();
        initJOGL();

        // reset timer
        prevTime = System.nanoTime();
        gameTime = frameTime = 0;
    }

    /////////////////////////////////// METHODS ////////////////////////////////
    /**
     * initPong method.
     * It creates intances of Ball, Paddle, Sound classes and initialize them.
     * 
     */
    private void initPong()
    {
        // sound
        sound = new Sound();
        
        // ball
        ball = new Ball();
        ball.setRadius(BALL_RADIUS);
        ball.setColor(1,1,1);
        
        // paddle for a player 
        player = new Paddle();
        player.setSpeed(PADDLE_SPEED);
        player.setColor(1,1,1);
        player.setWidth(PADDLE_WIDTH);
        player.setHeight(PADDLE_HEIGHT);
        player.setBounds(0, SCREEN_HEIGHT);
        player.setPosition(10.0f, SCREEN_HEIGHT/2.0f);
        
        // paddle for a computer
        computer = new Paddle();
        computer.setSpeed(PADDLE_SPEED);
        computer.setColor(1,1,1);
        computer.setWidth(PADDLE_WIDTH);
        computer.setHeight(PADDLE_HEIGHT);
        computer.setBounds(0, SCREEN_HEIGHT);
        computer.setPosition(SCREEN_WIDTH-10.0f, SCREEN_HEIGHT/2.0f);
        
        // scores to draw
        textRenderer = new TextRenderer(new Font("Dialog", Font.BOLD, 60));
        playerScore = computerScore = 0;        
    }
    
    /**
     * initJOGL method.
     * It initialize the visual part of the game.
     * 
     */
    private void initJOGL()
    {
        // OpenGL capabilities
        GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        // create instances to draw objects
        frame = new Frame("Alex's Pong Game");
        canvas = new GLCanvas(caps);
        animator = new FPSAnimator(canvas, FPS);

        // config frame
        frame.add(canvas);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setLocation(100, 100);
        frame.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                exit();
            }
        });
        frame.setVisible(true);

        // config canvas
        canvas.addGLEventListener(this);
        canvas.requestFocus();

        // start animator
        animator.start();

        // to debug
        System.out.println("JOGL is initialized.");
    }
    
    /**
     * exit method.
     * It terminates the game and clean the resources.
     * 
     */
    public static void exit()
    {
        animator.stop();
        frame.dispose();

        System.out.println("Exiting Pong Game...");
        System.exit(0);
    }
    
    /**
     * getFrameTime method.
     * compute frame time in sec
     * 
     * @return float - calculated frame time
     */
    private float getFrameTime()
    {
        // get the current time
        long currTime = System.nanoTime();
        
        // calculate new frame time and change it from nanosec to sec
        float calculatedFrameTime = (float)((currTime - prevTime) / 1000000000.0);
        
        // update prevTime
        prevTime = currTime;
        
        return calculatedFrameTime;
    }

    /**
     * update method.
     * It updates all objects in the game depending on the game state.
     * It also call play method of Sound class to produce proper sound for each event.
     * 
     */
    private void update()
    {
        
        if(gameState == GameState.MENU)
        {
            player.update(frameTime);
            return;
        }
        else if(gameState == GameState.READY)
        {
            readyTime += frameTime;
            player.update(frameTime);
            if(readyTime > 1.0)
            {
                setGameState(GameState.GAME);
            }
            ball.update(frameTime);
            return;
        }
        
        // update every objects in the game, so they can keep moving
        ball.update(frameTime);
        player.update(frameTime);
        computer.update(frameTime);
        
        // update AI
        updateAI();
        
        // collision test
        int hit = hitTest();
        // when the ball hit on the wall
        if(hit==1)
        {
            sound.play("blip01.wav");
        }
        // when the ball hit on the paddle
        else if(hit==2)
        {
            sound.play("blip02.wav");
        }
        // when the ball is missed
        else if(hit==3)
        {
            sound.play("blip03.wav");
            
            // if either player gets to the max score,
            // the game stop and be munu state.
            if (computerScore == MAX_SCORE || playerScore == MAX_SCORE)
            {
                setGameState(GameState.MENU);
            }
        }
    }
    
    /**
     * updateAI method.
     * It moves the computer paddle checking ball's position
     * 
     */
    private void updateAI()
    {
        aiTime += frameTime;
        // only update if AI_UPDATE_TIME(=0.3) passed
        if(aiTime < AI_UPDATE_TIME)
            return;
        // only update if the ball is heading to the computer
        if(ball.getVelocity().x < 0)
            return;

        computer.setMovingDown(false);
        computer.setMovingUp(false);
 
        float ballY = ball.getPosition().y;
        float paddleY = computer.getPosition().y;
        float offset = computer.getHeight() / 2.0f;

        // when the ball is moving upper than the paddle's position
        if(ballY > (paddleY + offset))
        {
            computer.setMovingUp(true);
        }
        // when the ball is moving lower than the paddle's position
        else if(ballY < (paddleY - offset))
        {
            computer.setMovingDown(true);
        }
        
        // reset ai timer
        aiTime = 0;
    }
    
    /**
     * hitTest method.
     * It gets the current postion of the ball, and then decide the game state and scores.
     * 
     * @return int value to be used to call a proper sound for each event.
     */
    private int hitTest()
    {
        int hit = 0;
        
        Vector2 position = ball.getPosition();
        Vector2 velocity = ball.getVelocity();
        Vector2 leftP = player.getPosition();
        Vector2 rightP = computer.getPosition();
        float offset = player.getHeight() * 0.5f;
        
        // when the ball hit the bottom wall
        if (position.y < 0)
        {
            ball.setPosition(position.x, 0);
            ball.setVelocity(velocity.x, -velocity.y);
            hit = 1;
        }
        // when the ball hit the top wall
        else if(position.y > SCREEN_HEIGHT)
        {
            ball.setPosition(position.x,SCREEN_HEIGHT);
            ball.setVelocity(velocity.x, -velocity.y);
            hit = 1;
        }
        
        // when the player miss the ball, so it went beyound the left-side wall
        if (position.x < 0)
        {
            computerScore++;
            hit = 3;
            setGameState(GameState.READY);
        }
        // when the player hit the ball, so it didn't reach to the left-side wall
        else if (position.x < leftP.x)
        {
            if ((leftP.y - offset) < position.y && position.y < (leftP.y + offset))
            {
                ball.setPosition(leftP.x, position.y);
                ball.setVelocity(-velocity.x, velocity.y);
                hit = 2;
            }
        }
        // when the computer miss the ball, so it went beyond the right-side wall
        else if (position.x > SCREEN_WIDTH)
        {
            playerScore++;
            hit = 3;
            setGameState(GameState.READY);
        }
        // when the computer hit the ball, so it didn't reach to the right-side wall
        else if (position.x > rightP.x)
        {
            if (position.y > (rightP.y - offset) && position.y < (rightP.y + offset))
            {
                ball.setPosition(rightP.x, position.y);
                ball.setVelocity(-velocity.x, velocity.y);
                hit = 2;
            }
        }
        return hit;
    }

    /**
     * setGameState method.
     * initial game state = MUNU
     * if the space Key is pressed, it changes to START
     * and then it moves to READY state right away.
     * after pausing 1 second on READY state, it changes to GAME
     * when either player get scored, go back to READY state.
     * only set to MENU state when either player reaches to max score.
     * 
     * @param state
     */
    private void setGameState(GameState state)
    {
        gameState = state;
        switch(gameState)
        {
            case MENU:
                break;

            // when game is started
            // prepare a ball and enter READY state
            case START:
                sound.play("blip02.wav");
                playerScore = computerScore = 0;
                ball.setPosition(SCREEN_WIDTH/2.0f, SCREEN_HEIGHT/2.0f);
                ball.setVelocity(0,0);
                setGameState(GameState.READY);
                break;

            // when game is in ready state
            // pause 1 second before firing a ball
            case READY:
                readyTime = 0;
                
                break;

            // when game is entered in game mode
            // fire a ball
            case GAME:
                sound.play("blip01.wav");
                ball.fire(SCREEN_WIDTH/2.0f, SCREEN_HEIGHT/2.0f, BALL_SPEED);
                break;
        }
    }

    /**
     * drawFrame method.
     * It draws the frame
     * 
     * @param drawble
     */
    private void drawFrame(GLAutoDrawable drawable)
    {
        final GL2 gl = drawable.getGL().getGL2();

        // clear screen
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();

        // draw scene
        drawBackground(gl);
        drawPaddles(gl);
        drawBall(gl);
        
        // draw text
        drawScores(gl);
        drawMessage(gl);
        drawResult(gl);
    }

   /**
    * drawBall method.
    * It draw a squre shaped ball using 2 triagles. 
    * 
    * @param gl
    */
    private void drawBall(GL2 gl)
    {
        // get the position where a ball will be drawn
        Vector2 p = ball.getPosition();
        
        // get the radius and the color to use to draw
        float r = ball.getRadius();
        Color color = ball.getColor();
        
        // set the color of gl
        gl.glColor3f(color.getRed(),color.getGreen(),color.getBlue());

        // draw 2 triangles
        gl.glBegin(GL.GL_TRIANGLES); 
        gl.glVertex2f(p.x-r, p.y-r); //V1
        gl.glVertex2f(p.x+r, p.y-r); //V2
        gl.glVertex2f(p.x+r, p.y+r); //V3
        gl.glVertex2f(p.x-r, p.y-r); //V1
        gl.glVertex2f(p.x+r, p.y+r); //V3
        gl.glVertex2f(p.x-r, p.y+r); //V4
        gl.glEnd();
    }

    /**
     * drawPaddles method.
     * It draws player's and computer's paddle
     * 
     * @param gl
     */
    private void drawPaddles(GL2 gl)
    {
        // get position and color
        Vector2 center = player.getPosition();
        float offsetX = player.getWidth() / 2.0f;
        float offsetY = player.getHeight() / 2.0f;
        Color color = player.getColor();

        // draw player's paddle
        gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        gl.glVertex2f(center.x + offsetX, center.y - offsetY);
        gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        gl.glVertex2f(center.x - offsetX, center.y + offsetY);
        gl.glEnd();

        // get position and color
        center = computer.getPosition();
        offsetX = computer.getWidth() / 2.0f;
        offsetY = computer.getHeight() / 2.0f;
        color = computer.getColor();

        // draw computer's paddle
        gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        gl.glVertex2f(center.x + offsetX, center.y - offsetY);
        gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        gl.glVertex2f(center.x - offsetX, center.y + offsetY);
        gl.glEnd();

    }

   /**
    * drawScores method.
    * It draws the scores of players and computers using textRenderer
    * 
    * @param gl
    */
    private void drawScores(GL2 gl)
    {
        // to store the string values of each score
        String score1, score2;
        
        // set the textRenderer and draw
        textRenderer.beginRendering(SCREEN_WIDTH, SCREEN_HEIGHT);
        
        textRenderer.setColor(1, 1, 1, 0.5f);
        
        score1 = String.valueOf(playerScore);
        textRenderer.draw(score1, 250, 420);
        
        score2 = String.valueOf(computerScore);
        textRenderer.draw(score2, 350, 420);
        
        textRenderer.endRendering();
    }

   /**
    * drawMessage method.
    * It draws the message (ex: game state, how to start) using textRenderer
    * 
    * @param gl
    */
    private void drawMessage(GL2 gl)
    {
        // only draw when the game is in munu state
        if(gameState != GameState.MENU)
            return;
        
        // message to draw
        String message = "Press SPACE to start";
        
        // compute text bounds to draw text at the center of screen
        Rectangle2D rect = textRenderer.getBounds(message);
        
        textRenderer.beginRendering(SCREEN_WIDTH, SCREEN_HEIGHT);
        textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        textRenderer.draw(message,
                          (int)(SCREEN_WIDTH/2.0 - rect.getCenterX()),
                          (int)(SCREEN_HEIGHT/2.0 - rect.getCenterY()));

        textRenderer.endRendering();
    }
    
   /**
    * drawResult method.
    * It draws the result (ex: win, or loose) using textRenderer
    * 
    * @param gl
    */
    private void drawResult(GL2 gl)
    {
        // only draw when either player or computer reach to the max score
        if(playerScore != MAX_SCORE && computerScore != MAX_SCORE)
            return;
        
        String result = null;
        
        if(playerScore == MAX_SCORE)
        {
            result = "You Won! Congrats!";
        }
        else
        {
            result = "You Lost! Try Again!";
        }
        
        // compute text bounds to draw text at the center of screen
        Rectangle2D rect = textRenderer.getBounds(result);
        
        textRenderer.beginRendering(SCREEN_WIDTH, SCREEN_HEIGHT);
        textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        textRenderer.draw(result,
                          (int)(SCREEN_WIDTH/2.0 - rect.getCenterX()),
                          (int)(SCREEN_HEIGHT/2.0 - rect.getCenterY() + 80));

        textRenderer.endRendering();
    }

    /**
     * drawBackground method.
     * It draws the top and bottom lines, and the center line
     * 
     * @param gl
     */
    private void drawBackground(GL2 gl)
    {
        gl.glLineWidth(10);
        gl.glColor3f(0.8f, 0.8f, 0.8f);

        // bottom line
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(0, 0);
        gl.glVertex2f(SCREEN_WIDTH, 0);
        gl.glEnd();

        // top line
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(0, SCREEN_HEIGHT);
        gl.glVertex2f(SCREEN_WIDTH, SCREEN_HEIGHT);
        gl.glEnd();

        // center dotted line
        final int DOT_LENGTH = 10;
        gl.glLineWidth(5);
        gl.glBegin(GL.GL_LINES);
        for(int i = 0; i <= SCREEN_HEIGHT; i += DOT_LENGTH * 2)
        {
            gl.glVertex2f(SCREEN_WIDTH/2, i);
            gl.glVertex2f(SCREEN_WIDTH/2, i + DOT_LENGTH);
        }
        gl.glEnd();

        // reset line width to default
        gl.glLineWidth(1);
    }

    /**
     * init method.
     * Methods for the implementation of GLEventListener
     * 
     * @param drawable
     */
    @Override
    public void init(GLAutoDrawable drawable)
    {
        ((Component)drawable).addKeyListener( new MyEventListener());
        ((Component)drawable).addMouseMotionListener(new MyEventListener());

        // init OpenGL
        GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
    }

    /**
     * reshape method.
     * Methods for the implementation of GLEventListener
     * 
     * @param drawable
     * @param x
     * @param y
     * @param w
     * @param h
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
    {
        // to remember the screen width/height when the window is resized
        screenWidth = w;
        screenHeight = h;
        
        GL2 gl = drawable.getGL().getGL2();

        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, -1, 1);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * display method.
     * Methods for the implementation of GLEventListener
     * 
     * @param drawable
     */
    @Override
    public void display(GLAutoDrawable drawable)
    {
        // get calculatedFrameTime
        frameTime = getFrameTime();
        gameTime += frameTime;

        // update scene before drawing
        update();

        // draw the frame
        drawFrame(drawable);
    }

    /**
     * dispose method.
     * Methods for the implementation of GLEventListener
     * 
     * @param gLDrawable
     */
    @Override
    public void dispose(GLAutoDrawable gLDrawable)
    {
    }
    
    //////////////////////////////////Inner Class /////////////////////////////
    // inner class to implements KeyListener and MouseMotionLister
    class MyEventListener implements KeyListener, MouseMotionListener
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_ESCAPE:
                    // exit the game
                    System.out.println("Exiting Pong Game...");
                    exit();
                    break;

                case KeyEvent.VK_UP:
                    // move player's paddle up
                    player.setMovingUp(true);
                    break;

                case KeyEvent.VK_DOWN:
                    // move player's paddle down
                    player.setMovingDown(true);
                    break;    
            }
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_SPACE:
                    // start the game
                    setGameState(GameState.START);
                    break;

                case KeyEvent.VK_UP:
                    // stop moving player's paddle up
                    player.setMovingUp(false);
                    break;

                case KeyEvent.VK_DOWN:
                    // stop moving player's paddle up
                    player.setMovingDown(false);
                    break; 
            }
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            float y = (float)e.getY() / screenHeight * SCREEN_HEIGHT;
            player.getPosition().y = SCREEN_HEIGHT - y;
        }
        
        @Override
        public void mouseMoved(MouseEvent e)
        {
        }
        
        @Override
        public void keyTyped(KeyEvent e)
        {
        }
    }
}

