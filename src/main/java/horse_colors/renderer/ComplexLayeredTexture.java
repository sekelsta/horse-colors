package sekelsta.horse_colors.renderer;

import net.minecraft.client.renderer.texture.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.mojang.blaze3d.platform.TextureUtil;
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
    public final List<TextureLayer> layers;

    public ComplexLayeredTexture(List<TextureLayer> textures) {
        this.layers = Lists.newArrayList(textures);
        if (this.layers.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }

    public NativeImage getLayer(IResourceManager manager, TextureLayer layer) {
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

    public void combineLayers(NativeImage base, NativeImage image, TextureLayer layer) {
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
            case POWER:
                powerLayer(base, image, layer);
                break;
            case ROOT:
                rootLayer(base, image, layer);
                break;
        }
    }

    public void blendLayer(NativeImage base, NativeImage image, TextureLayer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                base.blendPixel(j, i, layer.multiply(image.getPixelRGBA(j, i)));
            }
        }
    }

    public void blendLayerKeepAlpha(NativeImage base, NativeImage image, TextureLayer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int cb = base.getPixelRGBA(j, i);
                int ci = layer.multiply(image.getPixelRGBA(j, i));
                float a = TextureLayer.getAlpha(ci) / 255.0F;
                float r = TextureLayer.getRed(ci);
                float g = TextureLayer.getGreen(ci);
                float b = TextureLayer.getBlue(ci);
                float br = TextureLayer.getRed(cb);
                float bg = TextureLayer.getGreen(cb);
                float bb = TextureLayer.getBlue(cb);
                int fa = TextureLayer.getAlpha(cb);
                int fr = (int)(r * a + br * (1.0F-a));
                int fg = (int)(g * a + bg * (1.0F-a));
                int fb = (int)(b * a + bb * (1.0F-a));
                base.setPixelRGBA(j, i, TextureLayer.getCombined(fa, fb, fg, fr));
            }
        }
    }

    public void shadeLayer(NativeImage base, NativeImage image, TextureLayer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int shading = layer.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, layer.shade(color, shading));
            }
        }
    }

    public void highlightLayer(NativeImage base, NativeImage image, TextureLayer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int highlight = layer.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, layer.highlight(color, highlight));
            }
        }
    }

    public void maskLayer(NativeImage base, NativeImage image, TextureLayer layer) {
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

    // Raise RGB values to an exponent >= 1
    public void powerLayer(NativeImage base, NativeImage image, TextureLayer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int exp = image.getPixelRGBA(j, i);
                exp = layer.multiply(exp);
                base.blendPixel(j, i, layer.power(color, exp));
            }
        }
    }

    // Raise RGB values to an exponent <= 1
    public void rootLayer(NativeImage base, NativeImage image, TextureLayer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int exp = image.getPixelRGBA(j, i);
                exp = layer.multiply(exp);
                base.blendPixel(j, i, layer.root(color, exp));
            }
        }
    }

    public void colorLayer(NativeImage image, TextureLayer layer) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = image.getPixelRGBA(j, i);
                image.setPixelRGBA(j, i, layer.multiply(color));
            }
        }
    }

    public void loadTexture(IResourceManager manager) throws IOException {
        Iterator<TextureLayer> iterator = this.layers.iterator();
        TextureLayer baselayer = iterator.next();
        NativeImage baseimage = getLayer(manager, baselayer);
        if (baseimage == null) {
            // getLayer() will already have logged an error
            return;
        }
        colorLayer(baseimage, baselayer);

        while(iterator.hasNext()) {
            TextureLayer layer = iterator.next();
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

        this.loadImage(baseimage);
   }

   private void loadImage(NativeImage imageIn) {
      TextureUtil.prepareImage(this.getGlTextureId(), imageIn.getWidth(), imageIn.getHeight());
      imageIn.uploadTextureSub(0, 0, 0, true);
   }


}
