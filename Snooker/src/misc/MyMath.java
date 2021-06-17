package misc;

/**
 * Some additional math constants or functions
 */
public class MyMath {

    /** Private constructor, as the class is static */
    private MyMath() {}
    
    /** PI divided by 2, save that math */
    public static double PI_2 = Math.PI / 2.0 ;
    
    /** Double precision reciprocal of the square root of 3 */
    public static double rsqrt3 = 1 / Math.sqrt(3) ;

    /** Reciprocal of the square root of 3 */
    public static float rsqrt3f = (float) (1 / Math.sqrt(3)) ;

}
