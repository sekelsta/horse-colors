package sekelsta.horse_colors;

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

   public ComplexLayeredTexture(Layer... textures) {
      this.layers = Lists.newArrayList(textures);
      if (this.layers.isEmpty()) {
         throw new IllegalStateException("Layered texture with no layers.");
      }
   }

   public void loadTexture(IResourceManager manager) throws IOException {
      Iterator<Layer> iterator = this.layers.iterator();
      String s = iterator.next().name;

      try (IResource iresource = manager.getResource(new ResourceLocation(s))) {
         NativeImage nativeimage = net.minecraftforge.client.MinecraftForgeClient.getImageLayer(new ResourceLocation(s), manager);

         while(iterator.hasNext()) {
            Layer l = iterator.next();
            if (l == null) {
                continue;
            }
            String s1 = l.name;
            if (s1 != null) {
               try (
                  IResource iresource1 = manager.getResource(new ResourceLocation(s1));
                  NativeImage nativeimage1 = NativeImage.read(iresource1.getInputStream());
               ) {
                  for(int i = 0; i < nativeimage1.getHeight(); ++i) {
                     for(int j = 0; j < nativeimage1.getWidth(); ++j) {
                        nativeimage.blendPixel(j, i, nativeimage1.getPixelRGBA(j, i));
                     }
                  }
               }
            }
         }

         if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
               this.loadImage(nativeimage);
            });
         } else {
            this.loadImage(nativeimage);
         }
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't load layered image", (Throwable)ioexception);
      }

   }

   private void loadImage(NativeImage imageIn) {
      TextureUtil.prepareImage(this.getGlTextureId(), imageIn.getWidth(), imageIn.getHeight());
      imageIn.uploadTextureSub(0, 0, 0, true);
   }

    public static class Layer {
        public String name;
    }
}
