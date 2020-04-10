package sekelsta.horse_colors.renderer;

import net.minecraft.client.renderer.texture.*;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ComplexLayeredTexture extends Texture {
    private static final Logger LOGGER = LogManager.getLogger();
    public final List<Layer> layers;

    public ComplexLayeredTexture(List<Layer> textures) {
        this.layers = Lists.newArrayList(textures);
        if (this.layers.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }

    public NativeImage getLayer(IResourceManager manager, Layer layer) {
        if (layer.name == null) {
            LOGGER.error("Attempting to load unspecified texture (name is null)\n");
            return null;
        }
        try (IResource iresource = manager.getResource(new ResourceLocation(layer.name))) {
            NativeImage image = net.minecraftforge.client.MinecraftForgeClient.getImageLayer(new ResourceLocation(layer.name), manager);
            if (layer.next != null) {
                colorLayer(image, layer);
                combineLayers(image, getLayer(manager, layer.next), layer.next);
                // Avoid double multiply
                layer.red = 0xff;
                layer.green = 0xff;
                layer.blue = 0xff;
            }
            return image;
        } catch (IOException ioexception) {
            LOGGER.error("Couldn't load layered image", (Throwable)ioexception);
        }
        return null;
    }

    public void combineLayers(NativeImage base, NativeImage image, Layer layer) {
        switch(layer.type) {
            case NORMAL:
                blendLayer(base, image, layer);
                break;
            case NO_ALPHA:
                blendLayerKeepAlpha(base, image, layer);
                break;
            case MASK:
                maskLayer(base, image, layer);
                break;
            case SHADE:
                shadeLayer(base, image, layer);
                break;
            case HIGHLIGHT:
                highlightLayer(base, image, layer);
                break;
        }
    }

    public void blendLayer(NativeImage base, NativeImage image, Layer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                base.blendPixel(j, i, layer.multiply(image.getPixelRGBA(j, i)));
            }
        }
    }

    public void blendLayerKeepAlpha(NativeImage base, NativeImage image, Layer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int cb = base.getPixelRGBA(j, i);
                int ci = layer.multiply(image.getPixelRGBA(j, i));
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

    public void shadeLayer(NativeImage base, NativeImage image, Layer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int shading = layer.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, layer.shade(color, shading));
            }
        }
    }

    public void highlightLayer(NativeImage base, NativeImage image, Layer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int highlight = layer.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, layer.highlight(color, highlight));
            }
        }
    }

    public void maskLayer(NativeImage base, NativeImage image, Layer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                // Don't multiply here because that would do the wrong thing
                int mask = image.getPixelRGBA(j, i);
                int maskedColor = layer.mask(color, mask);
                base.setPixelRGBA(j, i, maskedColor);
            }
        }
    }

    public void colorLayer(NativeImage image, Layer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = image.getPixelRGBA(j, i);
                image.setPixelRGBA(j, i, layer.multiply(color));
            }
        }
    }

    public void loadTexture(IResourceManager manager) throws IOException {
        Iterator<Layer> iterator = this.layers.iterator();
        Layer baselayer = iterator.next();
        NativeImage baseimage = getLayer(manager, baselayer);
        if (baseimage == null) {
            // getLayer() will already have logged an error
            return;
        }
        colorLayer(baseimage, baselayer);

        while(iterator.hasNext()) {
            Layer layer = iterator.next();
            if (layer == null) {
                continue;
            }
            if (layer.name != null) {
                NativeImage image = getLayer(manager, layer);
                if (image != null) {
                    combineLayers(baseimage, image, layer);
                }
            }
        }

        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                this.loadImage(baseimage);
            });
        } else {
            this.loadImage(baseimage);
        }
   }

   private void loadImage(NativeImage imageIn) {
      TextureUtil.prepareImage(this.getGlTextureId(), imageIn.getWidth(), imageIn.getHeight());
      imageIn.uploadTextureSub(0, 0, 0, true);
   }

    public static class Layer {
        public String name;
        public Type type;
        public int alpha;
        public int red;
        public int green;
        public int blue;
        // Don't go overboard and chain thousands of layers together
        // They all have to fit in memory at once and are rendered
        // recursively so there is potential for stack overflow
        public Layer next;
        public Layer() {
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
}
