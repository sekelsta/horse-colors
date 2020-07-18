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
public class CustomLayeredTexture extends Texture {
    private static final Logger LOGGER = LogManager.getLogger();
    public final List<TextureLayer> layers;

    public CustomLayeredTexture(List<TextureLayer> textures) {
        this.layers = Lists.newArrayList(textures);
        if (this.layers.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }


    public void loadTexture(IResourceManager manager) throws IOException {
        Iterator<TextureLayer> iterator = this.layers.iterator();
        TextureLayer baselayer = iterator.next();
        NativeImage baseimage = baselayer.getLayer(manager);
        if (baseimage == null) {
            // getLayer() will already have logged an error
            return;
        }
        baselayer.colorLayer(baseimage);

        while(iterator.hasNext()) {
            TextureLayer layer = iterator.next();
            if (layer == null) {
                continue;
            }
            if (layer.name != null) {
                NativeImage image = layer.getLayer(manager);
                if (image != null) {
                    layer.combineLayers(baseimage, image);
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


}
