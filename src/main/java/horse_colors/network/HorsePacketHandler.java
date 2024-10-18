package sekelsta.horse_colors.network;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import sekelsta.horse_colors.HorseColors;

public class HorsePacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(final RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(HorseColors.MODID)
                .versioned(PROTOCOL_VERSION);
        registrar.play(CAutobreedPacket.ID, CAutobreedPacket::decode, CAutobreedPacket::handleServerside);
    }
}
