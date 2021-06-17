package objects;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Transform3D;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

import behaviours.FreeCamController;
import jpanels.BasicView;
import lights.LightFactory;

public class ScoreboardTester extends BasicView implements KeyListener {
    private static final long serialVersionUID = 1L;
    private Scoreboard sb;
    
    public ScoreboardTester () {}
    
    @Override
    public BranchGroup createContent() {
        BranchGroup content = new BranchGroup();
        
        content.addChild(LightFactory.createAmbientLight());
        content.addChild(LightFactory.createPointLight(new Point3f(0.5f, 1, 0)));
        
        Transform3D t = new Transform3D();
        t.lookAt(new Point3d(2,2,2), new Point3d(), new Vector3d(0,1,0));
        t.invert();
        setViewTransform(t);
        FreeCamController fcc = new FreeCamController(this);
        content.addChild(fcc);
        
        content.addChild(new AxisFrame());

        this.sb = new Scoreboard(new Vector3d(1, 1, 1));
        content.addChild(sb);
        super.getCanvas().addKeyListener(this);

        content.compile();
        return content;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new jframes.Window(new ScoreboardTester(), "Scoreboard Tester");
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent arg0) {}

    @Override
    public void keyReleased(KeyEvent arg0) {
        switch (arg0.getKeyCode()) {
        case KeyEvent.VK_1:
            this.sb.scoreP1(); break;
        case KeyEvent.VK_2:
            this.sb.scoreP2(); break;
        }
        
    }

    @Override
    public void keyTyped(KeyEvent arg0) {}


}
