package objects;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.QuadArray;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

import appearances.MaterialFactory;
import appearances.TexturedAppearance;

/**
 * This class contains four walls facing inwards,
 * a simple room, so to speak.
 */
public class SimpleRoom extends TransformGroup {

    /**
     * Private static method for creating a rectangle
     * @param pnt1 First point of the rectangle
     * @param pnt2 Second point of the rectangle
     * @param pnt3 Third point of the rectangle
     * @param pnt4 Fourth point of the rectangle
     * @param normal Unit vector which the rectangle faces, and therefore the normal of the face
     * @param filename Name of texture image file to use for the rectangle
     * @return Newly created Shape3D of a rectangle, made using the given paramaters
     */
	private static Shape3D createRectangle(Point3f pnt1, Point3f pnt2, Point3f pnt3, Point3f pnt4, Vector3f normal, String filename) {
		int flags = QuadArray.NORMALS | QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2;
		QuadArray square = new QuadArray(4, flags); //quadArray to define one side
		Point3f[] pt1 = {pnt1, pnt2, pnt3, pnt4};
		float uvs[][] = {{0f, 0f}, {1f, 0f}, {1f, 1f}, {0f, 1f}};
		for(int i = 0; i<4; i++) {
			square.setCoordinate(i, pt1[i]);//set coordinates
			square.setNormal(i, normal);	//set surface normal
			square.setTextureCoordinate(0, i, uvs[i]);
		}
		Appearance app = new TexturedAppearance(filename, 0.0001f, 0.5f);
        app.setMaterial(MaterialFactory.createMaterial(new Color3f(1, 0, 0)));
		return new Shape3D(square, app);
	}
	
    /**
     * Default constructor, creates a simple 2x1x2 room
     */
	public SimpleRoom () {
		this(1.0f);
	}
	
    /**
     * Full constructor. Allows a scaling factor to make a bigger room/
     * @param factor Multiplier scale for the size of the room
     */
	public SimpleRoom (float factor) {
		super();
		
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f down = new Vector3f(0, -1, 0);
		Vector3f right = new Vector3f(1, 0, 0);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f forward = new Vector3f(0, 0, 1);
		Vector3f back = new Vector3f(0, 0, -1);
		
		Point3f[] pnt = new Point3f[8];
		pnt[0] = new Point3f(new Point3f(-1f, 0f, -1f)); // Back -Bot-Left
		pnt[1] = new Point3f(new Point3f(1f, 0f, -1f));  // Back -Bot-Right
		pnt[2] = new Point3f(new Point3f(1f, 1f, -1f));  // Back -Top-Right
		pnt[3] = new Point3f(new Point3f(-1f, 1f, -1f)); // Back -Top-Left
		pnt[4] = new Point3f(new Point3f(-1f, 1f, 1f));  // Front-Top-Left
		pnt[5] = new Point3f(new Point3f(-1f, 0f, 1f));  // Front-Bot-Left
		pnt[6] = new Point3f(new Point3f(1f, 0f, 1f));   // Front-Bot-Right
		pnt[7] = new Point3f(new Point3f(1f, 1f, 1f));   // Front-Top-Right
		for(int i=0; i<8; i++) {
			pnt[i].x = pnt[i].x * factor;
			pnt[i].y = pnt[i].y * factor;
			pnt[i].z = pnt[i].z * factor;
		}

		super.addChild(createRectangle(pnt[4], pnt[3], pnt[2], pnt[7], down, "roof.png"));	//top
		super.addChild(createRectangle(pnt[5], pnt[0], pnt[3], pnt[4], right, "sky.jpg"));	//left
		super.addChild(createRectangle(pnt[0], pnt[1], pnt[2], pnt[3], forward, "sky.jpg"));	//back
		super.addChild(createRectangle(pnt[1], pnt[6], pnt[7], pnt[2], left, "sky.jpg"));	//right
		super.addChild(createRectangle(pnt[6], pnt[5], pnt[4], pnt[7], back, "sky.jpg"));	//front
		super.addChild(createRectangle(pnt[5], pnt[6], pnt[1], pnt[0], up, "floor.png"));	//bottom
		
	}

}
