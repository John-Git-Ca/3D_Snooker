package objects;

import java.awt.Font;
import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Font3D;
import org.jogamp.java3d.FontExtrusion;
import org.jogamp.java3d.QuadArray;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Text3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

import appearances.MaterialFactory;
import appearances.TexturedAppearance;

/**
 * A score board object to place in your scene
 */
public class Scoreboard extends TransformGroup {
    /** The transform groups for each dial */
    private TransformGroup[] dials;
    /** The current value of each digit. <br>
     * Digits 2,1,0 are player 1 while digits 5,4,3 are player 2 */
    private int [] digitValues;
    /** The angle between each symbol on a dial */
    private static final float da = (float) (Math.PI / 5.0);
    /** The rotation interpolators for each digit */
    private RotationInterpolator[] rerps ;

    /**
     * Only constructor. Only needs a position vector. <br>
     * Creates a fricken time machine that you can increment the score of.
     * @param p The position the centre of this machine should be at
     */
    public Scoreboard (Vector3d p) {
        // Create our main parent transform group
        super();
        Transform3D t = new Transform3D();
        t.setTranslation(p);
        super.setTransform(t);
        // Initialize the digits
        this.digitValues = new int[6];
        for(int i=0;i<6;i++) this.digitValues[i]=0;
        this.dials = new TransformGroup[6];
        createBoard();
        this.rerps = new RotationInterpolator[6];
        for (int i=0; i<6; i++) {
            this.rerps[i]=createRerp(this.dials[i]);
            this.dials[i].addChild(this.rerps[i]);
        }
        
    }

    /**
     * Scores a point for player 1. <p>
     * Increases the score for player 1 by 1,
     * by rotating the dial to the next digit and
     * carrying when necessary.
     */
    public void scoreP1 () {
        changeRerp(2);
        if(digitValues[2] == 0) {
            changeRerp(1);
            if(digitValues[1] == 0) {
                changeRerp(0);
            }
        }
    }

    /**
     * Scores a point for player 2. <p>
     * Increases the score for player 2 by 1,
     * by rotating the dial to the next digit and
     * carrying when necessary.
     */
    public void scoreP2 () {
        changeRerp(5);
        if(digitValues[5] == 0) {
            changeRerp(4);
            if(digitValues[4] == 0) {
                changeRerp(3);
            }
        }
    }

    /**
     * Updates the rotation interpolator at the given
     * index to rotate to the next digit for the dial
     * of the same index.
     * @param index The dial index to increment
     */
    private void changeRerp (int index) {
        RotationInterpolator rerp = this.rerps[index];      // Get rotation interpolator in question
        float curAngle = getCurrentAngle(rerp);             // Get the current angle of the rotation interpolator
        int i = this.digitValues[index] + 1 ;               // Increment the digit value
        this.digitValues[index] = i % 10;                   // Update the new value of the digit, clamping it below 10
        float targetAngle = i * da;                         // The angle we are aiming to rotate to
        if (targetAngle - curAngle > Math.PI)               // If turning more than 180 deg to the right...
            targetAngle -= 2*Math.PI;                       // Then rotate in the other direction instead
        if (targetAngle - curAngle < -Math.PI)              // If turning more than 180 to the left...
            targetAngle += 2*Math.PI;                       // Then rotate in the other directio instead
        rerp.setMinimumAngle(curAngle);                     // Set min to current
        rerp.setMaximumAngle(targetAngle);                  // Set max to target
        Alpha alpha = new Alpha(1, 800l);                   // Create a new alpha, 800ms long
        alpha.setStartTime(System.currentTimeMillis());     // Set the new start time to now, start rotating now
        rerp.setAlpha(alpha);                               // Set the alpha for the rerp
    }

    /**
     * Calculates the angle the given rotation interpolator
     * is currently at, based on it's alpha.
     * @param r The rotation interpolator to check
     * @return The current angle of the rerp: [0.0, 2pi)
     */
    private static float getCurrentAngle (RotationInterpolator r) {
        float min = r.getMinimumAngle();        // The current minimum angle
        float dif = r.getMaximumAngle() - min;  // The change of angle, angle from min to max
        float progress = r.getAlpha().value();  // 0.0 -> 1.0 progress of the alpha
        float curAngle = min + progress*dif;    // The current angle the dial is at
        return (float)(curAngle % (2*Math.PI)); // Clamp answer between 0.0 -> 2pi
    }

