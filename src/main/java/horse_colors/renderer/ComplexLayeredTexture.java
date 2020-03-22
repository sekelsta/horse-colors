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
        try (IResource iresource = manager.getResource(new ResourceLocation(layer.name))) {
            NativeImage image = net.minecraftforge.client.MinecraftForgeClient.getImageLayer(new ResourceLocation(layer.name), manager);
            NativeImage shading = null;
            NativeImage mask = null;
            if (layer.shading != null) {
                try (IResource iresources = manager.getResource(new ResourceLocation(layer.shading))) {
                    shading = net.minecraftforge.client.MinecraftForgeClient.getImageLayer(new ResourceLocation(layer.shading), manager);
                }
            }
            if (layer.mask != null) {
                try (IResource iresources = manager.getResource(new ResourceLocation(layer.mask))) {
                    mask = net.minecraftforge.client.MinecraftForgeClient.getImageLayer(new ResourceLocation(layer.mask), manager);
                }
            }
            colorLayer(image, shading, mask, layer);
            if (layer.next != null) {
                blendLayerKeepAlpha(image, getLayer(manager, layer.next));
            }
            return image;
        } catch (IOException ioexception) {
            LOGGER.error("Couldn't load layered image", (Throwable)ioexception);
        }
        return null;
    }

    public void blendLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                base.blendPixel(j, i, image.getPixelRGBA(j, i));
            }
        }
    }

    public void blendLayerKeepAlpha(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int cb = base.getPixelRGBA(j, i);
                int ci = image.getPixelRGBA(j, i);
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
                if (i == 66 && j == 58) {
                System.out.println("Blending (" + Integer.toHexString((int)r) + ", " + Integer.toHexString((int)g) + ", " + Integer.toHexString((int)b) + ")");
                System.out.println("    onto (" + Integer.toHexString((int)br) + ", " + Integer.toHexString((int)bg) + ", " + Integer.toHexString((int)bb) + ")");
                System.out.println(Integer.toHexString((int)fr) + ", " + Integer.toHexString((int)fg) + ", " + Integer.toHexString((int)fb) + ", " + Integer.toHexString((int)fa));}
                base.setPixelRGBA(j, i, NativeImage.getCombined(fa, fb, fg, fr));
            }
        }
    }

    public void colorLayer(NativeImage image, NativeImage shading, NativeImage mask, Layer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = image.getPixelRGBA(j, i);
                int finalColor = color;
                if (shading == null) {
                    finalColor = layer.multiply(color);
                }
                else {
                    finalColor = layer.shade(color, shading.getPixelRGBA(j, i));
                }
                if (mask != null) {
                    finalColor = layer.mask(finalColor, mask.getPixelRGBA(j, i));
                }
                image.setPixelRGBA(j, i, finalColor);
            }
        }
    }

    public void loadTexture(IResourceManager manager) throws IOException {
        Iterator<Layer> iterator = this.layers.iterator();
        Layer baselayer = iterator.next();
        NativeImage baseimage = getLayer(manager, baselayer);

        while(iterator.hasNext()) {
            Layer layer = iterator.next();
            if (layer == null) {
                continue;
            }
            if (layer.name != null) {
                NativeImage image = getLayer(manager, layer);
                blendLayer(baseimage, image);
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
        public String shading;
        public String mask;
        public int alpha;
        public int red;
        public int green;
        public int blue;
        // Don't go overboard and chain thousands of layers together
        // They all have to fit in memory at once and are rendered
        // recursively so therer is potential for stack overflow
        public Layer next;
        public Layer() {
            name = null;
            shading = null;
            mask = null;
            alpha = 255;
            red = 255;
            green = 255;
            blue = 255;
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
            color = this.multiply(color);
            float cr = NativeImage.getRed(color);
            float cg = NativeImage.getGreen(color);
            float cb = NativeImage.getBlue(color);
            float sr = NativeImage.getRed(shading);
            float sg = NativeImage.getGreen(shading);
            float sb = NativeImage.getBlue(shading);
            float a = (float)NativeImage.getAlpha(shading) / 255.0F;
            float avg = (float)(this.red + this.green + this.blue) / 255.0F / 3.0F;
            a *= (1.0F - 3.0f/4.0f*avg);
            float na = 1.0F - a;
            float r = Math.min(255.0F, sr * a + cr * na);
            float g = Math.min(255.0F, sg * a + cg * na);
            float b = Math.min(255.0F, sb * a + cb * na);
            int ca = NativeImage.getAlpha(color);
            return NativeImage.getCombined(ca, (int)b, (int)g, (int)r);
        }
        
        public int mask(int color, int mask) {
            float a = NativeImage.getAlpha(color) * NativeImage.getAlpha(mask);
            a /= 255.0F;
            int r = NativeImage.getRed(color);
            int g = NativeImage.getGreen(color);
            int b = NativeImage.getBlue(color);
            return NativeImage.getCombined((int)a, b, g, r);
        }

        public int mask(int color, int shading, int mask) {
            color = this.shade(color, shading);
            return mask(color, mask);
        }
    }
}
