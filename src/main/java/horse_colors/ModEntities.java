package felinoid.horse_colors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

public class ModEntities {
    public static EntityType<EntityHorseFelinoid> HORSE_FELINOID = null;

	@Mod.EventBusSubscriber(modid = HorseColors.MODID, bus = Bus.MOD)
	public static class RegistrationHandler {
		/**
		 * Register this mod's {@link Entity} types.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
			final EntityType<EntityHorseFelinoid> horseFelinoid = build(
					"horse_felinoid",
					EntityType.Builder.create(EntityHorseFelinoid.class, EntityHorseFelinoid::new)
			);

			event.getRegistry().registerAll(
					horseFelinoid
			);

			addSpawn(HORSE_FELINOID, HorseConfig.COMMON.spawnWeight.get(), HorseConfig.COMMON.minHerdSize.get(), HorseConfig.COMMON.maxHerdSize.get(), EnumCreatureType.CREATURE, getBiomes(BiomeDictionary.Type.PLAINS));
			addSpawn(HORSE_FELINOID, HorseConfig.COMMON.spawnWeight.get(), HorseConfig.COMMON.minHerdSize.get(), HorseConfig.COMMON.maxHerdSize.get(), EnumCreatureType.CREATURE, getBiomes(BiomeDictionary.Type.SAVANNA));
		}

		/**
		 * Build an {@link EntityType} from a {@link EntityType.Builder} using the specified name.
		 *
		 * @param name    The entity type name
		 * @param builder The entity type builder to build
		 * @return The built entity type
		 */
		private static <T extends Entity> EntityType<T> build(final String name, final EntityType.Builder<T> builder) {
			final ResourceLocation registryName = new ResourceLocation(HorseColors.MODID, name);

			final EntityType<T> entityType = builder
					.build(registryName.toString());

			entityType.setRegistryName(registryName);

			return entityType;
		}

		/**
		 * Add a spawn entry for the supplied entity in the supplied {@link Biome} list.
		 * <p>
		 * Adapted from Forge's {@code EntityRegistry.addSpawn} method in 1.12.2.
		 *
		 * @param entityType     The entity type
		 * @param itemWeight     The weight of the spawn list entry (higher weights have a higher chance to be chosen)
		 * @param minGroupCount  Min spawn count
		 * @param maxGroupCount  Max spawn count
		 * @param classification The entity classification
		 * @param biomes         The biomes to add the spawn to
		 */
		private static void addSpawn(final EntityType<? extends EntityLiving> entityType, final int itemWeight, final int minGroupCount, final int maxGroupCount, final EnumCreatureType classification, final Biome... biomes) {
			for (final Biome biome : biomes) {
				final List<Biome.SpawnListEntry> spawns = biome.getSpawns(classification);

				// Try to find an existing entry for the entity type
				spawns.stream()
						.filter(entry -> entry.entityType == entityType)
						.findFirst()
						.ifPresent(spawns::remove); // If there is one, remove it

				// Add a new one
				spawns.add(new Biome.SpawnListEntry(entityType, itemWeight, minGroupCount, maxGroupCount));
			}
		}

		/**
		 * Get an array of {@link Biome}s with the specified {@link BiomeDictionary.Type}.
		 *
		 * @param type The Type
		 * @return An array of Biomes
		 */
		private static Biome[] getBiomes(final BiomeDictionary.Type type) {
			return BiomeDictionary.getBiomes(type).toArray(new Biome[0]);
		}

	}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenders()
    {
		RenderingRegistry.registerEntityRenderingHandler(EntityHorseFelinoid.class, renderManager -> new RenderHorseFelinoid(renderManager));
	}

/*        // Horse
        String horse_name = "horse_felinoid";
		EntityEntry horse_entry = EntityEntryBuilder.create()
            .entity(EntityHorseFelinoid.class)
            // Last parameter is network ID, which needs to be unique per mod.
            .id(new ResourceLocation(HorseColors.MODID, horse_name), ID++)
            .name(horse_name)
            .egg(0x7F4320, 0x110E0D)
            .tracker(64, 2, false)
            .spawn(EnumCreatureType.CREATURE, HorseConfig.spawnWeight, 
                HorseConfig.minHerdSize, HorseConfig.maxHerdSize, 
                BiomeDictionary.getBiomes(Type.PLAINS))
            .spawn(EnumCreatureType.CREATURE, HorseConfig.spawnWeight, 
                HorseConfig.minHerdSize, HorseConfig.maxHerdSize, 
                BiomeDictionary.getBiomes(Type.SAVANNA))
            .build();
        event.getRegistry().register(horse_entry);
	}

    @OnlyIn(Dist.CLIENT)
    public void registerRenderers()
    {
        RenderHorseFelinoid horseRender = new RenderHorseFelinoid(Minecraft.getMinecraft().getRenderManager());
        RenderingRegistry.registerEntityRenderingHandler(EntityHorseFelinoid.class, horseRender);
	}*/
}
