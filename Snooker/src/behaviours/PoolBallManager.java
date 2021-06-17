package behaviours;

import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector2f;

import misc.SoundPlayer;
import objects.PoolBall;
import objects.PoolTable;
import objects.PoolBall.Type;

/**
 * Class for spawning, interacting, and colliding pool balls. <p>
 * Use {@link #getTG()} to get the transform group that
 * contains all the pool balls. The origin of the tg is the
 * centre of the pool table, where +z is towards the baulk line.
 */
public class PoolBallManager extends Behavior {
    /** Width of the table in metres */
    public static final double width = PoolTable.tableWidth_2*2;
    /** Equal to {@link #width} divided by 2 */
    public static final double width_2 = width / 2.0 ;
    /** Length of the table in metres */
    public static final double length = PoolTable.tableLength_2*2;
    /** Equal to {@link #length} divided by 2 */
    public static final double length_2 = length / 2.0 ;
    /** Radius of the corner pocket */
    public static final double pocketRadius = PoolTable.pocketRadius;
    /** The distance between the centre of the side pocket and the side of the pool table */
    public static final double sideDif = 0;//Math.sqrt(28 * PoolBall.radius2);
    public static final Vector2f[] pockets = PoolTable.pocketCoords;
    /** static wake up criterion to run on tick, (every frame) */
    private static WakeupCriterion WC_onTick = new WakeupOnElapsedFrames(0);
    /** Array of all 22 pool balls to iterate over.<br>
     * Each ball should be at a certain index. <br>
     * Some pool balls may be null as they get deleted when scored.
     * <p>
     * 0 = Cue ball <br>
     * 1 = Black ball <br>
     * 2 = Pink ball <br>
     * 3 = Blue ball <br>
     * 4 = Brown ball <br>
     * 5 = Green ball <br>
     * 6 = Yellow ball <br>
     * 7-21 = 15 Red balls
     * */
    private PoolBall [] poolballs;

    /** The transform group that contains all the pool balls. <br>
     * The origin of this transform group is the centre of the table. */
    private TransformGroup tg;

    public PoolBallManager() {
        this.poolballs = new PoolBall [22];
        this.tg = new TransformGroup();
        this.tg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE|TransformGroup.ALLOW_CHILDREN_EXTEND);
        double baulkLine = length * 0.3;
        double Dradius = width / 6;
        addBall(0, Type.CUE, Dradius/-2, baulkLine+Dradius/4);
        addBall(1, Type.BLACK, 0, length * -0.409090909);
        addBall(2, Type.PINK, 0, length / -4);
        addBall(3, Type.BLUE, 0, 0);
        addBall(4, Type.BROWN, 0, baulkLine);
        addBall(5, Type.GREEN, Dradius, baulkLine);
        addBall(6, Type.YELLOW, -Dradius, baulkLine);
        addPyramid(7, 5, Type.RED, 0, length/-4 - 2*PoolBall.radius);

        BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
        super.setSchedulingBounds(bounds);
        super.setEnable(true);
    }

    /**
     * Returns the transform group that stores all the pool balls. <p>
     * The origin is the centre of the pool table where +z is towards
     * the baulk line and +x is to the right of that direction.
     * @return The TransformGroup that contains all the pool balls
     */
    public TransformGroup getTG () {
        return this.tg;
    }
    
    /**
     * Adds multiple pool balls in a equilateral pyramid shape at the given coord. <br>
     * It will always grow the pyramid towards -z.
     * @param index The index to start from
     * @param layers The number of layers in the pyramid
     * @param type The type of pool ball to spawn
     * @param x The x-coord of the first pool ball, at the top of the pyramid
     * @param z The z-coord of the first pool ball, at the top of the pyramid
     */
    private void addPyramid (int index, int layers, Type type, double x, double z) {
        double difWidth = PoolBall.radius*1.1;
        double difHeight = Math.sin(Math.PI/3) * PoolBall.radius*2.2;
        for (int i=0; i<layers; i++) {
            for (int j=0; j<=i; j++) 
                addBall(index++, type, x+difWidth*2*j, z);
            x -= difWidth;
            z -= difHeight;
        }
    }
    
    /**
     * Shortcut function for adding pool balls for the constructor
     * @param i Index to add to
     * @param type {@link PoolBall.Type} of pool ball to add
     * @param x x position to set to
     * @param z y position to set to
     */
    private void addBall (int i, Type type, double x, double z) {
        this.poolballs[i] = new PoolBall(type, x, z);
        this.tg.addChild(this.poolballs[i]);
    }

    @Override
    public void initialize() {
        super.wakeupOn(WC_onTick);
    }
    
    /**
     * Starts moving the cue ball in the given direction at the given power. <p>
     * You're fine to pass a negative power to strike the ball backwards.
     * @param angle The x-z plane angle to aim at, in radians, where 0 aims towards +z
     * @param power The speed at which the ball should move, in j3d units per second
     */
    public void strikeCueBall (float angle, float power) {
        PoolBall cue = this.poolballs[0];
        if (cue == null) {
            System.err.println("Tried to strike a cue ball that doesn't exist");
        } else
        cue.setVel(Math.sin(angle)*power, Math.cos(angle)*power);
    }
    
    /**
     * Moves all the pool balls based on their velocity. <br>
     * Slows them down based on a drag coefficent. <br>
     * Does NOT detect collisions.
     */
    public void movePoolBalls () {
        for (int i=0; i<22; i++) {
            PoolBall pb = this.poolballs[i];
            if (pb != null && pb.isInMotion()) {
                checkPocketSink(i);
                checkWallCollision(pb);
            }
        }
        // Check ball collisions
        for (int i=0; i<21; i++) {
            PoolBall pb1 = this.poolballs[i];
            if (pb1 != null)
                for (int j=i+1; j<22; j++) {
                    PoolBall pb2 = this.poolballs[j];
                    if (pb2 != null)
                        checkCollision(pb1, pb2);
                }
        }
    }

    /**
     * Checks if a pool ball is within a pocket
     * and scores it if so. Takes an index so it
     * can modifiy the {@link #poolballs} array.
     * @param index The index the pool ball is at
     */
    private void checkPocketSink (int index) {
        PoolBall p = this.poolballs[index];
        for (int i=0; i<6; i++) { // Lazy check every pocket
            double difx = pockets[i].getX() - p.getPosX();
            double difz = pockets[i].getY() - p.getPosZ();
            if (difx*difx+difz*difz < pocketRadius*pocketRadius) {
                System.out.println("Scored "+p.getPointValue()+" points!");
                p.stop();
                p.setPos(1<<8, 1<<8);
                this.poolballs[index] = null;
                break;
            }
        }
    }

    /**
     * Checks if a pool ball is about to collide with
     * a wall and calculates the bounce.
     * @param p The pool ball to check
     */
    private void checkWallCollision (PoolBall p) {
        // Calculate raw new values
        double newX = p.getPosX() + p.getVelX();
        double newZ = p.getPosZ() + p.getVelZ();
        double newDX = p.getVelX() * PoolBall.dragCo;
        double newDZ = p.getVelZ() * PoolBall.dragCo;
        if (newDX > 0) { // Check right side wall collision
            double dif = newX + PoolBall.radius - width_2 ; // Distance from right side of pool table to right side of pool ball, + = toward right
            if (dif > 0) { newX -= dif*2; newDX = -newDX; }
            newDX -= PoolBall.dragCa; // Constant value drag
        } else { // Check left side collision
            double dif = newX - PoolBall.radius + width_2 ; // Distance from left side of pool table to left side of pool ball, + = towards right
            if (dif < 0) { newX -= dif*2; newDX = -newDX; }
            newDX += PoolBall.dragCa; // Constant value drag
        }
        if (newDZ > 0) { // Check front side collision
            double dif = newZ + PoolBall.radius - length_2 ; // Distance from front side of pool table to front side of pool ball, + = forwards
            if (dif > 0) { newZ -= dif*2; newDZ = -newDZ; }
            newDZ -= PoolBall.dragCa; // Constant value drag
        } else { // Check back side collision
            double dif = newZ - PoolBall.radius + length_2 ; // Distance form back side of pool table to back side of pool ball, + = forwards
            if (dif < 0) { newZ -= dif*2; newDZ = -newDZ; }
            newDZ += PoolBall.dragCa; // Constant value drag
        }
        p.setPos(newX, newZ);
        p.setVel(newDX, newDZ);
    }



    /**
     * Checks for a collision between the two balls
     * and calculates the collision if one has occured.
     * @param a One of the pool balls, non-null
     * @param b A different pool ball, non-null
     */
    private void checkCollision (PoolBall a, PoolBall b) {
        // Both exist and either or are in motion
        if (a.isInMotion() || b.isInMotion()) {
            double difx = b.getPosX() - a.getPosX() ;
            double difz = b.getPosZ() - a.getPosZ() ;
            if (difx*difx+difz*difz <= PoolBall.radius2*4) {
                // Check if they weren't colliding previously
                // double pdifx = b.getPrevPosX() - a.getPrevPosX() ;
                // double pdifz = b.getPrevPosZ() - a.getPrevPosZ() ;
                // if (pdifx*pdifx+pdifz*pdifz > PoolBall.radius2*4) {
                    // We're colliding for the first time

                    double angle = Math.atan2(difz,difx);
                    double sin = Math.sin(angle), cos = Math.cos(angle);
                    
                    double x1 = 0, z1 = 0;
                    double x2 = difx*cos+difz*sin;
                    double z2 = difz*cos-difx*sin;
                    
                    // rotate velocity
                    double vx1 = a.getVelX()*cos+a.getVelZ()*sin;
                    double vz1 = a.getVelZ()*cos-a.getVelX()*sin;
                    double vx2 = b.getVelX()*cos+b.getVelZ()*sin;
                    double vz2 = b.getVelZ()*cos-b.getVelZ()*sin;
                    
                    // resolve the 1D case
                    double vx1final = vx2 ;
                    double vx2final = vx1 ;
                    vx1 = vx1final;
                    vx2 = vx2final;

                    // Compensate for overlap
                    double absV = Math.abs(vx1)+Math.abs(vx2);
                    double overlap = (PoolBall.radius*2)-Math.abs(x1-x2);
                    x1 += vx1/absV*overlap;
                    x2 += vx2/absV*overlap;

                    // rotate the relative positions back
                    double x1final = x1*cos-z1*sin;
                    double z1final = z1*cos+x1*sin;
                    double x2final = x2*cos-z2*sin;
                    double z2final = z2*cos+x2*sin;

                    // Play collision sound
                    SoundPlayer.playPoolBallColl();
                    
                    // finally compute the new absolute positions
                    a.setPos(
                        a.getPosX() + x1final,
                        a.getPosZ() + z1final
                    );
                    b.setPos(
                        b.getPosX() + x2final,
                        b.getPosZ() + z2final
                    );
                    a.setVel(
                        vx1*cos-vz1*sin,
                        vz1*cos+vx1*sin
                    );
                    b.setVel(
                        vx2*cos-vz2*sin,
                        vz2*cos+vx2*sin
                    );
                //}
            }
        }
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> arg0) {
        movePoolBalls();
        super.wakeupOn(WC_onTick);
    }

    /**
     * Swaps the shapes of the pool balls to either a sphere or a cylinder
     */
    public void swapShapes () {
        for (int i=0; i<22; i++) {
            PoolBall pb = this.poolballs[i];
            if (pb != null) {
                pb.swapShapes();
            }
        }
    }

}
