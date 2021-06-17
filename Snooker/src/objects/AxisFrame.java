package objects;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Geometry;
import org.jogamp.java3d.LineArray;
import org.jogamp.java3d.LineAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;

/**
 * An axis frame is a 3D shape.
 * Three arms poke out from the origin along each
 * axis, from 0.0 to +1.0.
 * Red, green, blue represents the x, y, z axis
 */
public class AxisFrame extends Shape3D {

    /**
     * Default and only constructor
     */
    public AxisFrame() {
        super(createGeometry(), createAppearance());
    }
    
    /**
     * Private static method for creating the geometry of an axis frame
     * @return new geometry object for an axis frame
     */
    private static Geometry createGeometry () {
        int format = LineArray.COORDINATES | LineArray.COLOR_3;
        LineArray geom = new LineArray(6, format);
        Point3f origin = new Point3f();
        geom.setCoordinate(0, origin);
        geom.setCoordinate(2, origin);
        geom.setCoordinate(4, origin);
        Color3f red = new Color3f(1, 0, 0);
        Color3f green = new Color3f(0, 1, 0);
        Color3f blue = new Color3f(0, 0, 1);
        geom.setCoordinate(1, new Point3f(1,0,0));
        geom.setColor(0, red); geom.setColor(1, red);
        geom.setCoordinate(3, new Point3f(0,1,0));
        geom.setColor(2, green); geom.setColor(3, green);
        geom.setCoordinate(5, new Point3f(0,0,1));
        geom.setColor(4, blue); geom.setColor(5, blue);
        return geom;
    }
    
    /**
     * Private static method for creating the appearance object for an axis frame
     * @return new appearance object for an axis frame
     */
    private static Appearance createAppearance () {
        Appearance app = new Appearance();
        LineAttributes latt = new LineAttributes(
            2, // Line width
            LineAttributes.PATTERN_SOLID, // Line pattern
            true // Line anti-aliasing
        );
        app.setLineAttributes(latt);
        return app;
    }

}
