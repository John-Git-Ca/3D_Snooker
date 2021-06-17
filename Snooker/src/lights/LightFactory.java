package lights;

import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.Light;
import org.jogamp.java3d.PointLight;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;

/**
 * Factory class for creating Lights.
 * <p>
 * Contains a bunch of overloaded static methods that create Lights. <br>
 * Just remember to add them to the branchgroup.
 */
public class LightFactory extends Light {
    /** Overly large bounding sphere for all lights to use */
    private static BoundingSphere bounds = new BoundingSphere(new Point3d(), 100);

    /** Private constructor, as class is a factory */
    private LightFactory () {}

    /**
     * Creates an ambient white light of brightness 0.2.
     * @return The newly created ambient light
     */
    public static AmbientLight createAmbientLight () {
        return createAmbientLight(0.2f);
    }
    
    /**
     * Creates an ambient white light of the given brightness
     * @param brightness 0.0 to 1.0 brightness of the ambient light
     * @return The newly created AmbientLight
     */
    public static AmbientLight createAmbientLight (float brightness) {
        return createAmbientLight(new Color3f(brightness, brightness, brightness));
    }
    
    /**
     * Creates an ambient light of massive boundary with the given colour
     * @param clr The colour to set the ambient light to
     * @return The newly created AmbientLight
     */
    public static AmbientLight createAmbientLight (Color3f clr) {
        AmbientLight al = new AmbientLight(clr);
        al.setInfluencingBounds(bounds);
        return al;
    }

    /**
     * Creates a new point light with the given paramaters.
     * The colour is defaulted to full white.
     * The attenuation is defaulted to constant.
     * @param pos The position of the point light
     * @return The newly created point light
     */
    public static PointLight createPointLight (Point3f pos) {
        return createPointLight(pos, new Color3f(1,1,1), new Point3f(1,0,0));
    }
    
    /**
     * Creates a new point light with the given paramaters.
     * The attenuation is defaulted to constant.
     * @param pos The position of the point light
     * @param clr The colour of the point light
     * @return The newly created point light
     */
    public static PointLight createPointLight (Point3f pos, Color3f clr) {
        return createPointLight(pos, clr, new Point3f(1,0,0));
    }
    
    /**
     * Creates a new point light with the given paramaters.
     * Colour is defaulted to full white.
     * @param pos The position of the point light
     * @param atten The attenuation of the point light (constant, linear, quadratic)
     * @return The newly created point light
     */
    public static PointLight createPointLight (Point3f pos, Point3f atten) {
        return createPointLight(pos, new Color3f(1,1,1), atten);
    }
    
    /**
     * Creates a new point light with the given paramaters.
     * @param pos The position of the point light
     * @param clr The colour of the point light
     * @param atten The attenuation of the point light Point3f(constant, linear, quadratic)
     * @return The newly created point light
     */
    public static PointLight createPointLight (Point3f pos, Color3f clr, Point3f atten) {
        PointLight pl = new PointLight(clr, pos, atten);
        pl.setInfluencingBounds(bounds);
        return pl;
    }

}
