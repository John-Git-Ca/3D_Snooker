package objects;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.Switch;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Vector3d;

/**
 * When constructed, returns a pool ball object.
 * Which is a TransformGroup which contains a sphere and
 * fields for computing physics.
 */
public class PoolBall extends TransformGroup {
    /** The Y-up value that all balls rest ON, not at */
    public static final float height = PoolTable.surfaceHeight;
    /** The radius of each pool ball */
    public static final float radius = 0.02625f;
    /** The y-up position for pool balls to spawn and rest AT */
    public static final float yPos = height + radius;
    /** Equal to {@link #radius} squared */
    public static final float radius2 = radius * radius;
    /** Drag coefficient for slowing down pool balls, spd*dragCo per frame */
    public static final double dragCo = 0.985;
    /** Drag constant for slowing down pool balls, units per frame */
    public static final double dragCa = 0.00001;
    /** A speed below this limit is considered neglibile and should be set to 0 */
    public static final double spdLimit = PoolBall.dragCa / PoolBall.dragCo;
    /** Equal to {@link #spdLimit} squared */
    public static final double spdLimit2 = spdLimit * spdLimit;
    /** The transform that translates this ball to it's position */
    private Transform3D t;
    /** Switch to allow the changing of the poolball shape */
    private Switch sw;
    /** Number of points this ball is worth */ 
    private int pointValue;
    /** Colour of the sphere for this ball */
    private Color3f clr;
    /** The position this pool ball was previously at, last frame */
    private Vector3d prevPos;
    /** Worldspace position of this ball */
    private Vector3d pos;
    /** The velocity of this ball in units per frame, or 16ms*/
    private Vector3d vel;
    /** Is true if the ball is currently translating position */
    private boolean inMotion;
    
    /**
     * Enum for every different type of Snooker ball
     * <p>
     * Used in the PoolBall constructor.<br>
     * Simply pass one of the static fields.
     * @see PoolBall#PoolBall(Type, double, double)
     */
    public static class Type {
        /** Number of points this type of pool ball is worth */
        private int pointValue;
        /** Colour of this type of pool ball */
        private Color3f colour;
        
        /**
         * Private constructor for initializing the enum
         * @param p Point value of the pool ball type
         * @param c Colour of the pool ball type
         */
        private Type (int p, Color3f c) {
            this.pointValue = p;
            this.colour = c;
        }
        
        /** Not worth any points when scored and coloured white */
        public static Type CUE    = new Type(0, new Color3f(0.875f, 0.875f,  0.75f  ));
        /** There's sixteen of these balls, worth 1 point each */
        public static Type RED    = new Type(1, new Color3f(0.875f, 0.0625f, 0.0625f));
        /** There's one yellow ball worth 2 points */
        public static Type YELLOW = new Type(2, new Color3f(0.75f,  0.75f,   0.125f ));
        /** There's one green ball worth 3 points */
        public static Type GREEN  = new Type(3, new Color3f(0.125f, 0.5f,    0.125f ));
        /** There's one brown ball worth 4 points */
        public static Type BROWN  = new Type(4, new Color3f(0.375f, 0.1875f, 0.0625f));
        /** There's one blue ball worth 5 points */
        public static Type BLUE   = new Type(5, new Color3f(0.125f, 0.125f,  0.875f ));
        /** There's one pink ball worth 6 points */
        public static Type PINK   = new Type(6, new Color3f(0.875f, 0.375f,  0.625f ));
        /** There's one black ball worth 7 points */
        public static Type BLACK  = new Type(7, new Color3f(0.125f, 0.125f,  0.125f ));
    }

    /**
     * Default and only constructor for a pool ball
     * @param type The type of Snooker pool ball. Use the enum {@link Type}
     * @param x x-right position of this ball to spawn and rest at
     * @param z z-forward position of this ball to spawn and rest at
     */
    public PoolBall (Type type, double x, double z) {
        super();
        super.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        super.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        this.pointValue = type.pointValue;
        this.clr = type.colour;
        this.t = new Transform3D();
        this.prevPos = new Vector3d(x, yPos, z);
        this.pos = new Vector3d(x, yPos, z);
        this.vel = new Vector3d();
        this.inMotion = false;
        this.t.setTranslation(this.pos);
        this.sw = new Switch();
        this.sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
        this.sw.setCapability(Switch.ALLOW_CHILDREN_WRITE);
        super.setTransform(this.t);
        Appearance app = createBallAppearance(clr);
        Sphere sphere = new Sphere(
            PoolBall.radius, // Radius of the ball
            Sphere.GENERATE_NORMALS, // Capability flags
            128, // Fidelity of sphere, number of polygons
            app // The appearance object
        );
        Cylinder cylinder = new Cylinder(
            PoolBall.radius, // Radius of cylinder
            PoolBall.radius, // Length of cylinder
            Cylinder.GENERATE_NORMALS, // capability flags
            app // appearance of object
        );
        super.addChild(this.sw);
        this.sw.addChild(sphere);
        this.sw.addChild(cylinder);
        this.sw.setWhichChild(0);
    }
    
