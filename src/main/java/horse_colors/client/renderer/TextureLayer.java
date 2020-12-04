package sekelsta.horse_colors.client.renderer;

import java.io.IOException;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureLayer {
    protected static final Logger LOGGER = LogManager.getLogger();

    public String name;
    public String description;
    public Type type;
    public int alpha;
    public int red;
    public int green;
    public int blue;

    public TextureLayer() {
        name = null;
        description = null;
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
        HIGHLIGHT,
        POWER,
        ROOT
    }

    public NativeImage getLayer(IResourceManager manager) {
        if (this.name == null) {
            LOGGER.error("Attempting to load unspecified texture (name is null): " + this.toString());
            return null;
        }
        try (IResource iresource = manager.getResource(new ResourceLocation(this.name))) {
            NativeImage image = net.minecraftforge.client.MinecraftForgeClient.getImageLayer(new ResourceLocation(this.name), manager);
            return image;
        } catch (IOException ioexception) {
            LOGGER.error("Couldn't load layered image", (Throwable)ioexception);
        }
        LOGGER.error("Skipping layer " + this.toString());
        return null;
    }

    public void combineLayers(NativeImage base, NativeImage image) {
        switch(this.type) {
            case NORMAL:
                blendLayer(base, image);
                break;
            case NO_ALPHA:
                blendLayerKeepAlpha(base, image);
                break;
            case MASK:
                maskLayer(base, image);
                break;
            case SHADE:
                shadeLayer(base, image);
                break;
            case HIGHLIGHT:
                highlightLayer(base, image);
                break;
            case POWER:
                powerLayer(base, image);
                break;
            case ROOT:
                rootLayer(base, image);
                break;
        }
    }

    public void blendLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                base.blendPixel(j, i, this.multiply(image.getPixelRGBA(j, i)));
            }
        }
    }

    public void blendLayerKeepAlpha(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int cb = base.getPixelRGBA(j, i);
                int ci = this.multiply(image.getPixelRGBA(j, i));
                float a = NativeImage.getAlpha(ci) / 255.0F;
                float r = NativeImage.getRed(ci);
                float g = NativeImage.getGreen(ci);
                float b = NativeImage.getBlue(ci);
                float br = NativeImage.getRed(cb);
                float bg = NativeImage.getGreen(cb);
                float bb = NativeImage.getBlue(cb);
                int fa = NativeImage.getAlpha(cb);
                int fr = (int)(r * a + br * (1.0F-a));
                int fg = (int)(g * a + bg * (1.0F-a));
                int fb = (int)(b * a + bb * (1.0F-a));
                base.setPixelRGBA(j, i, NativeImage.getCombined(fa, fb, fg, fr));
            }
        }
    }

    public void shadeLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int shading = this.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, this.shade(color, shading));
            }
        }
    }

    public void highlightLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int highlight = this.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, this.highlight(color, highlight));
            }
        }
    }

    public void maskLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                // Don't multiply here because that would do the wrong thing
                int mask = image.getPixelRGBA(j, i);
                int maskedColor = this.mask(color, mask);
                base.setPixelRGBA(j, i, maskedColor);
            }
        }
    }

    // Raise RGB values to an exponent >= 1
    public void powerLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int exp = image.getPixelRGBA(j, i);
                exp = this.multiply(exp);
                base.blendPixel(j, i, this.power(color, exp));
            }
        }
    }

    // Raise RGB values to an exponent <= 1
    public void rootLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int exp = image.getPixelRGBA(j, i);
                exp = this.multiply(exp);
                base.blendPixel(j, i, this.root(color, exp));
            }
        }
    }

    public void colorLayer(NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = image.getPixelRGBA(j, i);
                image.setPixelRGBA(j, i, this.multiply(color));
            }
        }
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

    // For each RGB value, raise color to the 1 / exp
    public int power(int color, int exp) {
        float r0 = NativeImage.getRed(color) / 255f;
        float g0 = NativeImage.getGreen(color) / 255f;
        float b0 = NativeImage.getBlue(color) / 255f;
        // No dividing by 0
        float r1 = Math.max(0.002f, NativeImage.getRed(exp) / 255f);
        float g1 = Math.max(0.002f, NativeImage.getGreen(exp) / 255f);
        float b1 = Math.max(0.002f, NativeImage.getBlue(exp) / 255f);
        int r = clamp((int)(255f * Math.pow(r0, 1f / r1)));
        int g = clamp((int)(255f * Math.pow(g0, 1f / g1)));
        int b = clamp((int)(255f * Math.pow(b0, 1f / b1)));
        int a = NativeImage.getAlpha(exp);
        return NativeImage.getCombined(a, b, g, r);
    }

    // For each RGB value, raise color to the exp
    public int root(int color, int exp) {
        float r0 = NativeImage.getRed(color) / 255f;
        float g0 = NativeImage.getGreen(color) / 255f;
        float b0 = NativeImage.getBlue(color) / 255f;
        float r1 = NativeImage.getRed(exp) / 255f;
        float g1 = NativeImage.getGreen(exp) / 255f;
        float b1 = NativeImage.getBlue(exp) / 255f;
        int r = clamp((int)(255f * Math.pow(r0, r1)));
        int g = clamp((int)(255f * Math.pow(g0, g1)));
        int b = clamp((int)(255f * Math.pow(b0, b1)));
        int a = NativeImage.getAlpha(exp);
        return NativeImage.getCombined(a, b, g, r);
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
        this.red = clamp(this.red);
        this.green = clamp(this.green);
        this.blue = clamp(this.blue);
    }
    private int clamp(int x) {
        return Math.max(0, Math.min(x, 255));
    }

    static String getAbv(String s) {
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
        String s = "";
        if (this.name != null) {
            s += getAbv(this.name);
        }
        s += "-" + this.type.toString();
        s += "-" + Integer.toHexString(this.alpha);
        s += Integer.toHexString(this.red);
        s += Integer.toHexString(this.green);
        s += Integer.toHexString(this.blue);
        return s;
    }
    // Return a string unique for all the layers in the group,
    // ideally shorter rather than longer
    public String getUniqueName() {
        if (this.name == null) {
            return "";
        }
        String s = getAbv(this.name);
        if (this.type != Type.NORMAL) {
            s += "-" + this.type.toString();
        }
        if (this.alpha != 255 || this.red != 255 | this.green != 255 || this.blue != 255) {
            s += "-" + Integer.toHexString(this.alpha);
            s += Integer.toHexString(this.red);
            s += Integer.toHexString(this.green);
            s += Integer.toHexString(this.blue);
        }
        s += "_";
        return s.toLowerCase();
    }
}
