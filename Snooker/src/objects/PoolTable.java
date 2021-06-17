package objects;

import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;
import appearances.TexturedAppearance;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.geometry.Box;

/**
 * Contains a pool table object. <br>
 * When constructed, returns a TransformGroup containing
 * all the parts of a pool table. Contains no other
 * methods or fields.
 */
public class PoolTable extends TransformGroup {
    /** Half the length of the pool table */
    public static final float tableLength_2 = 3.569f/2;
    /** Half the width of the pool table */
    public static final float tableWidth_2 = 1.778f/2;
    /** Half the thickness of the pool table top board */
    public static final float tableDepth_2 = 0.05f;
    /** The radius of the table legs */
    public static final float legRadius = 0.05f;
    /** The height of the table legs */
    public static final float legHeight = 0.5f;
    /** y-up value where the surface of the table is */
    public static final float surfaceHeight = legHeight + tableDepth_2 ;
    /** Radius of any of the pockets */
    public static final float pocketRadius = (float) (4 * PoolBall.radius / Math.sqrt(2));
    /** Height of the pockets, should be really thin */
    public static final float pocketHeight = 0.001f;
    /** An appearance object containing a felt texure */
    private static Appearance feltApp = new TexturedAppearance("FeltTexture.jpg");
    /** An appearance object containing a wood texture */
    private static Appearance woodApp = new TexturedAppearance("wood.jpg");
    /** An appearance object containing a full black material */
    private static Appearance blackApp = new Appearance();
    static{ blackApp.setColoringAttributes(new ColoringAttributes(new Color3f(), ColoringAttributes.FASTEST)); }
    /** A list of 2D vectors as coords for all the pockets, origin is centre of table. Should be length 6 */
    public static final Vector2f[] pocketCoords = createPocketCoords();

    /**
     * Default constructor. <br>
     * Constructs a new pool table at origin.
     */
    public PoolTable() {
        this(new Vector3f());
    }

    /**
     * Constructor with a position
     * @param pos Position to spawn the pool table at
     */
    public PoolTable (Vector3f pos) {
        super(transFromPos(pos));
        super.addChild(createTable());        
    }

    /**
     * Converts a position vector to a transform with
     * that position as the translation for the transform.
     * @param pos The position to translate to
     * @return The newly created transform, translated by the given pos
     */
    private static Transform3D transFromPos (Vector3f pos) {
        Transform3D t1 = new Transform3D();
        t1.setTranslation(pos);
        return t1;
    }

    /**
     * Creates the entire table. <br>
     * This is nested into the class's TransformGroup
     * because it's created z-up then rotated to be z-forward.
     * @return The TransformGroup containing the entire table
     */
    private static TransformGroup createTable () {
        Transform3D tfTable = new Transform3D();
        tfTable.rotX(-Math.PI/2); 
        TransformGroup tgTable = new TransformGroup(tfTable);
        tgTable.addChild(createTop());
        tgTable.addChild(createPockets());
        tgTable.addChild(createLegs());
        return tgTable;
    }

    /**
     * Creates the top board of the pool table
     * @return The newly created transformgroup containing the pool table top
     */
    private static TransformGroup createTop () {
        Transform3D tfTableTop = new Transform3D();
        tfTableTop.setTranslation(new Vector3f(0.0f,0.0f,legHeight/2));
        TransformGroup tgTableTop = new TransformGroup(tfTableTop);
        tgTableTop.addChild(new Box(tableWidth_2,tableLength_2,tableDepth_2,Primitive.GENERATE_TEXTURE_COORDS, feltApp));
        return tgTableTop;
    }

    /**
     * Creates all the pockets for the table
     * @return The newly created transformgroup containing all the pockets
     */
    private static TransformGroup createPockets () {
        TransformGroup tg = new TransformGroup();
        float dz = legHeight/2 + tableDepth_2 + pocketHeight ;
        for (int i=0; i<6; i++) {
            tg.addChild( createPocket( new Vector3f(
                pocketCoords[i].getX(),
                pocketCoords[i].getY(),
                dz
            )));
        }
        return tg;
    }

    /**
     * Creates all the table legs and returns them as a Group
     * @return The newly created transformgroup containing all the legs
     */
    private static TransformGroup createLegs () {
        TransformGroup tgLegs = new TransformGroup();
        float dx = tableWidth_2 - legRadius;
        float dy = tableLength_2 - legRadius;
        tgLegs.addChild(createLeg(new Vector3f(+dx, +dy, 0.0f)));
        tgLegs.addChild(createLeg(new Vector3f(+dx, -dy, 0.0f)));
        tgLegs.addChild(createLeg(new Vector3f(-dx, +dy, 0.0f)));
        tgLegs.addChild(createLeg(new Vector3f(-dx, -dy, 0.0f)));
        return tgLegs;
    }

    /**
     * Creates a table leg at a given position
     * @param position The position to set the translation to
     * @return The newly created transformgroup, with the transform set, containing the table leg
     */
    private static TransformGroup createLeg (Vector3f position) {
        return initObj (
            new Cylinder (
                legRadius, legHeight,
                Primitive.GENERATE_TEXTURE_COORDS, 
                woodApp
            ),
            position
        );
    }

    /**
     * Creates a pool table pocket at the given position
     * @param position the position for the pocket to be
     * @return Newly created transformgroup with transform set, containing the pocket
     */
    private static TransformGroup createPocket (Vector3f position) {
        return initObj(
            new Cylinder(pocketRadius, pocketHeight, blackApp),
            position
        );
    }

    /**
     * Adds a node to a transformgroup, sets the translation to the
     * given position, and returns the transformgroup.
     * @param obj The already created node object to make child
     * @param position The translation this transformgroup should have
     * @return The newly created transformgroup
     */
    private static TransformGroup initObj (Node obj, Vector3f position) {
        Transform3D tfPocket = new Transform3D();
        tfPocket.rotX(Math.PI/2);
        tfPocket.setTranslation(position);
        TransformGroup tgPocket = new TransformGroup(tfPocket);
        tgPocket.addChild(obj);
        return tgPocket;
    }

    /**
     * Generates the coordinates for all the pockets
     * @return the 2D coordinates for all the pockets, top down
     */
    private static Vector2f[] createPocketCoords () {
        float dx = tableWidth_2 ;
        float dy = tableLength_2 ;
        Vector2f[] points = {
            new Vector2f(+dx, +dy),
            new Vector2f(+dx, -dy),
            new Vector2f(-dx, +dy),
            new Vector2f(-dx, -dy),
            new Vector2f(+dx,  0f),
            new Vector2f(-dx,  0f)
        };
        return points;
    }

}