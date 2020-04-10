package sekelsta.horse_colors.renderer;

import net.minecraft.client.renderer.texture.*;

public class TextureLayer {
    public String name;
    public Type type;
    public int alpha;
    public int red;
    public int green;
    public int blue;
    // Don't go overboard and chain thousands of layers together
    // They all have to fit in memory at once and are rendered
    // recursively so there is potential for stack overflow
    public TextureLayer next;
    public TextureLayer() {
        name = null;
        type = Type.NORMAL;
        alpha = 255;
        red = 255;
        green = 255;
        blue = 255;
    }

    public enum Type {
        NORMAL,
        NO_ALPHA,
        MASK,
        SHADE,
        HIGHLIGHT
    }

    public int multiply(int color) {
        int a = NativeImage.getAlpha(color);
        a = (int)((float)a * (float)this.alpha / 255.0F);
        int r = NativeImage.getRed(color);
        r = (int)((float)r * (float)this.red / 255.0F);
        int g = NativeImage.getGreen(color);
        g = (int)((float)g * (float)this.green / 255.0F);
        int b = NativeImage.getBlue(color);
        b = (int)((float)b * (float)this.blue / 255.0F);
        return NativeImage.getCombined(a, b, g, r);
    }

    public int shade(int color, int shading) {
        float cr = NativeImage.getRed(color);
        float cg = NativeImage.getGreen(color);
        float cb = NativeImage.getBlue(color);
        float sr = NativeImage.getRed(shading);
        float sg = NativeImage.getGreen(shading);
        float sb = NativeImage.getBlue(shading);
        float a = (float)NativeImage.getAlpha(shading) / 255.0F;
        float avg = (float)(cr + cg + cb) / 255.0F / 3.0F;
        a *= 0.5f + 0.5f * (1f - avg) * (1f - avg);
        float na = 1.0F - a;
        float r = Math.max(0, Math.min(255.0F, sr * a + cr * na));
        float g = Math.max(0, Math.min(255.0F, sg * a + cg * na));
        float b = Math.max(0, Math.min(255.0F, sb * a + cb * na));
        int ca = NativeImage.getAlpha(color);
        return NativeImage.getCombined(ca, (int)b, (int)g, (int)r);
    }
    
    public int highlight(int color, int light) {
        float r0 = NativeImage.getRed(color);
        float g0 = NativeImage.getGreen(color);
        float b0 = NativeImage.getBlue(color);
        float r1 = NativeImage.getRed(light);
        float g1 = NativeImage.getGreen(light);
        float b1 = NativeImage.getBlue(light);
        float a = (float)NativeImage.getAlpha(light) / 255.0F;
        float avg = (float)(r0 + g0 + b0) / 255.0F / 3.0F;
        a *= 0.5f + 0.5f * avg * avg;
        float na = 1.0F - a;
        float r = Math.max(0, Math.min(255.0F, r1 * a + r0 * na));
        float g = Math.max(0, Math.min(255.0F, g1 * a + g0 * na));
        float b = Math.max(0, Math.min(255.0F, b1 * a + b0 * na));
        int ca = NativeImage.getAlpha(color);
        return NativeImage.getCombined(ca, (int)b, (int)g, (int)r);
    }

    public int mask(int color, int mask) {
        float a = NativeImage.getAlpha(color) * NativeImage.getAlpha(mask);
        a /= 255.0F;
        float weight = this.alpha / 255f;
        a = a * weight + NativeImage.getAlpha(color) * (1 - weight);
        int r = NativeImage.getRed(color);
        int g = NativeImage.getGreen(color);
        int b = NativeImage.getBlue(color);
        return NativeImage.getCombined((int)a, b, g, r);
    }

    // Restrict to range [0, 255]
    public void clamp() {
        this.red = Math.max(0, Math.min(this.red, 255));
        this.green = Math.max(0, Math.min(this.green, 255));
        this.blue = Math.max(0, Math.min(this.blue, 255));
    }

    private String getAbv(String s) {
        int i = s.lastIndexOf("/");
        if (i > -1) {
            s = s.substring(i + 1);
        }
        if (s.endsWith(".png")) {
            s = s.substring(0, s.length() - 4);
        }
        return s;
    }

    public String toString() {
        if (this.name == null) {
            return "";
        }
        String s = getAbv(this.name);
        s += "-" + this.type.toString();
        s += "-" + Integer.toHexString(this.alpha);
        s += Integer.toHexString(this.red);
        s += Integer.toHexString(this.green);
        s += Integer.toHexString(this.blue);
        return s;
    }
}
