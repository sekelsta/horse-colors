package sekelsta.horse_colors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import org.jetbrains.annotations.NotNull;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.ContainerEventHandler;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.HorseColors;


@OnlyIn(Dist.CLIENT)
public class HorseGui extends HorseInventoryScreen {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(HorseColors.MODID, "textures/gui/horse.png");
    // The EntityHorse whose inventory is currently being accessed.
    // This is a copy of super's horseEntity field for access without reflection.
    private final AbstractHorseGenetic horseGenetic;

    public HorseGui(HorseInventoryMenu container, Inventory playerInventory, AbstractHorseGenetic horse) {
        super(container, playerInventory, horse);
        this.horseGenetic = horse;
    }

   /**
    * Draws the background layer of this container (behind the items).
    */
    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.horseGenetic instanceof AbstractChestedHorse) {
            AbstractChestedHorse abstractchestedhorseentity = (AbstractChestedHorse)this.horseGenetic;
            if (abstractchestedhorseentity.hasChest()) {
                this.blit(matrixStack, i + 79, j + 17, 0, this.imageHeight, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
            }
        }

        if (this.horseGenetic.isSaddleable()) {
            this.blit(matrixStack, i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);
        }

        if (this.horseGenetic instanceof HorseGeneticEntity) {
            // Draw the armor slot
            this.blit(matrixStack, i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
        }
        else {
            // Draw carpet slot
            this.blit(matrixStack, i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);
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
            boolean grayIcons = HorseConfig.COMMON.useGeneticAnimalsIcons.get();
            if (grayIcons) {
                textureX += 2 * iconWidth;
            }
            // Render pregnancy progress bar
            if (this.horseGenetic.isPregnant() && !grayIcons) {
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

            // Render genetic animals pregnancy progress indicator
            if (this.horseGenetic.isPregnant() && grayIcons) {
                // Blit pregnancy foreground based on progress
                int pregnantAmount = (int)(10 * horseGenetic.getPregnancyProgress()) + 1;
                this.blit(matrixStack, renderX, renderY + 11 - pregnantAmount, textureX, textureY + iconHeight + 11 - pregnantAmount, iconWidth, pregnantAmount);
            }
        }

        InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, this.horseGenetic);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        super.renderLabels(matrixStack, x, y);
        if (!HorseConfig.COMMON.enableSizes.get() || horseGenetic.isBaby() || horseGenetic.hasChest()) {
            // Avoid showing an inaccurate height for foals
            return;
        }
        float height = horseGenetic.getGenome().getGeneticHeightCm();
        int cm = Math.round(height);
        int inches = Math.round(height / 2.54f);
        int hands = inches / 4;
        int point = inches % 4;
        String translationKey = HorseColors.MODID + ".gui.height";
        Component heightText = Component.translatable(translationKey, hands, point, cm);
        String heightString = heightText.getString(1000);
        int yy = 20;
        for (String line : heightString.split("\n")) {
            // matrix stack, text, x, y, color
            this.font.draw(matrixStack, Component.literal(line), 82, yy, 0x404040);
            yy += 9;
        }
        if (horseGenetic.isTooSmallForPlayerToRide()) {
            this.font.draw(matrixStack, Component.translatable(HorseColors.MODID + ".gui.miniature"), 82, yy, 0x404040);
        }
        else if (horseGenetic.getGenome().isLarge()) {
            this.font.draw(matrixStack, Component.translatable(HorseColors.MODID + ".gui.large"), 82, yy, 0x404040);
        }
    }

    public static void replaceGui(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof HorseInventoryScreen) {
            HorseInventoryScreen screen = (HorseInventoryScreen)event.getScreen();
            AbstractHorse horse = null;
            try {
                // f_98812_ = horse
                horse = ObfuscationReflectionHelper.getPrivateValue(HorseInventoryScreen.class, screen, "f_98812_");
            }
            catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
                System.err.println("Unable to access private value horseGenetic while replacing the horse GUI.");
                System.err.println(e);
            }
            if (horse instanceof AbstractHorseGenetic) {
                AbstractHorseGenetic horseGenetic = (AbstractHorseGenetic)horse;
                Inventory inventory = new Inventory(null);
                ContainerEventHandler.replaceSaddleSlot(horseGenetic, screen.getMenu());
                event.setNewScreen(new HorseGui(screen.getMenu(), inventory, horseGenetic));
            }
        }
    }
}
