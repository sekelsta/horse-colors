package sekelsta.horse_colors.client.renderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sekelsta.horse_colors.util.Color;

import com.mojang.blaze3d.platform.NativeImage;

public class TextureLayer {
    protected static final Logger LOGGER = LogManager.getLogger();

    private static final HashMap<ResourceLocation, NativeImage> loadedImages = new HashMap<>();

    public String name;
    public Type type;
    public Color color;

    public TextureLayer() {
        name = null;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        TextureLayer other = (TextureLayer)o;
        return (name == other.name || (name != null && name.equals(other.name)))
            && type == other.type
            && color == other.color || (color != null && color.equals(other.color));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, color);
    }

    public NativeImage getLayer(ResourceManager manager) {
        if (this.name == null) {
            LOGGER.error("Attempting to load unspecified texture (name is null): " + this.toString());
            return null;
        }
        ResourceLocation resourceLocation = new ResourceLocation(this.name);
        NativeImage loadedImage = loadedImages.get(resourceLocation);
        if (loadedImage != null) {
            return loadedImage;
        }
        try {
            Resource resource = manager.getResource(resourceLocation).orElseThrow();
            return NativeImage.read(resource.open());
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

    private float getRedAsFloat(NativeImage image, int x, int y) {
        byte b = image.getRedOrLuminance(x, y);
        int i = (int)b & 255;
        return i / 255f;
    }

    private float getGreenAsFloat(NativeImage image, int x, int y) {
        byte b = image.getGreenOrLuminance(x, y);
        int i = (int)b & 255;
        return i / 255f;
    }

    private float getBlueAsFloat(NativeImage image, int x, int y) {
        byte b = image.getBlueOrLuminance(x, y);
        int i = (int)b & 255;
        return i / 255f;
    }

    private float getAlphaAsFloat(NativeImage image, int x, int y) {
        byte b = image.getLuminanceOrAlpha(x, y);
        int i = (int)b & 255;
        return i / 255f;
    }

    private float getMultipliedRedAsFloat(NativeImage image, int x, int y) {
        return getRedAsFloat(image, x, y) * color.r;
    }

    private float getMultipliedGreenAsFloat(NativeImage image, int x, int y) {
        return getGreenAsFloat(image, x, y) * color.g;
    }

    private float getMultipliedBlueAsFloat(NativeImage image, int x, int y) {
        return getBlueAsFloat(image, x, y) * color.b;
    }

    private float getMultipliedAlphaAsFloat(NativeImage image, int x, int y) {
        return getAlphaAsFloat(image, x, y) * color.a;
    }

    private void setRGBA(NativeImage image, int x, int y, float r, float g, float b, float a) {
        int ir = clamp((int)(r * 255));
        int ig = clamp((int)(g * 255));
        int ib = clamp((int)(b * 255));
        int ia = clamp((int)(a * 255));

        int redOffset = image.format().luminanceOrRedOffset();
        int greenOffset = image.format().luminanceOrGreenOffset();
        int blueOffset = image.format().luminanceOrBlueOffset();
        int alphaOffset = image.format().luminanceOrAlphaOffset();

        int color = (ir << redOffset) | (ig << greenOffset) | (ib << blueOffset) | (ia << alphaOffset);

        image.setPixelRGBA(x, y, color);
    }

    public void blendLayer(NativeImage base, NativeImage image) {
        for(int base_y = 0; base_y < base.getHeight(); ++base_y) {
            for(int base_x = 0; base_x < base.getWidth(); ++base_x) {
                int x = base_x * image.getWidth() / base.getWidth();
                int y = base_y * image.getHeight() / base.getHeight();
                float baseR = getRedAsFloat(base, x, y);
                float baseG = getGreenAsFloat(base, x, y);
                float baseB = getBlueAsFloat(base, x, y);
                float baseA = getAlphaAsFloat(base, x, y);

                float imgR = getMultipliedRedAsFloat(image, x, y);
                float imgG = getMultipliedGreenAsFloat(image, x, y);
                float imgB = getMultipliedBlueAsFloat(image, x, y);
                float imgA = getMultipliedAlphaAsFloat(image, x, y);

                float r = imgR * imgA + baseR * (1 - imgA);
                float g = imgG * imgA + baseG * (1 - imgA);
                float b = imgB * imgA + baseB * (1 - imgA);
                float a = imgA * imgA + baseA * (1 - imgA);

                setRGBA(base, x, y, r, g, b, a);
            }
        }
    }

    public void blendLayerKeepAlpha(NativeImage base, NativeImage image) {
        for(int base_y = 0; base_y < base.getHeight(); ++base_y) {
            for(int base_x = 0; base_x < base.getWidth(); ++base_x) {
                int x = base_x * image.getWidth() / base.getWidth();
                int y = base_y * image.getHeight() / base.getHeight();
                float baseR = getRedAsFloat(base, x, y);
                float baseG = getGreenAsFloat(base, x, y);
                float baseB = getBlueAsFloat(base, x, y);
                float baseA = getAlphaAsFloat(base, x, y);

                float imgR = getMultipliedRedAsFloat(image, x, y);
                float imgG = getMultipliedGreenAsFloat(image, x, y);
                float imgB = getMultipliedBlueAsFloat(image, x, y);
                float imgA = getMultipliedAlphaAsFloat(image, x, y);

                float r = baseR * baseA + imgR * (1 - baseA);
                float g = baseG * baseA + imgG * (1 - baseA);
                float b = baseB * baseA + imgB * (1 - baseA);
                float a = imgA;

                setRGBA(base, x, y, r, g, b, a);
            }
        }
    }

    public void shadeLayer(NativeImage base, NativeImage image) {
        for(int base_y = 0; base_y < base.getHeight(); ++base_y) {
            for(int base_x = 0; base_x < base.getWidth(); ++base_x) {
                int x = base_x * image.getWidth() / base.getWidth();
                int y = base_y * image.getHeight() / base.getHeight();
                float baseR = getRedAsFloat(base, x, y);
                float baseG = getGreenAsFloat(base, x, y);
                float baseB = getBlueAsFloat(base, x, y);
                float baseA = getAlphaAsFloat(base, x, y);

                float imgR = getMultipliedRedAsFloat(image, x, y);
                float imgG = getMultipliedGreenAsFloat(image, x, y);
                float imgB = getMultipliedBlueAsFloat(image, x, y);
                float imgA = getMultipliedAlphaAsFloat(image, x, y);

                float value = (baseR + baseG + baseB) / 3f;
                float a = imgA * (0.5f + 0.5f * (1 - value) * (1 - value));

                float r = imgR * a + baseR * (1 - a);
                float g = imgG * a + baseG * (1 - a);
                float b = imgB * a + baseB * (1 - a);
                a = a * a + baseA * (1 - a);

                setRGBA(base, x, y, r, g, b, a);
            }
        }
    }

    public void highlightLayer(NativeImage base, NativeImage image) {
        for(int base_y = 0; base_y < base.getHeight(); ++base_y) {
            for(int base_x = 0; base_x < base.getWidth(); ++base_x) {
                int x = base_x * image.getWidth() / base.getWidth();
                int y = base_y * image.getHeight() / base.getHeight();
                float baseR = getRedAsFloat(base, x, y);
                float baseG = getGreenAsFloat(base, x, y);
                float baseB = getBlueAsFloat(base, x, y);
                float baseA = getAlphaAsFloat(base, x, y);

                float imgR = getMultipliedRedAsFloat(image, x, y);
                float imgG = getMultipliedGreenAsFloat(image, x, y);
                float imgB = getMultipliedBlueAsFloat(image, x, y);
                float imgA = getMultipliedAlphaAsFloat(image, x, y);

                float value = (baseR + baseG + baseB) / 3f;
                float a = imgA * (0.5f + 0.5f * value * value);

                float r = imgR * a + baseR * (1 - a);
                float g = imgG * a + baseG * (1 - a);
                float b = imgB * a + baseB * (1 - a);
                a = a * a + baseA * (1 - a);

                setRGBA(base, x, y, r, g, b, a);
            }
        }
    }

    public void maskLayer(NativeImage base, NativeImage image) {
        for(int base_y = 0; base_y < base.getHeight(); ++base_y) {
            for(int base_x = 0; base_x < base.getWidth(); ++base_x) {
                int x = base_x * image.getWidth() / base.getWidth();
                int y = base_y * image.getHeight() / base.getHeight();
                float baseR = getRedAsFloat(base, x, y);
                float baseG = getGreenAsFloat(base, x, y);
                float baseB = getBlueAsFloat(base, x, y);
                float baseA = getAlphaAsFloat(base, x, y);

                // Don't multiply here because that would do the wrong thing
                float imgA = getAlphaAsFloat(image, x, y);

                setRGBA(base, x, y, baseR, baseG, baseB, baseA * imgA);
            }
        }
    }

    // Raise RGB values to an exponent >= 1
    public void powerLayer(NativeImage base, NativeImage image) {
        for(int base_y = 0; base_y < base.getHeight(); ++base_y) {
            for(int base_x = 0; base_x < base.getWidth(); ++base_x) {
                int x = base_x * image.getWidth() / base.getWidth();
                int y = base_y * image.getHeight() / base.getHeight();
                float baseR = getRedAsFloat(base, x, y);
                float baseG = getGreenAsFloat(base, x, y);
                float baseB = getBlueAsFloat(base, x, y);
                float baseA = getAlphaAsFloat(base, x, y);

                float imgR = getMultipliedRedAsFloat(image, x, y);
                float imgG = getMultipliedGreenAsFloat(image, x, y);
                float imgB = getMultipliedBlueAsFloat(image, x, y);
                float imgA = getMultipliedAlphaAsFloat(image, x, y);

                // For each RGB value, raise color to the 1 / imgV
                // No dividing by 0
                float expR = (float)Math.pow(baseR, 1 / Math.max(0.002f, imgR));
                float expG = (float)Math.pow(baseG, 1 / Math.max(0.002f, imgG));
                float expB = (float)Math.pow(baseB, 1 / Math.max(0.002f, imgB));
                float a = imgA;

                float r = expR * a + baseR * (1 - a);
                float g = expG * a + baseG * (1 - a);
                float b = expB * a + baseB * (1 - a);
                a = a * a + baseA * (1 - a);

                setRGBA(base, x, y, r, g, b, a);
            }
        }
    }

    // Raise RGB values to an exponent <= 1
    public void rootLayer(NativeImage base, NativeImage image) {
        for(int base_y = 0; base_y < base.getHeight(); ++base_y) {
            for(int base_x = 0; base_x < base.getWidth(); ++base_x) {
                int x = base_x * image.getWidth() / base.getWidth();
                int y = base_y * image.getHeight() / base.getHeight();
                float baseR = getRedAsFloat(base, x, y);
                float baseG = getGreenAsFloat(base, x, y);
                float baseB = getBlueAsFloat(base, x, y);
                float baseA = getAlphaAsFloat(base, x, y);

                float imgR = getMultipliedRedAsFloat(image, x, y);
                float imgG = getMultipliedGreenAsFloat(image, x, y);
                float imgB = getMultipliedBlueAsFloat(image, x, y);
                float imgA = getMultipliedAlphaAsFloat(image, x, y);

                // For each RGB value, raise color to the imgV
                float expR = (float)Math.pow(baseR, imgR);
                float expG = (float)Math.pow(baseG, imgG);
                float expB = (float)Math.pow(baseB, imgB);
                float a = imgA;

                float r = expR * a + baseR * (1 - a);
                float g = expG * a + baseG * (1 - a);
                float b = expB * a + baseB * (1 - a);
                a = a * a + baseA * (1 - a);

                setRGBA(base, x, y, r, g, b, a);
            }
        }
    }

    public void colorLayer(NativeImage image) {
        for(int y = 0; y < image.getHeight(); ++y) {
            for(int x = 0; x < image.getWidth(); ++x) {
                float imgR = getMultipliedRedAsFloat(image, x, y);
                float imgG = getMultipliedGreenAsFloat(image, x, y);
                float imgB = getMultipliedBlueAsFloat(image, x, y);
                float imgA = getMultipliedAlphaAsFloat(image, x, y);

                setRGBA(image, x, y, imgR, imgG, imgB, imgA);
            }
        }
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
}
