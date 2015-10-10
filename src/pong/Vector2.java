package pong;

/**
 *  File name: Vector2.java
 *  Date: Dec.12.2014
 *
 *  Description: This class is for setting the object's positions and 
 *              updating its movements. 
 * 
 * @author Alexis Park
 */

public class Vector2
{
    ///////////////////////// FIELDS /////////////////////
    public float x;
    public float y;
    
    ///////////////////// CONSTRUCTOR ///////////////////
    public Vector2()
    {
        this(0,0);
    }
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    public Vector2(Vector2 v)
    {
        this(v.x, v.y);
    }
    
    ///////////////////////// METHODS ////////////////////
    // setters
    public void set(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public void set(Vector2 v)
    {
        this.set(v.x, v.y);
    }
    
    // arithmetics
    public Vector2 add(Vector2 rhs)
    {
        this.x += rhs.x;
        this.y += rhs.y;
        
        return this;
    }
    
    public Vector2 subtract(Vector2 rhs)
    {
        this.x -= rhs.x;
        this.y -= rhs.y;
        
        return this;
    }
    
    public Vector2 scale(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        
        return this;
    }
    
    public float dotProduct(Vector2 rhs)
    {
        return (this.x * rhs.x) + (this.y * rhs.y);
    }
    
    public float getDistance(Vector2 rhs)
    {
        /* Pythagorean Theorem
         * 
         * the distance between two positions is 
         * d^2 = (x2 - x1)^2 + (y2 - y1)^2
         */ 
        float disX = this.x - rhs.x;
        float disY = this.y - rhs.y;
        disX *= disX;
        disY *= disY;
        
        return (float)Math.sqrt(disX + disY);
    }
    
    public float getLength()
    {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    // to make the length as 1
    public Vector2 normalize()
    {
        float lengthInverse = 1 / this.getLength();
        this.x *= lengthInverse;
        this.y *= lengthInverse;
        
        return this;
    }
    
    // return a copy of this objec
    public Vector2 clone()
    {
        return new Vector2(this);
    }
    
    // to debug
    @Override
    public String toString()
    {
        return String.format("Vector2(x=%.3f, y=%.3f)", x, y);
    }
}
