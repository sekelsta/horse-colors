package sekelsta.horse_colors.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class CAutobreedPayload implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(HorseColors.MODID, "cautobreed");

    public int entityID;
    public boolean allowed;

    public CAutobreedPayload(int entityID, boolean allowed) {
        this.entityID = entityID;
        this.allowed = allowed;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityID);
        buffer.writeBoolean(this.allowed);
    }

    public static CAutobreedPayload decode(FriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        boolean allowed = buffer.readBoolean();
        return new CAutobreedPayload(id, allowed);
    }

    public void handleServerside(PlayPayloadContext context) {
        Player sender = context.player().get();
        if (sender == null) {
            throw new RuntimeException("Expected handled CAutobreed payload to be sent from client to server");
        }

        // Enqueue anything that needs to be thread-safe
        context.workHandler().execute(() -> {
            Entity entity = sender.level().getEntity(this.entityID);
            ((AbstractHorseGenetic)entity).setAutobreedable(this.allowed);
        });
    }
}
