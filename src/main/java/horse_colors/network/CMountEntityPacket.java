package sekelsta.horse_colors.network;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.genetics.IGeneticEntity;
import sekelsta.horse_colors.config.HorseConfig;

public class CMountEntityPacket {
    private int entityId;

    public CMountEntityPacket() {}

    public CMountEntityPacket(int entityId) {
        this.entityId = entityId;
    }

    public CMountEntityPacket(Entity entity) {
        this(entity.getId());
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.entityId);
    }

    public static CMountEntityPacket decode(PacketBuffer buffer) {
        int id = buffer.readVarInt();
        return new CMountEntityPacket(id);
    }

    private boolean tryMounting(Entity rider, Entity mount) {
        // Ban boats and whatnot from riding
        if (!(rider instanceof LivingEntity)) {
            return false;
        }
        // No aquatic riders
        if (rider instanceof WaterMobEntity) {
            return false;
        }
        if (!(mount instanceof IGeneticEntity)) {
            AxisAlignedBB riderBox = rider.getBoundingBox();
            AxisAlignedBB mountBox = mount.getBoundingBox();
            if (riderBox.getSize() * 2 > mountBox.getSize()) {
                return false;
            }
            double riderSize = riderBox.getXsize() * riderBox.getYsize() * riderBox.getZsize();
            if (rider instanceof AnimalEntity) {
                riderSize *= 2;
            }
            if (rider instanceof AgeableEntity && ((AgeableEntity)rider).isBaby()) {
                riderSize *= 2;
            }
            if (riderSize * 2 > 
                    mountBox.getXsize() * mountBox.getYsize() * mountBox.getZsize()) {
                return false;
            }
        }
        return rider.startRiding(mount);
    }

    // Helper for handle which runs on the main thread
    // This is a separate method from handle so that return statements can
    // be used for control flow, while still marking the packet handled
    // afterwards.
    private void handleMain(Context context) {
        ServerPlayerEntity sender = context.getSender();
        Entity target = sender.level.getEntity(this.entityId);
        boolean extra = HorseConfig.COMMON.mountingTweaks.get() > 1;
        if (target == null) {
            HorseColors.logger.warn("Could not find entity with id " + this.entityId + " requested by " 
                + sender.getName().getString());
            return;
        }
        // Cannot use this to move hostile mobs
        if (!target.getType().getCategory().isFriendly()) {
            return;
        }
        // Cannot control other players
        if (target instanceof PlayerEntity) {
            return;
        }
        // If mounted and the creature fits, mount it behind you
        if (sender.isPassenger()) {
            Entity mount = sender.getVehicle();
            if (mount instanceof IGeneticEntity || extra) {
                if (tryMounting(target, mount)) {
                    return;
                }
            }
        }
        // Otherwise try to mount it on an entity leashed to you
        List<MobEntity> entities = sender.level.getEntitiesOfClass(
            MobEntity.class, 
            sender.getBoundingBox().inflate(9, 4, 9),
            (entity) -> {
                return entity != target 
                    && entity.getLeashHolder() == sender
                    && (entity instanceof IGeneticEntity || extra);
            }
        );
        for (MobEntity entity : entities) {
            if (tryMounting(target, entity)) {
                return;
            }
        }
        // Else if the target is not being ridden by a player, dismount all
        // its passengers
        if (target instanceof IGeneticEntity || extra) {
            for (Entity passenger : target.getPassengers()) {
                if (passenger instanceof PlayerEntity && passenger != sender) {
                    return;
                }
            }
            // Only if the target had a passenger before the click, though
            if (target.getPassengers().size() == 1 
                    && target.getControllingPassenger() == sender) {
                return;
            }
            target.ejectPassengers();
        }
    }

    public void handle(Supplier<Context> context) {
        // Enqueue anything that needs to be thread-safe
        context.get().enqueueWork(() -> {
            if (HorseConfig.COMMON.mountingTweaks.get() > 0) {
                handleMain(context.get());
            }
        });
        context.get().setPacketHandled(true);
    }
}
