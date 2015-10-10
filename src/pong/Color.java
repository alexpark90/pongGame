package pong;

/**
 *  File name: Color.java
 *  Date: Dec.12.2014
 *
 *  Description: This class is used to set the color of objects.
 * 
 * @author Alexis Park
 */
public class Color 
{
    ///////////////////////// FIELDS /////////////////////
    private float red;
    private float green;
    private float blue;


    ///////////////////// CONSTRUCTORS ///////////////////
    public Color()
    {
        set(1, 1, 1);
    }
    public Color(float r, float g, float b)
    {
        set(r, g, b);
    }
    public Color(Color c)
    {
        this(c.red, c.green, c.blue);
    }

    ///////////////////////// METHODS ////////////////////
    // setters
    public final void set(float r, float g, float b)
    {
        // clamp before assign
        red = clamp(r);
        green = clamp(g);
        blue = clamp(b);
    }
    public void set(Color c)
    {
        set(c.red, c.green, c.blue);
    }
    public void setRed(float red)
    {
        this.red = clamp(red);
    }
    public void setGreen(float green)
    {
        this.green = clamp(green);
    }
    public void setBlue(float blue)
    {
        this.blue = clamp(blue);
    }

    // getters
    public Color get()
    {
        return this;
    }
    public float getRed()
    {
        return red;
    }
    public float getGreen()
    {
        return green;
    }
    public float getBlue()
    {
        return blue;
    }
    
    // limit value range between 0 and 1 (normalize)
    private float clamp(float value)
    {
        if(value < 0)
            value = 0;
        else if(value > 1)
            value = 1;
        return value;
    }
    
    @Override
    public String toString()
    {
        return String.format("Color(red=%1.3f, green=%1.3f, blue=%1.3f)",
                             red, green, blue);
    }
}
