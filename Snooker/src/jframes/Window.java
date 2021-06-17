package jframes;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Default window.
 * Contains a title and whatever JPanel you put inside it.
 * <h3> Implementing </h3>
 * Your main function should create a Runnable which just creates
 * a new one of these. <br> Then in this class's constructor would you pass the source,
 * and optionally a title, or dimensions. <br>
 * For the source, pass a JPanel containing all the essentials. The {@link jpanels.BasicView} class should do.
 */
public class Window extends JFrame {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor with defaulting title and dimensions
     * Title defaults to "Ultimate Pool 100"
     * Dimensions default to 600x600
     * @param source The JPanel is basically the screen inside this window. Look through the jpanels package.
     */
    public Window (JPanel source) {
        this(source, "Ultimate Pool 100", 600, 600);
    }
    
    /**
     * Constructor with defaulting dimensions.
     * Dimensions default to 600x600
     * @param source The JPanel is basically the screen inside this window. Look through the jpanels package.
     * @param title The title of the window
     */
    public Window (JPanel source, String title) {
        this(source, title, 600, 600);
    }
    
    /**
     * Full constructor for a JFrame object.
     * The JFrame is basically the window itself.
     * @param source The JPanel is basically the screen inside this window. Look through the jpanels package.
     * @param title The title of the window
     * @param width Width of the window
     * @param height Height of the window
     */
    public Window (JPanel source, String title, int width, int height) {
        super(title);
        super.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        super.getContentPane().add(source);
        super.pack();
        super.setSize(width, height);
        super.setVisible(true);
    }

}
