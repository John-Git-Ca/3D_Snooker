package objects;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Transform3D;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

import behaviours.FreeCamController;
import jpanels.BasicView;
import lights.LightFactory;
import objects.PoolBall.Type;

public class RoomLightingTester extends BasicView {
    private static final long serialVersionUID = 1L;

    public RoomLightingTester() {}
    
    @Override
    public BranchGroup createContent() {
        BranchGroup content = new BranchGroup();
        
        content.addChild(new AxisFrame());
        content.addChild(LightFactory.createAmbientLight());
        content.addChild(LightFactory.createPointLight(
            new Point3f(0.5f, 1, 0),
            new Point3f(0, 1f, 0)
        ));
        
        // Add the free cam
        Transform3D t = new Transform3D();
        t.lookAt(new Point3d(2,2,2), new Point3d(), new Vector3d(0,1,0));
        t.invert();
        setViewTransform(t);
        FreeCamController fcc = new FreeCamController(this);
        content.addChild(fcc);
        
        content.addChild(new SimpleRoom(2.0f));
        
        content.addChild(new PoolBall(Type.BLUE, 0, 0));
        
        content.compile();
        return content;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jframes.Window(new RoomLightingTester(), "Room Lighting Tester");
            }
        });
    }

}
