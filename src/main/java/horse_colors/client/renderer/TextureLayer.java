package sekelsta.horse_colors.client.renderer;

import java.io.IOException;
import java.util.Locale;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sekelsta.horse_colors.util.Color;

public class TextureLayer {
    protected static final Logger LOGGER = LogManager.getLogger();

    public String name;
    public String description;
    public Type type;
    public Color color;

    public TextureLayer() {
        name = null;
        description = null;
        type = Type.NORMAL;
        color = new Color();
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
                blendPixel(base, j, i, this.multiply(image.getPixelRGBA(j, i)));
            }
        }
    }

    public void blendLayerKeepAlpha(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int cb = base.getPixelRGBA(j, i);
                int ci = this.multiply(image.getPixelRGBA(j, i));
                float a = NativeImage.getA(ci) / 255.0F;
                float r = NativeImage.getR(ci);
                float g = NativeImage.getG(ci);
                float b = NativeImage.getB(ci);
                float br = NativeImage.getR(cb);
                float bg = NativeImage.getG(cb);
                float bb = NativeImage.getB(cb);
                int fa = NativeImage.getA(cb);
                int fr = (int)(r * a + br * (1.0F-a));
                int fg = (int)(g * a + bg * (1.0F-a));
                int fb = (int)(b * a + bb * (1.0F-a));
                base.setPixelRGBA(j, i, NativeImage.combine(fa, fb, fg, fr));
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
                blendPixel(base, j, i, this.power(color, exp));
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
                blendPixel(base, j, i, this.root(color, exp));
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
        int a = NativeImage.getA(color);
        a = (int)((float)a * this.color.a);
        int r = NativeImage.getR(color);
        r = (int)((float)r * this.color.r);
        int g = NativeImage.getG(color);
        g = (int)((float)g * this.color.g);
        int b = NativeImage.getB(color);
        b = (int)((float)b * this.color.b);
        return NativeImage.combine(a, b, g, r);
    }

    public int shade(int color, int shading) {
        float cr = NativeImage.getR(color);
        float cg = NativeImage.getG(color);
        float cb = NativeImage.getB(color);
        float sr = NativeImage.getR(shading);
        float sg = NativeImage.getG(shading);
        float sb = NativeImage.getB(shading);
        float a = (float)NativeImage.getA(shading) / 255.0F;
        float avg = (float)(cr + cg + cb) / 255.0F / 3.0F;
        a *= 0.5f + 0.5f * (1f - avg) * (1f - avg);
        float na = 1.0F - a;
        float r = Math.max(0, Math.min(255.0F, sr * a + cr * na));
        float g = Math.max(0, Math.min(255.0F, sg * a + cg * na));
        float b = Math.max(0, Math.min(255.0F, sb * a + cb * na));
        int ca = NativeImage.getA(color);
        return NativeImage.combine(ca, (int)b, (int)g, (int)r);
    }
    
    public int highlight(int color, int light) {
        float r0 = NativeImage.getR(color);
        float g0 = NativeImage.getG(color);
        float b0 = NativeImage.getB(color);
        float r1 = NativeImage.getR(light);
        float g1 = NativeImage.getG(light);
        float b1 = NativeImage.getB(light);
        float a = (float)NativeImage.getA(light) / 255.0F;
        float avg = (float)(r0 + g0 + b0) / 255.0F / 3.0F;
        a *= 0.5f + 0.5f * avg * avg;
        float na = 1.0F - a;
        float r = Math.max(0, Math.min(255.0F, r1 * a + r0 * na));
        float g = Math.max(0, Math.min(255.0F, g1 * a + g0 * na));
        float b = Math.max(0, Math.min(255.0F, b1 * a + b0 * na));
        int ca = NativeImage.getA(color);
        return NativeImage.combine(ca, (int)b, (int)g, (int)r);
    }

    // For each RGB value, raise color to the 1 / exp
    public int power(int color, int exp) {
        float r0 = NativeImage.getR(color) / 255f;
        float g0 = NativeImage.getG(color) / 255f;
        float b0 = NativeImage.getB(color) / 255f;
        // No dividing by 0
        float r1 = Math.max(0.002f, NativeImage.getR(exp) / 255f);
        float g1 = Math.max(0.002f, NativeImage.getG(exp) / 255f);
        float b1 = Math.max(0.002f, NativeImage.getB(exp) / 255f);
        int r = clamp((int)(255f * Math.pow(r0, 1f / r1)));
        int g = clamp((int)(255f * Math.pow(g0, 1f / g1)));
        int b = clamp((int)(255f * Math.pow(b0, 1f / b1)));
        int a = NativeImage.getA(exp);
        return NativeImage.combine(a, b, g, r);
    }

    // For each RGB value, raise color to the exp
    public int root(int color, int exp) {
        float r0 = NativeImage.getR(color) / 255f;
        float g0 = NativeImage.getG(color) / 255f;
        float b0 = NativeImage.getB(color) / 255f;
        float r1 = NativeImage.getR(exp) / 255f;
        float g1 = NativeImage.getG(exp) / 255f;
        float b1 = NativeImage.getB(exp) / 255f;
        int r = clamp((int)(255f * Math.pow(r0, r1)));
        int g = clamp((int)(255f * Math.pow(g0, g1)));
        int b = clamp((int)(255f * Math.pow(b0, b1)));
        int a = NativeImage.getA(exp);
        return NativeImage.combine(a, b, g, r);
    }

    public int mask(int color, int mask) {
        float a = NativeImage.getA(color) * NativeImage.getA(mask);
        a /= 255.0F;
        float weight = this.color.a;
        a = a * weight + NativeImage.getA(color) * (1 - weight);
        int r = NativeImage.getR(color);
        int g = NativeImage.getG(color);
        int b = NativeImage.getB(color);
        return NativeImage.combine((int)a, b, g, r);
    }

    // Restrict to range [0, 255]
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
        s += "-" + this.color.toHexString();
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
        if (color.getIntRed() != 255 || color.getIntGreen() != 255 
                || color.getIntBlue() != 255 || color.getIntAlpha() != 255) {
            s += "-" + this.color.toHexString();
        }
        s += "_";
        // Specify English to avoid Turkish locale bug
        return s.toLowerCase(Locale.ENGLISH);
    }

    public void blendPixel(NativeImage image, int x, int y, int color) {
        int baseColor = image.getPixelRGBA(x, y);
        float a = (float)image.getA(color) / 255.0F;
        float blue = (float)image.getB(color);
        float green = (float)image.getG(color);
        float red = (float)image.getR(color);
        float baseAlpha = (float)image.getA(baseColor) / 255.0F;
        float baseBlue = (float)image.getB(baseColor);
        float baseGreen = (float)image.getG(baseColor);
        float baseRed = (float)image.getR(baseColor);
        float alph = a * a + baseAlpha * (1 - a);
        int finalAlpha = (int)(alph * 255.0F);
        int finalBlue = (int)(blue * a + baseBlue * (1 - a));
        int finalGreen = (int)(green * a + baseGreen * (1 - a));
        int finalRed = (int)(red * a + baseRed * (1 - a));
        if (finalAlpha > 255) {
            finalAlpha = 255;
        }

        if (finalBlue > 255) {
            finalBlue =  255;
        }

        if (finalGreen > 255) {
            finalGreen = 255;
        }

        if (finalRed > 255) {
            finalRed = 255;
        }

        image.setPixelRGBA(x, y, image.combine(finalAlpha, finalBlue, finalGreen, finalRed));
    }
}
