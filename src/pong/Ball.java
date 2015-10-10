package pong;

/**
 *  File name: Ball.java 
 *  Date: Dec.12.2014
 * 
 *  Description: This class is for setting and controlling a ball.
 *              It has the method to fire the ball when the game starts, and
 *              update method uses instance of Vector2 to update ball's position. 
 * 
 * @author Alexis Park
 */

public class Ball 
{
    ///////////////////////// FIELDS /////////////////////
    private Vector2 position;
    private Vector2 velocity;
    private Color color;
    private float radius;
    
    ///////////////////// CONSTRUCTOR ///////////////////
    public Ball()
    {
        position = new Vector2(0,0);
        velocity = new Vector2(0,0);
        color = new Color(1,1,1);
        radius = 1;
    }
    
    ///////////////////////// METHODS ////////////////////
    // setters
    public void setPosition(float x, float y)
    {
        position.set(x,y);
    }
    
    public void setVelocity(float x, float y)
    {
        velocity.set(x,y);
    }
    
    public void setColor(float r, float g, float b)
    {
        color.set(r,g,b);
    }
    
    public void setRadius(float r)
    {
        radius = r;
    }
    
    // getters
    public Vector2 getPosition()
    {
        return position;
    }
    
    public Vector2 getVelocity()
    {
        return velocity;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public float getRadius()
    {
        return radius;
    }
    
    // to update the ball movement in the game
    public void update(float frameTime)
    {
        Vector2 delta = velocity.clone().scale(frameTime);
        position.add(delta);
    }
    
    public void fire(float posX, float posY, float speed)
    {
        // set the start position of the ball
        position.set(posX, posY);
        
        // generate velocity vector
        velocity.x = -1;
        velocity.y = (float)(Math.random() * 2 -1);
        velocity.normalize();
        velocity.scale(speed);        
    }
}
