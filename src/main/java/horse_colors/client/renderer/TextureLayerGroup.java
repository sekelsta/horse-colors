package sekelsta.horse_colors.client.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.server.packs.resources.ResourceManager;

import sekelsta.horse_colors.util.Color;

public class TextureLayerGroup extends TextureLayer {
    public List<TextureLayer> layers;
    
    public TextureLayerGroup() {
        this.layers = new ArrayList<>();
    }

    public TextureLayerGroup(List<TextureLayer> layers) {
        this.layers = layers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextureLayerGroup)) {
            return false;
        }
        if (super.equals(o)) {
            TextureLayerGroup other = (TextureLayerGroup)o;
            return other.layers.equals(this.layers);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), layers.hashCode());
    }

    public void add(TextureLayer layer) {
        layers.add(layer);
    }

    protected NativeImage getUncoloredImage(ResourceManager manager) {
        TextureLayer baselayer = layers.get(0);
        NativeImage baseimage = baselayer.getImage(manager);
        if (baseimage == null) {
            // baselayer.getLayer() will already have logged an error
            return null;
        }

        for (int i = 1; i < layers.size(); ++i) {
            TextureLayer layer = layers.get(i);
            if (layer == null) {
                continue;
            }
            try {
                layer.apply(baseimage, manager);
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to combine images adding layer "
                                             + layer + " for " + this, e);
            }
        }

        return baseimage;
    }

    @Override
    public NativeImage getImage(ResourceManager manager) {
        NativeImage baseimage = getUncoloredImage(manager);
        colorLayer(baseimage);
        return baseimage;
    }

    @Override
    public void apply(NativeImage base, ResourceManager manager) {
        try (NativeImage image = getUncoloredImage(manager)) {
            combineLayers(base, image);
        }
    }

    // Return a string unique for all the layers in the group
    public String getUniqueName() {
        String s = "";
        for (int i = 0; i < layers.size(); ++i) {
            if (this.layers.get(i) != null) {
                s += this.layers.get(i).getUniqueName();
            }
        }
        // Specify English to avoid Turkish locale bug
        return s.toLowerCase(Locale.ENGLISH);
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
