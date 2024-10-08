package sekelsta.horse_colors.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.structure.pools.*;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.List;

import sekelsta.horse_colors.HorseConfig;
import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.HorseColors;

@Mod.EventBusSubscriber(modid = HorseColors.MODID)
public class Spawns {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_DEFERRED = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, HorseColors.MODID);

    private static final DeferredHolder<Codec<? extends BiomeModifier>, Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, new ResourceLocation(HorseColors.MODID, "biome_spawn_serializer"));

    public record EquineBiomeModifier(SpawnerData plainsSpawner, SpawnerData savannaSpawner) implements BiomeModifier {
        @Override
        public void modify(Holder<Biome> biomeHolder, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                if (biomeHolder.is(Tags.Biomes.IS_PLAINS)) {
                    builder.getMobSpawnSettings().addSpawn(plainsSpawner.type.getCategory(), plainsSpawner);
                }
                else if (biomeHolder.is(BiomeTags.IS_SAVANNA)) {
                    builder.getMobSpawnSettings().addSpawn(savannaSpawner.type.getCategory(), savannaSpawner);
                }
            }
            else if (phase == Phase.REMOVE) {
                List<SpawnerData> spawns = builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE);
                spawns.removeIf(EquineBiomeModifier::shouldRemove);
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return BIOME_MODIFIER_SERIALIZER.get();
        }

        private static boolean shouldRemove(SpawnerData spawner) {
            return spawner.type == EntityType.HORSE || spawner.type == EntityType.DONKEY;
        }
    }

    public static void registerBiomeModifiers() {
        Codec<EquineBiomeModifier> codec = 
            RecordCodecBuilder.create(builder -> builder.group(
                    SpawnerData.CODEC.fieldOf("plains_spawner").forGetter(EquineBiomeModifier::plainsSpawner),
                    SpawnerData.CODEC.fieldOf("savanna_spawner").forGetter(EquineBiomeModifier::savannaSpawner)
            ).apply(builder, EquineBiomeModifier::new));
        BIOME_MODIFIER_DEFERRED.register("equine_spawn", () -> codec);
    }

    @SubscribeEvent
    public static void neoforge_issue_939_workaround(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof AbstractHorseGenetic && event.getSpawnData() == null) {
            event.setSpawnData(new AbstractHorseGenetic.GeneticData(((AbstractHorseGenetic)event.getEntity()).getRandomBreed()));
        }
    }
}
