package appearances;

import org.jogamp.java3d.Material;
import org.jogamp.vecmath.Color3f;

/**
 * Factory style class for creating Materials
 */
public class MaterialFactory extends Material {

    /** Private constructor as class is a factory */
    private MaterialFactory() {}
    
    /**
     * Creates a plain material, semi-shiny, like acrylic.
     * @param clr The colour of the material
     * @return The newly created material
     */
    public static Material createMaterial(Color3f clr) {
        return new Material(
            clr, // Ambient colour
            new Color3f(0.0f, 0.0f, 0.0f), // Emissive colour
            clr, // Diffuse colour
            new Color3f(0.9f, 0.9f, 0.9f), // Specular colour
            128 // Shininess
        );
    }
}
