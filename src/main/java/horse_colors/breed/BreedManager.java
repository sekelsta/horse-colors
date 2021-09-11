package sekelsta.horse_colors.breed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.*;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

public class BreedManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private static Map<ResourceLocation, Breed<Gene>> breeds;

    private static BreedManager instance = new BreedManager();

    public BreedManager() {
        super(GSON, "breeds");
    }

    protected void apply(Map<ResourceLocation, JsonElement> mapIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        breeds = new HashMap<>();
        for(ResourceLocation key : mapIn.keySet()) {
            // Forge uses names starting with _ for metadata
            if (key.getPath().startsWith("_")) {
                continue;
            }
            try {
                // Possible IllegalStateException will be caught
                JsonObject json = mapIn.get(key).getAsJsonObject();
                Breed<Gene> b = deserializeBreed(json);
                if (b != null) {
                    breeds.put(key, b);
                }
            }
            catch (IllegalStateException e) {
                HorseColors.logger.error("Could not parse json: " + key);
            }
            catch (ClassCastException e) {
                HorseColors.logger.error("Unexpected data type in json: " + key);
            }
            HorseColors.logger.debug("Loaded " + breeds.size() 
                + " breed data files");
        }
    }

    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(instance);
    }

    private static Breed<Gene> deserializeBreed(JsonObject json) 
        throws ClassCastException, IllegalStateException
    {
        Breed<Gene> breed = new Breed<>(Gene.class);
        if (json.has("genes")) {
            JsonObject genesJson = (JsonObject)json.get("genes");
            for (Map.Entry<String, JsonElement> entry : genesJson.entrySet()) {
                JsonArray jarray = entry.getValue().getAsJsonArray();
                ArrayList<Float> frequencies = new ArrayList<>();
                for (int i = 0; i < jarray.size(); ++i) {
                    frequencies.add(jarray.get(i).getAsFloat());
                }
                breed.genes.put(Gene.valueOf(entry.getKey()), frequencies);
            }
        }
        return breed;
    }

    public static Breed<Gene> getBreed(ResourceLocation name) {
        return breeds.get(name);
    }
}
