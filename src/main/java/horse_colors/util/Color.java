package sekelsta.horse_colors.util;

// Has normalized values for red, green, blue, and alpha
public class Color {
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(1f, 1f, 1f);


    public float a;
    public float r;
    public float g;
    public float b;

    public Color() {
        this(1f, 1f, 1f);
    }

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1f;
    }

    public Color(int red, int green, int blue) {
        this(red / 255f, green / 255f, blue / 255f);
    }

    public Color(Color copy) {
        this(copy.r, copy.g, copy.b);
    }

    public int getIntRed() {
        return (int)(r * 255);
    }

    public int getIntGreen() {
        return (int)(g * 255);
    }

    public int getIntBlue() {
        return (int)(b * 255);
    }

    public int getIntAlpha() {
        return (int)(a * 255);
    }

    public String toHexString() {
        return String.format("%02X%02X%02X%02X", this.getIntRed(), 
            this.getIntGreen(), this.getIntBlue(), this.getIntAlpha());
    }

    // Raise red, green, and blue to the given power. Has a similar effect on
    // the color as you could get by increasing the concentration of pigment.
    public void power(float p) {
        r = (float)Math.pow(r, p);
        g = (float)Math.pow(g, p);
        b = (float)Math.pow(b, p);
        clamp();
    }

    // Average this color with another, weighted
    public void average(Color c, float weight) {
        r = r * (1 - weight) + c.r * weight;
        g = g * (1 - weight) + c.g * weight;
        b = b * (1 - weight) + c.b * weight;
        a = a * (1 - weight) + c.a * weight;
        clamp();
    }

    // Average with white
    public void addWhite(float w) {
        Color white = new Color();
        this.average(white, w);
    }

    // Multiply each RGBA value with the given color
    public void multiply(Color c) {
        r *= c.r;
        g *= c.g;
        b *= c.b;
        a *= c.a;
        clamp();
    }

    // Clamp values to be between 0 and 1
    private void clamp() {
        r = Math.max(0f, Math.min(r, 1f));
        g = Math.max(0f, Math.min(g, 1f));
        b = Math.max(0f, Math.min(b, 1f));
        a = Math.max(0f, Math.min(a, 1f));
    }
}
