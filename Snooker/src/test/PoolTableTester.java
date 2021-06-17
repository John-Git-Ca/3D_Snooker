package objects;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Transform3D;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

import behaviours.FreeCamController;
import jpanels.BasicView;
import lights.LightFactory;

public class PoolTableTester extends BasicView {
    private static final long serialVersionUID = 1L;

    public PoolTableTester() {
        
    }

    @Override
    public BranchGroup createContent() {
        BranchGroup bg = new BranchGroup();
        
        // Basic environment
        bg.addChild(new AxisFrame());
        bg.addChild(LightFactory.createAmbientLight());
        bg.addChild(LightFactory.createPointLight(new Point3f(0.1f, 3, 0.1f)));

        // Add the free cam
        Transform3D t = new Transform3D();
        t.lookAt(new Point3d(2,2,2), new Point3d(), new Vector3d(0,1,0));
        t.invert();
        setViewTransform(t);
        bg.addChild(new FreeCamController(this));
        
        // Add the pool table
        bg.addChild(new PoolTable());

        bg.compile();
        return bg;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jframes.Window(new PoolTableTester(), "Pool Table Tester");
            }
        });
    }

}
