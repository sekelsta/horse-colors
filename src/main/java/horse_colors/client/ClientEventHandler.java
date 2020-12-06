package sekelsta.horse_colors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.network.CMountEntityPacket;
import sekelsta.horse_colors.network.HorseColorsPacketHandler;

public class ClientEventHandler {
    @OnlyIn(Dist.CLIENT)
    public static void handleInteract(EntityInteract event) {
        // Check if the config setting allows the mounting tweaks
        if (HorseConfig.COMMON.mountingTweaks.get() < 1) {
            return;
        }
        // Check if holding control / sprint key
        if (Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown()) {
            CMountEntityPacket packet = new CMountEntityPacket(event.getTarget());
            HorseColorsPacketHandler.CHANNEL.sendToServer(packet);
        }
    }
}
