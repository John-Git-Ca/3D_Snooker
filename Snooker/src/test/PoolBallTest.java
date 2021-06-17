package objects;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.QuadArray;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TriangleFanArray;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

import behaviours.FreeCamController;
import behaviours.PoolBallManager;
import jpanels.BasicView;
import lights.LightFactory;

public class PoolBallTest extends BasicView implements KeyListener {
    private static final long serialVersionUID = 1L;
    private FreeCamController fcc;
    private PoolBallManager pbm;
    
    public PoolBallTest () {}

    private Shape3D createSquare () {
        int format = QuadArray.COORDINATES | QuadArray.NORMALS;
        QuadArray geom = new QuadArray(4, format);
        float wr = (float)PoolBallManager.width_2;
        float lr = (float)PoolBallManager.length_2;
        float h = PoolBall.height;
        float coordinates[] = {
            -wr, h, -lr,    -wr, h, +lr,
            +wr, h, +lr,    +wr, h, -lr
        };
        float normals[] = {
            0, 1, 0,    0, 1, 0,
            0, 1, 0,    0, 1, 0
        };
        geom.setCoordinates(0, coordinates);
        geom.setNormals(0, normals);
        Appearance app = new Appearance();
        app.setColoringAttributes(new ColoringAttributes(
            new Color3f(0.1f, 0.5f, 0.1f), ColoringAttributes.NICEST
        ));
        return new Shape3D(geom, app);
    }

    private Shape3D createCircle (Point3d centre, float radius) {
        int fidelity = 64, strip[] = {fidelity} ;
        int format=TriangleFanArray.COORDINATES | TriangleFanArray.NORMALS; 
        float up [] = {0, 1, 0};
        TriangleFanArray geom = new TriangleFanArray(fidelity, format, strip);
        geom.setCoordinate(0, centre); geom.setNormal(0, up);
        double a=0, da = Math.PI*2*(1.0 / (fidelity-2));
        for (int i=1; i<fidelity; i++) {
            geom.setCoordinate(i, new Point3d(
                centre.getX() + Math.cos(a) * radius,
                centre.getY(),
                centre.getZ() + Math.sin(a) * radius
            ));
            geom.setNormal(i, up);
            a += da;
        }
        Appearance app = new Appearance();
        app.setColoringAttributes(new ColoringAttributes(
            new Color3f(0,0.2f,0), ColoringAttributes.NICEST
        ));
        app.setPolygonAttributes(new PolygonAttributes(
            PolygonAttributes.POLYGON_FILL,
            PolygonAttributes.CULL_NONE,
            0.0f
        ));
        return new Shape3D(geom, app);
    }
    
    @Override
    public BranchGroup createContent() {
        BranchGroup content = new BranchGroup();
        
        // Add the lights
        content.addChild(new AxisFrame());
        content.addChild(LightFactory.createAmbientLight());
        content.addChild(LightFactory.createPointLight(new Point3f(0.5f, 1, 0)));
        
        // Add the free cam
        Transform3D t = new Transform3D();
        t.lookAt(new Point3d(2,2,2), new Point3d(), new Vector3d(0,1,0));
        t.invert();
        setViewTransform(t);
        this.fcc = new FreeCamController(this);
        content.addChild(fcc);

        // Add the square 'pool table'
        content.addChild(createSquare());
        for (int i=0; i<6; i++) {
            content.addChild(createCircle(
                new Point3d(PoolBallManager.pockets[i].x, PoolBall.height+0.0001, PoolBallManager.pockets[i].y),
                (float) PoolBallManager.pocketRadius
            ));
        }
        
        // Add the pool ball manager
        this.pbm = new PoolBallManager();
        content.addChild(pbm.getTG());
        content.addChild(pbm);
        this.getCanvas().addKeyListener(this);

        content.compile();
        return content;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jframes.Window(new PoolBallTest(), "Pool Ball Logic Tester");
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent arg0) {}

    @Override
    public void keyReleased(KeyEvent arg0) {
        int key = arg0.getKeyCode();
        if (key == KeyEvent.VK_R) {
            float angle = (float)(this.fcc.getYaw()+Math.PI);
            this.pbm.strikeCueBall(angle, 3.0f*0.016f);
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {}

}
