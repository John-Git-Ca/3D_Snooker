package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Transform3D;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

import behaviours.GroundedCamController;
import behaviours.PoolBallManager;
import jpanels.BasicView;
import lights.LightFactory;
import objects.AxisFrame;
import objects.PoolTable;
import objects.Scoreboard;
import objects.SimpleRoom;

public class SinglePlayer extends BasicView implements KeyListener {
    private static final long serialVersionUID = 1L;
    private GroundedCamController gcc;
    private PoolBallManager pbm;
    private Scoreboard sb;
    
    public SinglePlayer () {}

    @Override
    public BranchGroup createContent() {
        BranchGroup content = new BranchGroup();

        // Add the lights
//        content.addChild(new AxisFrame());
        content.addChild(LightFactory.createAmbientLight());
        content.addChild(LightFactory.createPointLight(new Point3f(0.5f, 1, 0)));

        // Add the room
        content.addChild(new SimpleRoom(5.0f));

        // Add the scoreboard
//        content.addChild(this.sb = new Scoreboard(new Vector3d(-3,1,-1)));

        // Add the free cam
        Transform3D t = new Transform3D();
        t.lookAt(new Point3d(2,1,2), new Point3d(0, -50, 0), new Vector3d(0,1,0));
        t.invert();     setViewTransform(t);
        content.addChild(this.gcc = new GroundedCamController(this));

        // Add the pool table
        content.addChild(new PoolTable(new Vector3f(0, PoolTable.legHeight/2, 0)));
        
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
                new jframes.Window(new SinglePlayer(), "Ultimate Pool 100");
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent arg0) {}

    @Override
    public void keyReleased(KeyEvent arg0) {
        int key = arg0.getKeyCode();
        if (key >= KeyEvent.VK_1 && key <= KeyEvent.VK_9) {
            float angle = (float)(this.gcc.getYaw()+Math.PI);
            float power = (key-KeyEvent.VK_0)*0.016f;
            this.pbm.strikeCueBall(angle, power);
        } else
        if (key == KeyEvent.VK_COMMA) {
            this.sb.scoreP1();
        } else
        if (key == KeyEvent.VK_PERIOD) {
            this.sb.scoreP2();
        } else
        if (key == KeyEvent.VK_T) {
            this.pbm.swapShapes();
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {}

}