    /**
     * Creates a rotation interpolator with blank values
     * for the given transform group.
     * @param target The target transformgroup to rotate
     * @return The newly created rotation interpolator
     */
    private RotationInterpolator createRerp (TransformGroup target) {
        Transform3D yAxis = new Transform3D();
        yAxis.rotZ(Math.PI/2);
        Alpha rotationAlpha = new Alpha(1, 0, 0, 800, 200, 0); // Rotate for 800ms, accelerating for 200ms at the beginning
        RotationInterpolator rot_beh = new RotationInterpolator(rotationAlpha, target, yAxis, 0, 0);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        rot_beh.setSchedulingBounds(bounds);
        rot_beh.setEnable(true);
        return rot_beh;
    }
    
    /**
     * Creates the the whole thing assigns the
     * references to the appropriate members.
     */
    private void createBoard() {
        Transform3D back3D = new Transform3D();
        back3D.setTranslation(new Vector3f(-0.09f, -0.41f, 0.6f));
        TransformGroup backTG = new TransformGroup(back3D);
        super.addChild(backTG);
        backTG.addChild(createBoard(4));
        Transform3D edgeT3D = new Transform3D();
        edgeT3D.setTranslation(new Vector3f(0.2f, 4.6f, 0f));
        TransformGroup edgeTG = new TransformGroup();
        backTG.addChild(edgeTG);
        edgeTG.addChild(edge(1));
        edgeTG.addChild(createText3D("Player1     Player2", new Color3f(1.0f, 1.0f, 1.0f)));

        TransformGroup numberTG = new TransformGroup();
        super.addChild(numberTG);
        Transform3D[] transform3ds = new Transform3D[6];
        TransformGroup[] sceneTG = new TransformGroup[6];	
        
        for(int i=0; i<6; i++ ) {
            float x = (float) (-1+i*0.3);
            if(i>2)
                x = x + 0.2f;
            transform3ds[i] = new Transform3D();
            transform3ds[i].setTranslation(new Vector3f(x, 0f, 0f));
            sceneTG[i] = new TransformGroup(transform3ds[i]);
            if(i<3)
                sceneTG[i].addChild(createDial(new Color3f(1.0f, 0.0f, 0.0f)));
            else 
                sceneTG[i].addChild(createDial(new Color3f(0.0f, 0.0f, 1.0f)));				
            dials[i] = new TransformGroup();
            dials[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            dials[i].addChild(sceneTG[i]);
            numberTG.addChild(dials[i]);
        }
    }
    
    /**
     * Creates a dial object for use in our score board. <br>
     * The dial is a 10 sided polygon with digit symbols on each side. <br>
     * Check {@link #createSide(Color3f)} to figure out the dimensions of the shape.
     * @param bgclr The colour of the dial, not the digit symbol
     * @return A new transform group containing the digits and sides
     */
    private static TransformGroup createDial(Color3f bgclr) {
        TransformGroup number = new TransformGroup();       // The main transform group for 
        Transform3D[] transform3ds = new Transform3D[10];   // A transform for each digit symbol, (0-9)
        TransformGroup[] sceneTG = new TransformGroup[10];  // A transform group for each digit symbol, (0-9)
        Color3f digitClr = new Color3f(1.0f, 1.0f, 1.0f);   // The colour of each digit symbol
        Vector3f offset = new Vector3f(0, 0f, 0.61f);       // Offset to keep the digit symbol centred
        Transform3D t = new Transform3D();                  // Create the translation transform
        t.setTranslation(offset);                           // Set the translation
        for (int i = 0 ; i < 10 ; i++) {                    // Create each digit symbol and put it on the dial
            transform3ds[i] = new Transform3D();            // Create a new transform to put the digit symbol in place
            transform3ds[i].rotX(i*da);                     // Rotate to the right angle
            transform3ds[i].mul(t);                         // But not before translating it into place
            sceneTG[i] = new TransformGroup(transform3ds[i]);
            sceneTG[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            sceneTG[i].addChild(createSide(bgclr)); // background for each digit
            sceneTG[i].addChild(createText3D(""+i, digitClr));
            number.addChild(sceneTG[i]);
        }
        return number;
    }

    /**
     * Static method to create 3D text of the given text and colour. <p>
     * The text should be roughly centred, really flat, and 0.2 tall.
     * @param text The string the text should say
     * @param clr The colour of the text
     * @return The new transform group containing the text. Don't change it's transform.
     */
    private static TransformGroup createText3D(String text, Color3f clr) {
        Font my2DFont = new Font(text, Font.PLAIN, 1); //create 3d font
        FontExtrusion myExtru = new FontExtrusion();
        Font3D my3DFont = new Font3D(my2DFont, myExtru);// create 3d font
        Appearance app = new Appearance();				//create new appearance
        app.setColoringAttributes(new ColoringAttributes(clr, 1));		//set the color of the text according to the argument clr

        Text3D text1 = new Text3D(my3DFont, text, new Point3f(), 0, 1); // make 3d text
        // Scale and translate the text
        Transform3D scaler = new Transform3D(); scaler.setScale(new Vector3d(0.2, 0.2, 1.0/(1<<8)));
        Transform3D trans = new Transform3D(); trans.setTranslation(new Vector3f(0, -0.1f, 0));
        trans.mul(scaler);
        // Add the new text and transform to the transform group 
        TransformGroup ts = new TransformGroup(trans);
        ts.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ts.addChild(new Shape3D(text1, app));
        return ts;		
    }

    /**
     * Creates a rectangle of ratio 1:2 for making
     * the 10 sided polygon that is the dial.
     * @param clr The colour of the rectangle.
     * @return The newly created shape3d of the rectangle
     */
    private static Shape3D createSide(Color3f clr) {
        QuadArray square = new QuadArray(4, QuadArray.NORMALS | QuadArray.COORDINATES); //quadArray to define one side
        Point3f[] pt1 = {new Point3f(0.1f, 0.2f, 0), new Point3f(-0.1f, 0.2f, 0), new Point3f(-0.1f, -0.2f, 0), new Point3f(0.1f, -0.2f, 0)};
        float[] normal = {0, 0, 1};
        for(int i = 0; i<4; i++) {
            square.setCoordinate(i, pt1[i]);//set coordinates
            square.setNormal(i, normal);	//set surface normal
        }
        Appearance app = new Appearance();
        app.setMaterial(MaterialFactory.createMaterial(clr));
        return new Shape3D(square, app);
    }

    /**
     * Creates the inner margin of the backboard
     * @param factor A multiplier to scale the size
     * @return The new transformgroup containing the new shape
     */
    private static TransformGroup createBoard(float factor) {
        QuadArray square = new QuadArray(4, QuadArray.NORMALS | QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2); //quadArray to define one side
        Point3f[] pt1 = {new Point3f(0.29f, 0f, 0.0f), new Point3f(0.29f, 0.21f, 0.0f), new Point3f(-0.29f, 0.21f, 0.0f), new Point3f(-0.29f, 0f, 0.0f)};
        float[] normal = {0, 0, 1};
        
        for(int i=0; i<4; i++) {
            pt1[i].x = pt1[i].x * factor;
            pt1[i].y = pt1[i].y * factor;
            pt1[i].z = pt1[i].z * factor;
        }
        
        float uv0[] = {0f, 0f};
        float uv1[] = {1f, 0f};
        float uv2[] = {1f, 1f};
        float uv3[] = {0f, 1f};
        
        for(int i = 0; i<4; i++) {
            square.setCoordinate(i, pt1[i]);//set coordinates
            square.setNormal(i, normal);	//set surface normal
        }
        square.setTextureCoordinate(0, 0, uv0);
        square.setTextureCoordinate(0, 1, uv1);
        square.setTextureCoordinate(0, 2, uv2);
        square.setTextureCoordinate(0, 3, uv3);		
        
        Appearance app = new TexturedAppearance("ledscreen.png");
        TransformGroup board = new TransformGroup();
        board.addChild(new Shape3D(square, app));
        return board;
    }

    /**
     * Creates the outer padding of the backboard
     * @param factor Scalar multiplier to scale the size
     * @return The new transformgroup containing the new shape
     */
    private static TransformGroup edge(float factor) {
        QuadArray square = new QuadArray(4, QuadArray.NORMALS | QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2); //quadArray to define one side
        
        Point3f[] pts = new Point3f[4];
        pts[0] = new Point3f(1.4f, 1.18f, -0.1f);
        pts[1] = new Point3f(-1.4f, 1.18f, -0.1f);
        pts[2] = new Point3f(-1.4f, -0.2f, -0.1f);
        pts[3] = new Point3f(1.4f, -0.2f, -0.1f);
        float[] normal = {0, 0, 1};
        
        for(int i=0; i<4; i++) {
            pts[i].x = pts[i].x * factor;
            pts[i].y = pts[i].y * factor;
            pts[i].z = pts[i].z * factor;
        }
        
        float uv0[] = {0f, 0f};
        float uv1[] = {1f, 0f};
        float uv2[] = {1f, 1f};
        float uv3[] = {0f, 1f};
        
        for(int i = 0; i<4; i++) {
            square.setCoordinate(i, pts[i]);//set coordinates
            square.setNormal(i, normal);	//set surface normal
        }
        square.setTextureCoordinate(0, 0, uv0);
        square.setTextureCoordinate(0, 1, uv1);
        square.setTextureCoordinate(0, 2, uv2);
        square.setTextureCoordinate(0, 3, uv3);		
        
        Appearance app = new TexturedAppearance("edge.png");
        TransformGroup board = new TransformGroup();
        board.addChild(new Shape3D(square, app));
        
        return board;
    }
}