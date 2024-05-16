package sekelsta.horse_colors.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class CAutobreedPacket {
    public int entityID;
    public boolean allowed;

    public CAutobreedPacket(int entityID, boolean allowed) {
        this.entityID = entityID;
        this.allowed = allowed;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityID);
        buffer.writeBoolean(this.allowed);
    }

    public static CAutobreedPacket decode(FriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        boolean allowed = buffer.readBoolean();
        return new CAutobreedPacket(id, allowed);
    }

    public static void handleServerside(CAutobreedPacket packet, Supplier<Context> context) {
        ServerPlayer sender = context.get().getSender();
        if (sender == null) {
            throw new RuntimeException("Expected handled CAutobreed packet to be sent from client to server");
        }

        // Enqueue anything that needs to be thread-safe
        context.get().enqueueWork(() -> {
            Entity entity = sender.level().getEntity(packet.entityID);
            ((AbstractHorseGenetic)entity).setAutobreedable(packet.allowed);
        });
        context.get().setPacketHandled(true);
    }
}
