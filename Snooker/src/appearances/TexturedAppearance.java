package appearances;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TextureAttributes;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.utils.image.ImageException;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Color3f;

/**
 * Subclass of appearance that is initialized with a texture
 * using the given image as the texture image.
 */
public class TexturedAppearance extends Appearance {
    /** Texture image to use as a backup */
    private static String backupImage = "NoTexture.png";
    /** Name of the texture image used */
    private String texName;
    /** Scale of the texture image when applying it */
    private float scale;
    /** Rotation of the texture image when applying it */
    private float rotation;
    
    /** Default constructor, uses the backup texture image */
    public TexturedAppearance () { this("NoTexture.png"); }

    /**
     * Overloaded constructor. Defaults scale to 1.0 and rotation to 0.0
     * @param textureName Name of texture image to use
     */
    public TexturedAppearance (String textureName) { this(textureName, 1.0f, 0.0f); }

    /**
     * Overloaded constructor. Defaults rotation to 0.0
     * @param textureName Name of texture image to use
     * @param scale Multiplier scale of image when projecting
     */
    public TexturedAppearance (String textureName, float scale) { this(textureName, scale, 0.0f); }

    /**
     * Full constructor. Creates an appearance initialized
     * with a texture using the given paramaters.
     * @param textureName Name of texture image to use
     * @param scale Multiplier scale of image when projecting
     * @param rotation Radiand rotation of image when projecting
     */
    public TexturedAppearance (String textureName, float scale, float rotation) {
        super();
        this.texName = textureName;
        this.scale = scale;
        this.rotation = rotation;
        this.setTexture(loadTexture(textureName));
        this.setTextureAttributes(newTextureAttributes(scale, rotation));
        this.setMaterial(MaterialFactory.createMaterial(new Color3f(0.5f, 0.5f, 0.5f)));
    }
    
    /**
     * Returns the name of the texture image being used
     * @return The name of the texture image being used
     */
    public String getName() {
        return this.texName;
    }
    
    /**
     * Gets the current image scale being used
     * @return Scale multiplier of the image
     */
    public float getScale() {
        return this.scale;
    }
    
    /**
     * Gets the current image rotation being used
     * @return Radians rotation of the image
     */
    public float getRotation() {
        return this.rotation;
    }
    
    /**
     * Creates a texture attributes object for modifying texture objects
     * @param scale Multiplier to scale your texture by
     * @param rotation Radians to rotaate your texture by
     * @return The newly created TextureAttributes object
     */
    public static TextureAttributes newTextureAttributes (float scale, float rotation) {
        TextureAttributes ta = new TextureAttributes();
        Transform3D trans = new Transform3D();
        ta.setTextureMode(TextureAttributes.REPLACE);
        trans.setScale(scale);
        trans.rotY(rotation);
        ta.setTextureTransform(trans);
        return ta;
    }
    
    /**
     * Creates a 2D texture object from the given image file name
     * @param fileName The name, with extension, of the image file to use
     * @return The newly created Texture2D object
     */
    public static Texture2D loadTexture (String fileName) {
        String filePath = "assets/images/" + fileName;
        TextureLoader loader ;
        try {
            loader = new TextureLoader(filePath, null);
        } catch (ImageException e) {
            if (fileName.equals(backupImage)) {
                e.printStackTrace(System.err);
                return null;
            } else {
                System.err.println("Failed to open texture image: "+fileName);
                return loadTexture(backupImage);
            }
        }
        ImageComponent2D image = loader.getImage();
        Texture2D texture = new Texture2D(
            Texture.BASE_LEVEL, Texture.RGBA,
            image.getWidth(), image.getHeight()
        );
        texture.setImage(0, image);
        return texture;
    }

}