    /**
     * private static method that creates an Appearance for this ball of the given colour.
     * Only used in the constructor.
     * @param clr The colour to set the new appearance to
     * @return The new Appearance object, to make our sphere with
     */
    private static Appearance createBallAppearance (Color3f clr) {
        Appearance app = new Appearance();
        app.setMaterial(new Material(
            clr, // Ambient colour
            new Color3f(0.0f, 0.0f, 0.0f), // Emissive colour
            clr, // Diffuse colour
            new Color3f(0.9f, 0.9f, 0.9f), // Specular colour
            128 // Shininess
        ));
        return app;
    }
        
    /**
     * Returns the number of points this ball is worth
     * @return Integer value of this ball
     */
    public int getPointValue () {
        return this.pointValue;
    }
    
    /**
     * Returns the colour of this ball
     * @return Color3f of this ball
     */
    public Color3f getColour () {
        return this.clr;
    }

    /**
     * Returns the x position this
     * pool ball was previously at
     * @return Previous x position of this pool ball
     */
    public double getPrevPosX () {
        return this.prevPos.getX();
    }

    /**
     * Returns the z position this
     * pool ball was previously at
     * @return Previous z position of this pool ball
     */
    public double getPrevPosZ () {
        return this.prevPos.getZ();
    }

    /**
     * Returns the x position of this pool ball, <br>
     * and therefore the x component translation of this pool ball.
     * @return x position of this pool ball
     */
    public double getPosX () {
        return this.pos.getX();
    }

    /**
     * Returns the z position of this pool ball, <br>
     * and therefore the z component translation of this pool ball.
     * @return z position of this pool ball
     */
    public double getPosZ () {
        return this.pos.getZ();
    }

    /**
     * Returns a new vector of this pool ball's position, <br>
     * and therefore the translation of this pool ball.
     * @return New vector position of this pool ball's position
     */
    public Vector3d getPos () {
        return new Vector3d(this.getPosX(), PoolBall.yPos, this.getPosZ());
    }

    /**
     * Sets the position and therefore translation of this pool ball, <br>
     * and updates the transform this pool ball belongs to.
     * @param x The new x position of this pool ball
     * @param z The new z position of this pool ball
     */
    public void setPos (double x, double z) {
        this.prevPos.set(this.pos);
        this.pos.set(x, PoolBall.yPos, z);
        this.t.setTranslation(this.pos);
        super.setTransform(this.t);
    }

    /**
     * Returns the x velocity of this pool ball
     * @return The x velocity of this pool ball
     */
    public double getVelX () {
        return this.vel.getX();
    }

     /**
     * Returns the z velocity of this pool ball
     * @return The z velocity of this pool ball
     */
    public double getVelZ () {
        return this.vel.getZ();
    }

    /**
     * Sets the velocity of this pool ball, <br>
     * and updates {@link #inMotion} appropriately.
     * @param x The new x velocity of this pool ball
     * @param z The new z velocity of this pool ball
     */
    public void setVel (double x, double z) {
        this.vel.set(x, 0, z);
        if (vel.x*vel.x+vel.z*vel.z < PoolBall.spdLimit2) {
            this.stop();
        } else this.inMotion = true;
    }

    /**
     * Sets this pool ball's velocity to
     * zero and sets inMotion to false.
     */
    public void stop () {
        this.vel.set(0,0,0);
        this.inMotion = false;
    }

    /**
     * Returns the speed of this pool ball, squared
     * @return The speed squared of this pool ball
     */
    public double getSpd2 () {
        return vel.x*vel.x+vel.z*vel.z;
    }

    /**
     * Returns the speed of this pool ball
     * @return The speed of this pool ball
     */
    public double getSpd () {
        return Math.sqrt(getSpd2());
    }

    /**
     * Returns whether this pool ball is in motion, <br>
     * or, has a non-negligible velocity.
     * @return True if the ball is moving
     */
    public boolean isInMotion () {
        return this.inMotion;
    }

    /**
     * Swaps the pool ball shape to either sphere or cylinder
     */
    public void swapShapes () {
        this.sw.setWhichChild(
            (this.sw.getWhichChild()>0)?0:1
        );
    }

}
