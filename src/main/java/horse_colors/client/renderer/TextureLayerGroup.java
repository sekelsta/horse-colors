package sekelsta.horse_colors.client.renderer;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.IResourceManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sekelsta.horse_colors.util.Color;

public class TextureLayerGroup extends TextureLayer {
    public List<TextureLayer> layers;
    
    public TextureLayerGroup() {
        this.layers = new ArrayList<>();
    }

    public TextureLayerGroup(List<TextureLayer> layers) {
        this.layers = layers;
    }

    public void add(TextureLayer layer) {
        layers.add(layer);
    }

    @Override
    public NativeImage getLayer(IResourceManager manager) {
        Iterator<TextureLayer> iterator = this.layers.iterator();
        TextureLayer baselayer = iterator.next();
        NativeImage baseimage = baselayer.getLayer(manager);
        if (baseimage == null) {
            // baselayer.getLayer() will already have logged an error
            return null;
        }
        baselayer.colorLayer(baseimage);

        while(iterator.hasNext()) {
            TextureLayer layer = iterator.next();
            if (layer == null) {
                continue;
            }
            NativeImage image = layer.getLayer(manager);
            if (image != null) {
                layer.combineLayers(baseimage, image);
            }
        }

        this.colorLayer(baseimage);
        // Set white to avoid a double multiply
        this.color = new Color();

        return baseimage;
    }

    // Return a string unique for all the layers in the group
    public String getUniqueName() {
        String s = "";
        for (int i = 0; i < layers.size(); ++i) {
            if (this.layers.get(i) != null) {
                s += this.layers.get(i).getUniqueName();
            }
        }
        return s.toLowerCase();
    }

    public List<String> getDebugStrings() {
        List<String> strings = new ArrayList<>();
        for (TextureLayer layer : layers) {
            if (layer instanceof TextureLayerGroup) {
                TextureLayerGroup group = (TextureLayerGroup)layer;
                for (String s : group.getDebugStrings()) {
                    strings.add("    " + s);
                }
            }
            else if (layer != null) {
                strings.add(layer.toString());
            }
        }
        return strings;
    }
}
