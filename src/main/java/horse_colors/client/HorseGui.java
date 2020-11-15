package sekelsta.horse_colors.client;
import net.minecraft.client.gui.screen.inventory.*;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.HorseInventoryScreen;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.HorseColors;

@OnlyIn(Dist.CLIENT)
public class HorseGui extends HorseInventoryScreen {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(HorseColors.MODID, "textures/gui/horse.png");
    // The EntityHorse whose inventory is currently being accessed.
    // This is a copy of super's horseEntity field for access without reflection.
    private final AbstractHorseGenetic horseGenetic;

    public HorseGui(HorseInventoryContainer container, PlayerInventory playerInventory, AbstractHorseGenetic horse) {
        super(container, playerInventory, horse);
        this.horseGenetic = horse;
    }

   /**
    * Draws the background layer of this container (behind the items).
    */
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE_LOCATION);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        if (this.horseGenetic instanceof AbstractChestedHorseEntity) {
            AbstractChestedHorseEntity abstractchestedhorseentity = (AbstractChestedHorseEntity)this.horseGenetic;
            if (abstractchestedhorseentity.hasChest()) {
                this.blit(matrixStack, i + 79, j + 17, 0, this.ySize, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
            }
        }

        // func_230264_L__() = canBeSaddled()
        if (this.horseGenetic.func_230264_L__()) {
            this.blit(matrixStack, i + 7, j + 35 - 18, 18, this.ySize + 54, 18, 18);
        }

        // func_230276_fq_() = wearsArmor()
        if (this.horseGenetic.func_230276_fq_()) {
            // If it were a llama would draw the carpet like this
            // this.blit(i + 7, j + 35, 36, this.ySize + 54, 18, 18);
            // But it's not, so draw the armor slot
            this.blit(matrixStack, i + 7, j + 35, 0, this.ySize + 54, 18, 18);
        }

        if (HorseConfig.isGenderEnabled()) {
            int iconWidth = 10;
            int iconHeight = 11;
            int textureX = 176;
            int renderX = i + 157;
            int renderY = j + 4;
            if (this.horseGenetic.isMale()) {
                textureX += iconWidth;
            }
            int textureY = 0;
            if (this.horseGenetic.isPregnant()) {
                renderX -= 2;
                int pregRenderX = renderX + iconWidth + 1;
                // Blit pregnancy background
                this.blit(matrixStack, pregRenderX, renderY + 1, 181, 23, 2, 10);
                // Blit pregnancy foreground based on progress
                int pregnantAmount = (int)(11 * horseGenetic.getPregnancyProgress());
                this.blit(matrixStack, pregRenderX, renderY + 11 - pregnantAmount, 177, 33 - pregnantAmount, 2, pregnantAmount);
            }
            // Blit gender icon
            // X, y to render to, x, y to render from, width, height
            this.blit(matrixStack, renderX, renderY, textureX, textureY, iconWidth, iconHeight);
        }

        InventoryScreen.drawEntityOnScreen(i + 51, j + 60, 17, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, this.horseGenetic);
    }

    public static void replaceGui(GuiOpenEvent event) {
        if (event.getGui() instanceof HorseInventoryScreen) {
            HorseInventoryScreen screen = (HorseInventoryScreen)event.getGui();
            AbstractHorseEntity horse = null;
            try {
                // field_147034_x = horseGenetic
                horse = ObfuscationReflectionHelper.getPrivateValue(HorseInventoryScreen.class, screen, "field_147034_x");
            }
            catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
                System.err.println("Unable to access private value horseGenetic while replacing the horse GUI.");
                System.err.println(e);
            }
            if (horse instanceof AbstractHorseGenetic) {
                AbstractHorseGenetic horseGenetic = (AbstractHorseGenetic)horse;
                PlayerInventory inventory = null;
                try {
                    // field_213127_e = playerInventory
                    inventory = ObfuscationReflectionHelper.getPrivateValue(ContainerScreen.class, screen, "field_213127_e");
                }
                catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
                    System.err.println("Unable to access private value playerInventory while replacing the horse GUI.");
                    System.err.println(e);
                }
                event.setGui(new HorseGui(screen.getContainer(), inventory, horseGenetic));
            }
        }
    }
}
