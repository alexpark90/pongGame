package pong;

/**
 *  File name: Paddle.java
 *  Date: Dec.12.2014
 *
 *  Description: This class is for setting and controlling a paddle.
 *              It contains instance of Vector2 to update its position.
 * 
 * @author Alexis Park
 */

public class Paddle 
{
    ///////////////////////// FIELDS /////////////////////
    private Vector2 position;
    private Color color;
    private float speed;
    private float width;
    private float height;    
    // bounds
    private float minY;
    private float maxY; 
    // flag for paddle is moving up / moving down
    private boolean movingUp;       
    private boolean movingDown;

    ///////////////////// CONSTRUCTOR ///////////////////
    public Paddle()
    {
        // init members
        position = new Vector2();
        color = new Color();
        speed = 0;
        width = height = 1;
        minY = maxY = 0;
        movingUp = movingDown = false;
    }


    ///////////////////////// METHODS ////////////////////
    // setters
    public void setPosition(float x, float y)
    {
        position.set(x, y);
    }
    public void setColor(float r, float g, float b)
    {
        color.set(r, g, b);
    }
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }
    public void setWidth(float width)
    {
        this.width = width;
    }
    public void setHeight(float height)
    {
        this.height = height;
    }
    public void setBounds(float min, float max)
    {
        minY = min;
        maxY = max;
    }
    public void setMovingUp(boolean flag)
    {
        movingUp = flag;
    }
    public void setMovingDown(boolean flag)
    {
        movingDown = flag;
    }
    
    // getters
    public Vector2 getPosition()
    {
        return position;
    }
    public Color getColor()
    {
        return color;
    }
    public float getSpeed()
    {
        return speed;
    }
    public float getWidth()
    {
        return width;
    }
    public float getHeight()
    {
        return height;
    }
    public boolean isMovingUp()
    {
        return movingUp;
    }
    public boolean isMovingDown()
    {
        return movingDown;
    }

    @Override
    public String toString()
    {
        return "Paddle(position=" + position + ",\n" +
               "       color="    + color    + ",\n" +
               "       speed="    + speed    + ",\n" +
               "       width="    + width    + ",\n" +
               "       height="   + height   + ")";
    }

    // to update the position of a paddle in the game
    public void update(float frameTime)
    {
        // move the paddle up
        if(movingUp)
        {
            position.y += speed * frameTime;
            if (position.y > maxY)
                position.y = maxY;
        }

        // move the paddle down
        if(movingDown)
        {
            position.y -= speed * frameTime;
            if (position.y < minY)
                position.y = minY;
        }
    }
}
