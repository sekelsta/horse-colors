package sekelsta.horse_colors;

import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class ContainerEventHandler {
    public static void editContainer(PlayerContainerEvent.Open event) {
        if (!(event.getContainer() instanceof HorseInventoryContainer)) {
            return;
        }
        HorseInventoryContainer horseContainer = (HorseInventoryContainer)event.getContainer();
        AbstractHorseEntity horse = null;
        try {
            // , "field_188516_a"
            horse = ObfuscationReflectionHelper.getPrivateValue(HorseInventoryContainer.class, horseContainer, "field_111242_f");
        }
        catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            System.err.println("Unable to access private value horse while replacing the horse container.");
            System.err.println(e);
        }

        if (horse == null || !(horse instanceof AbstractHorseGenetic)) {
            return;
        }
        AbstractHorseGenetic horseGenetic = (AbstractHorseGenetic)horse;
        replaceSaddleSlot(horseGenetic, horseContainer);
    }

    // Replace the saddle slot with one that accepts alternate saddles
    // This is called both on the server side from here and on theclient side from HorseGui
    public static void replaceSaddleSlot(AbstractHorseGenetic horse, Container container) {
        // This isn't getting called on dedicated servers, even though it is getting called on the server thread of an integrated client
        Slot saddleSlot = new Slot(horse.getHorseChest(), 0, 8, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return horse.isSaddle(stack) && !this.hasItem() && horse.isSaddleable();
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isEnabled() {
                return horse.isSaddleable();
            }
        };
        container.slots.set(0, saddleSlot);
    }
}
