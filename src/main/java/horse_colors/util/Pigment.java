package sekelsta.horse_colors.util;

// For adjusting the density of pigment of the given color
public class Pigment {
    // The pigment color at a fixed and relatively low density
    public Color color;
    // The density of pigment
    public float concentration;
    // For decreasing saturation by averaging the color with white
    public float white;

    public Pigment() {}

    public Pigment(Color color, float concentration, float white) {
        this.color = color;
        this.concentration = concentration;
        this.white = white;
    }

    // Use the pigment color and density to get the final color
    public Color toColor() {
        Color c = new Color(color);
        c.power(concentration);
        c.addWhite(white);
        return c;
    }
}
