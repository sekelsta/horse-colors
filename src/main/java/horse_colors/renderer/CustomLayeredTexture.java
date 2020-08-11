package sekelsta.horse_colors.renderer;

import net.minecraft.client.renderer.texture.*;
import com.google.common.collect.Lists;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class CustomLayeredTexture extends AbstractTexture {
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
        BufferedImage baseimage = baselayer.getLayer(manager);
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
                BufferedImage image = layer.getLayer(manager);
                if (image != null) {
                    layer.combineLayers(baseimage, image);
                }
            }
        }

        this.loadImage(baseimage);
   }

   private void loadImage(BufferedImage imageIn) {
      TextureUtil.uploadTextureImage(this.getGlTextureId(), imageIn);
 /*     TextureUtil.prepareImage(this.getGlTextureId(), imageIn.getWidth(), imageIn.getHeight());
      imageIn.uploadTextureSub(0, 0, 0, true);*/
   }


}
