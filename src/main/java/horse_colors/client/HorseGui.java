package sekelsta.horse_colors.client;
import net.minecraft.client.gui.screen.inventory.*;

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
public class HorseGui extends ContainerScreen<HorseInventoryContainer> {
   private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation(HorseColors.MODID, "textures/gui/horse.png");
   /** The EntityHorse whose inventory is currently being accessed. */
   private final AbstractHorseGenetic horseEntity;
   /** The mouse x-position recorded during the last rendered frame. */
   private float mousePosx;
   /** The mouse y-position recorded during the last renderered frame. */
   private float mousePosY;

   public HorseGui(HorseInventoryContainer container, PlayerInventory playerInventory, AbstractHorseGenetic horse) {
      super(container, playerInventory, horse.getDisplayName());
      this.horseEntity = horse;
      this.passEvents = false;
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
        if (this.horseEntity instanceof AbstractChestedHorseEntity) {
            AbstractChestedHorseEntity abstractchestedhorseentity = (AbstractChestedHorseEntity)this.horseEntity;
            if (abstractchestedhorseentity.hasChest()) {
                this.blit(i + 79, j + 17, 0, this.ySize, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
            }
        }

        if (this.horseEntity.canBeSaddled()) {
            this.blit(i + 7, j + 35 - 18, 18, this.ySize + 54, 18, 18);
        }

        if (this.horseEntity.wearsArmor()) {/*
            if (this.horseEntity instanceof LlamaEntity) {
                this.blit(i + 7, j + 35, 36, this.ySize + 54, 18, 18);
            } else {*/
                this.blit(i + 7, j + 35, 0, this.ySize + 54, 18, 18);
            //}
        }

        if (HorseConfig.isGenderEnabled()) {
            int iconWidth = 10;
            int iconHeight = 11;
            int textureX = 176;
            if (this.horseEntity.isMale()) {
                textureX += iconWidth;
            }
            int textureY = 0;
            if (this.horseEntity.isCastrated() || this.horseEntity.isPregnant()) {
                textureY += iconHeight;
            }
            // X, y to render to, x, y to render from, width and height in unknown order
            this.blit(i + 157, j + 4, textureX, textureY, iconWidth, iconHeight);
        }

        InventoryScreen.drawEntityOnScreen(i + 51, j + 60, 17, (float)(i + 51) - this.mousePosx, (float)(j + 75 - 50) - this.mousePosY, this.horseEntity);
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.mousePosx = (float)p_render_1_;
        this.mousePosY = (float)p_render_2_;
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    public static void replaceGui(GuiOpenEvent event) {
        if (event.getGui() instanceof HorseInventoryScreen) {
            HorseInventoryScreen screen = (HorseInventoryScreen)event.getGui();
            AbstractHorseEntity horse = null;
            try {
                horse = ObfuscationReflectionHelper.getPrivateValue(HorseInventoryScreen.class, screen, "horseEntity");
            }
            catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
                System.err.println("Unable to access private value horseEntity while replacing the horse GUI.");
                System.err.println(e);
            }
            if (horse instanceof AbstractHorseGenetic) {
                AbstractHorseGenetic horseGenetic = (AbstractHorseGenetic)horse;
                PlayerInventory inventory = null;
                try {
                    inventory = ObfuscationReflectionHelper.getPrivateValue(ContainerScreen.class, screen, "playerInventory");
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
