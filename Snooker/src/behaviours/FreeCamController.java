package behaviours;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.vecmath.Matrix3d;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Quat4d;
import org.jogamp.vecmath.Vector3d;
import jpanels.BasicView;

/**
 * Allows the moving and rotating of a ViewTransform, AKA a camera.
 * <h3> Controls </h3>
 * <ul>
 * <li> Use W and S to move forward and back
 * <li> Use A and D to move left and right
 * <li> Use Space and Shift to move up and down
 * <li> Drag the mouse to rotate the camera
 * </ul>
 * <h3> Implementing </h3>
 * Create this object, passing in the BasicView object. <br>
 * Then add this to the content branch. <br>
 * It will add itself to the KeyListener and MouseListener list.
 */
public class FreeCamController extends Behavior
implements
java.awt.event.KeyListener,
java.awt.event.MouseMotionListener,
java.awt.event.MouseListener
{
    /** Units per second speed of the camera when translating */
    public static final double movSpeed = 2.0;
    /** To run the behaviour on every frame */
    private static WakeupCriterion wakecon = new WakeupOnElapsedFrames(0);
    /** PI divided by two, save that math */
    private static double PI_2 = Math.PI/2.0;
    /** The target TransformGroup, should be the ViewTransform */
    protected TransformGroup targetTG;
    /** The target Transform3D, should be the ViewTransform's transform */
    protected Transform3D targetT;
    /** The set of all keys currently held down */
    protected HashSet<Integer> keys;
    /** Unit vectors for the directions of the camera */
    protected Vector3d forward, right, up;
    /** Current yaw rotation of the camera */
    protected double viewYaw;
    /** Current pitch rotation of the camera */
    protected double viewPitch;
    /** Current position and therefore translation of the camera, in global worldspace */
    protected Vector3d viewPos;
    /** x position of mouse last frame, used when mouse dragging */
    private int lastMX;
    /** y position of mouse last frame, used when mouse dragging */
    private int lastMY;

    /**
     * The one and only constructor, pass in the BasicView
     * or some other subclass for it to control.
     * @param view The BasicView for which to control
     */
    public FreeCamController (BasicView view) {
        super();
        this.keys = new HashSet<>(4);
        this.targetTG = view.getViewTransformGroup();
        view.getCanvas().addKeyListener(this);
        view.getCanvas().addMouseMotionListener(this);
        view.getCanvas().addMouseListener(this);
        this.targetT = new Transform3D();
        this.targetTG.getTransform(this.targetT);
        Quat4d q = new Quat4d();
        this.viewPos = new Vector3d();
        this.targetT.get(q, this.viewPos);
        this.viewYaw = Math.atan2(2*q.y*q.w-2*q.x*q.z , 1 - 2*q.y*q.y - 2*q.z*q.z);
        this.viewPitch = -Math.asin(2.0 * (q.w*q.y + q.x*q.z));
        this.forward = new Vector3d(0, 0, 1);
        this.right = new Vector3d(1, 0, 0);
        this.up = new Vector3d(0, 1, 0);
        this.lastMX = 0;
        this.lastMY = 0;
        updateDirs();
        updateTargetTG();
        BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
        super.setSchedulingBounds(bounds);
        super.setEnable(true);

    }
    
    /**
     * Gets the current position of the camera
     * @return The current position of the camera
     */
    public Vector3d getPos () {return this.viewPos;}
    
    /**
     * Gets the current yaw rotation of the camera
     * @return The current yaw rotation of the camera in radians
     */
    public double getYaw () {return this.viewYaw;}
    
    /**
     * Gets the current pitch rotation of the camera
     * @return The current pitch rotation of the camera in radians
     */
    public double getPitch () {return this.viewPitch;}
    
    /**
     * Gets the forward direction of the camera
     * @return Forward facing unit vector 
     */
    public Vector3d getForward () {return this.forward;}
    
    /**
     * Gets the upwards direction of the camera
     * @return Up facing unit vector
     */
    public Vector3d getUp () {return this.up;}
    
    /**
     * Gets the rightwards direction of the camera
     * @return Right facing unit vector
     */
    public Vector3d getRight () {return this.right;}

    @Override
    public void initialize() {
        super.wakeupOn(wakecon);
    }
    
    /**
     * Updates the ViewTransform to the current position and angle
     */
    protected void updateTargetTG () {
        Matrix3d m1=new Matrix3d(), m2=new Matrix3d();
        m1.rotY(viewYaw); m2.rotX(viewPitch); m1.mul(m2);
        targetT.setRotation(m1);
        targetT.setTranslation(viewPos);
        targetTG.setTransform(targetT);
    }
    
    /**
     * Updates the unit vector directions to the current angles
     */
    protected void updateDirs () {
        forward.set(-Math.sin(viewYaw)*Math.cos(viewPitch), -Math.sin(-viewPitch), -Math.cos(viewYaw)*Math.cos(viewPitch));
        right.set(Math.cos(-viewYaw), 0, Math.sin(-viewYaw));
        up.cross(right, forward);
    }

    /**
     * Checks key presses and updates the ViewTransform
     */
    @Override
    public void processStimulus(Iterator<WakeupCriterion> arg0) {
        handleKeyInput(); // Change vectors
        updateTargetTG(); // Apply changes
        super.wakeupOn(wakecon);
    }

    /** Checks against {@link #keys} and moves {@link #viewPos} accordingly */
    protected void handleKeyInput () {
        // Time difference in seconds
        double dt = 0.016;
        // Check movement
        if (keys.contains(KeyEvent.VK_W)) viewPos.scaleAdd(+movSpeed*dt, forward, viewPos);
        if (keys.contains(KeyEvent.VK_A)) viewPos.scaleAdd(-movSpeed*dt, right, viewPos);
        if (keys.contains(KeyEvent.VK_D)) viewPos.scaleAdd(+movSpeed*dt, right, viewPos);
        if (keys.contains(KeyEvent.VK_S)) viewPos.scaleAdd(-movSpeed*dt, forward, viewPos);
        if (keys.contains(KeyEvent.VK_SPACE)) viewPos.scaleAdd(+movSpeed*dt, up, viewPos);
        if (keys.contains(KeyEvent.VK_SHIFT)) viewPos.scaleAdd(-movSpeed*dt, up, viewPos);
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent arg0) {
        this.keys.add(arg0.getKeyCode());
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent arg0) {
        this.keys.remove(arg0.getKeyCode());
    }

    @Override
    public void keyTyped(java.awt.event.KeyEvent arg0) {}
    
    @Override
    public void mouseDragged(java.awt.event.MouseEvent arg0) {
        int difX = this.lastMX - arg0.getX();
        int difY = this.lastMY - arg0.getY();
        this.lastMX = arg0.getX();
        this.lastMY = arg0.getY();
        // Moving across half the screen rotates 45deg or pi/4 radians
        viewYaw += PI_2 * difX / arg0.getComponent().getWidth();
        viewPitch += PI_2 * difY / arg0.getComponent().getHeight();
        updateDirs();
    }

    @Override
    public void mouseMoved(java.awt.event.MouseEvent arg0) {}

    @Override
    public void mouseClicked(MouseEvent arg0) {}

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent arg0) {
        this.lastMX = arg0.getX();
        this.lastMY = arg0.getY();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {}

}
