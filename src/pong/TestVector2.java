
package pong;

/**
 *  File name: TestVector2.java 
 *
 *  Description: This class is for testing vector2 class.
 * 
 * @author Alexis Park
 */

public class TestVector2 
{
    public static void main(String[] args) 
    {
        Vector2 v1 = new Vector2(2,2);
        Vector2 v2 = new Vector2(1,1);
        
        // get the distance between point v1 and v2
        System.out.println(v1.getDistance(v2));
        
         // v1 - v2
        System.out.println(v1.subtract(v2));
        
        // v1 * -1
        System.out.println(v1.scale(-1));
        
        // It need to be done because the current length is not 1 (its root2)
        System.out.println(v1.normalize());
        
        // above 3 methods calling is equal to v1.subtract(v2).scale(-1).normalize();
    }
}
