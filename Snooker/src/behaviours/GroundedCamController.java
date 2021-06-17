package behaviours;

import jpanels.BasicView;
import java.awt.event.KeyEvent;

/**
 * Extends and overrides some {@link FreeCamController} methods
 * to make a camera that is grounded instead of free flying.
 */
public class GroundedCamController extends FreeCamController {
    public static final double movSpeed = 1.5;

    /**
     * Default and only constructor.
     * @param view The BasicView object
     */
    public GroundedCamController(BasicView view) {
        super(view);
    }

    /**
     * Checks key presses and updates the ViewTransform
     */
    @Override
    protected void handleKeyInput () {
        // Time difference in seconds
        double dt = 0.016;
        // Check movement
        if (keys.contains(KeyEvent.VK_W)) viewPos.scaleAdd(+movSpeed*dt, forward, viewPos);
        if (keys.contains(KeyEvent.VK_A)) viewPos.scaleAdd(-movSpeed*dt, right, viewPos);
        if (keys.contains(KeyEvent.VK_D)) viewPos.scaleAdd(+movSpeed*dt, right, viewPos);
        if (keys.contains(KeyEvent.VK_S)) viewPos.scaleAdd(-movSpeed*dt, forward, viewPos);
        dt /= 2;
        if (keys.contains(KeyEvent.VK_SPACE)) {
            viewPos.scaleAdd(+movSpeed*dt, up, viewPos);
            if (viewPos.getY() > 1.1) viewPos.setY(1.1);
        }
        if (keys.contains(KeyEvent.VK_SHIFT)) {
            viewPos.scaleAdd(-movSpeed*dt, up, viewPos);
            if (viewPos.getY() < 0.5) viewPos.setY(0.5);
        }
    }

    /**
     * Updates the forward and right unit vectors
     */
    @Override
    protected void updateDirs () {
        forward.set(-Math.sin(viewYaw), 0, -Math.cos(viewYaw));
        right.set(Math.cos(-viewYaw), 0, Math.sin(-viewYaw));
    }
}
