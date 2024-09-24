package sekelsta.horse_colors.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;

import org.jetbrains.annotations.NotNull;
import sekelsta.horse_colors.HorseConfig;
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
    private final int autobreedIconWidth = 12;
    private final int autobreedIconHeight = 10;
    private int autobreedRenderX;
    private int autobreedRenderY;

    public HorseGui(HorseInventoryMenu container, Inventory playerInventory, AbstractHorseGenetic horse) {
        super(container, playerInventory, horse);
        this.horseGenetic = horse;
    }

   /**
    * Draws the background layer of this container (behind the items).
    */
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.horseGenetic instanceof AbstractChestedHorse) {
            AbstractChestedHorse abstractchestedhorseentity = (AbstractChestedHorse)this.horseGenetic;
            if (abstractchestedhorseentity.hasChest()) {
                guiGraphics.blit(TEXTURE_LOCATION, i + 79, j + 17, 0, this.imageHeight, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
            }
        }

        if (this.horseGenetic.isSaddleable()) {
            guiGraphics.blit(TEXTURE_LOCATION, i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);
        }

        if (this.horseGenetic instanceof HorseGeneticEntity) {
            // Draw the armor slot
            guiGraphics.blit(TEXTURE_LOCATION, i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
        }
        else {
            // Draw carpet slot
            guiGraphics.blit(TEXTURE_LOCATION, i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);
        }

        int genderIconRenderX = i + 168;
        if (HorseConfig.isGenderEnabled()) {
            int iconWidth = 10;
            int iconHeight = 11;
            int textureX = 176;
            genderIconRenderX -= iconWidth + 1;
            int renderY = j + 4;
            if (this.horseGenetic.isMale()) {
                textureX += iconWidth;
            }
            int textureY = 0;
            boolean grayIcons = HorseConfig.COMMON.useGeneticAnimalsIcons.get();
            if (grayIcons) {
                textureX += 2 * iconWidth;
            }
            if (!horseGenetic.isFertile()) {
                textureY = 11;
            }
            // Render pregnancy progress bar
            if (this.horseGenetic.isPregnant() && !grayIcons) {
                genderIconRenderX -= 2;
                int pregRenderX = genderIconRenderX + iconWidth + 1;
                // Blit pregnancy background
                guiGraphics.blit(TEXTURE_LOCATION, pregRenderX, renderY + 1, 181, 23, 2, 10);
                // Blit pregnancy foreground based on progress
                int pregnantAmount = (int)(11 * horseGenetic.getPregnancyProgress());
                guiGraphics.blit(TEXTURE_LOCATION, pregRenderX, renderY + 11 - pregnantAmount, 177, 33 - pregnantAmount, 2, pregnantAmount);
            }
            // Blit gender icon
            // X, y to render to, x, y to render from, width, height
            guiGraphics.blit(TEXTURE_LOCATION, genderIconRenderX, renderY, textureX, textureY, iconWidth, iconHeight);

            // Render genetic animals pregnancy progress indicator
            if (this.horseGenetic.isPregnant() && grayIcons) {
                // Blit pregnancy foreground based on progress
                int pregnantAmount = (int)(10 * horseGenetic.getPregnancyProgress()) + 1;
                guiGraphics.blit(TEXTURE_LOCATION, genderIconRenderX, renderY + 11 - pregnantAmount, textureX, iconHeight + 22 - pregnantAmount, iconWidth, pregnantAmount);
            }
        }
        if (HorseConfig.BREEDING.autobreeding.get()) {
            int textureX = 177;
            int textureY = 36;
            if (horseGenetic.isAutobreedable()) {
                textureX += autobreedIconWidth + 1;
            }
            int renderX = genderIconRenderX - autobreedIconWidth - 1;
            if (horseGenetic.isMale()) {
                renderX -= 2;
            }
            int renderY = j + 5;
            autobreedRenderX = renderX;
            autobreedRenderY = renderY;
            guiGraphics.blit(TEXTURE_LOCATION, renderX, renderY, textureX, textureY, autobreedIconWidth, autobreedIconHeight);
            if (inAutobreedButton(mouseX, mouseY)) {
                guiGraphics.blit(TEXTURE_LOCATION, renderX - 1, renderY - 1, 203, textureY - 1, autobreedIconWidth + 2, autobreedIconHeight + 2);
            }
        }

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, i + 26, j + 18, i + 78, j + 70, 17, 0.25F, mouseX, mouseY, this.horseGenetic);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        super.renderLabels(guiGraphics, x, y);
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
            guiGraphics.drawString(this.font, Component.literal(line), 82, yy, 0x404040, false);
            yy += 9;
        }
        if (horseGenetic.isTooSmallForPlayerToRide()) {
            guiGraphics.drawString(this.font, Component.translatable(HorseColors.MODID + ".gui.miniature"), 82, yy, 0x404040, false);
        }
        else if (horseGenetic.getGenome().isLarge()) {
            guiGraphics.drawString(this.font, Component.translatable(HorseColors.MODID + ".gui.large"), 82, yy, 0x404040, false);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && HorseConfig.BREEDING.autobreeding.get() && inAutobreedButton(x, y)) {
            horseGenetic.setAutobreedable(!horseGenetic.isAutobreedable());
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    private final boolean inAutobreedButton(double x, double y) {
        return x >= autobreedRenderX && x < autobreedRenderX + autobreedIconWidth
            && y >= autobreedRenderY && y < autobreedRenderY + autobreedIconHeight;
    }

    public static void replaceGui(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof HorseInventoryScreen) {
            HorseInventoryScreen screen = (HorseInventoryScreen)event.getScreen();
            AbstractHorse horse = screen.horse;
            if (horse instanceof AbstractHorseGenetic) {
                AbstractHorseGenetic horseGenetic = (AbstractHorseGenetic)horse;
                Inventory inventory = new Inventory(null);
                ContainerEventHandler.replaceSaddleSlot(horseGenetic, screen.getMenu());
                event.setNewScreen(new HorseGui(screen.getMenu(), inventory, horseGenetic));
            }
        }
    }
}
