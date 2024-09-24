package sekelsta.horse_colors.client.renderer;

import net.minecraft.client.renderer.texture.*;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;

@OnlyIn(Dist.CLIENT)
public class CustomLayeredTexture extends AbstractTexture {
    public final TextureLayerGroup layerGroup;

    public CustomLayeredTexture(TextureLayerGroup layers) {
        this.layerGroup = layers;
        if (this.layerGroup.layers.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        NativeImage image = layerGroup.getImage(manager);

        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                this.loadImage(image);
            });
        } else {
            this.loadImage(image);
        }
   }

   private void loadImage(NativeImage imageIn) {
      TextureUtil.prepareImage(this.getId(), imageIn.getWidth(), imageIn.getHeight());
      imageIn.upload(0, 0, 0, true);
   }


}
