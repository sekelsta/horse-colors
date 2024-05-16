package sekelsta.horse_colors.network;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import sekelsta.horse_colors.HorseColors;

public class HorsePacketHandler {
    private static int ID = 0;

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        // Name
        new ResourceLocation(HorseColors.MODID, "main"),
        // Protocol version supplier
        () -> PROTOCOL_VERSION,
        // Predicate - client compatible protocol versions
        PROTOCOL_VERSION::equals,
        // Predicate - server compatible protocol versions
        PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        CHANNEL.registerMessage(ID++, CAutobreedPacket.class, CAutobreedPacket::encode,
            CAutobreedPacket::decode, CAutobreedPacket::handleServerside,
            Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void sendToServer(CAutobreedPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}
