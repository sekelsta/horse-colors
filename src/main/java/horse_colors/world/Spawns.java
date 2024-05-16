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

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.List;

import sekelsta.horse_colors.HorseConfig;
import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.HorseColors;

@Mod.EventBusSubscriber(modid = HorseColors.MODID)
public class Spawns {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_DEFERRED = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, HorseColors.MODID);

    private static final RegistryObject<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZER = RegistryObject.create(new ResourceLocation(HorseColors.MODID, "biome_spawn_serializer"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, HorseColors.MODID);

    public record EquineBiomeModifier(SpawnerData plainsSpawner, SpawnerData savannaSpawner) implements BiomeModifier {
        @Override
        public void modify(Holder<Biome> biomeHolder, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                if (biomeHolder.containsTag(Tags.Biomes.IS_PLAINS)) {
                    builder.getMobSpawnSettings().addSpawn(plainsSpawner.type.getCategory(), plainsSpawner);
                }
                else if (biomeHolder.containsTag(BiomeTags.IS_SAVANNA)) {
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
