package sekelsta.horse_colors.client;
import net.minecraft.client.gui.inventory.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.HorseColors;

@SideOnly(Side.CLIENT)
public class HorseGui extends GuiContainer {
    private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation(HorseColors.MODID, "textures/gui/horse.png");
    /** The player inventory bound to this GUI. */
    private final IInventory playerInventory;
    /** The horse inventory bound to this GUI. */
    private final IInventory horseInventory;
    /** The EntityHorse whose inventory is currently being accessed. */
    private final AbstractHorseGenetic horseEntity;
    /** The mouse x-position recorded during the last rendered frame. */
    private float mousePosx;
    /** The mouse y-position recorded during the last renderered frame. */
    private float mousePosY;

    ITextComponent title;

    public HorseGui(IInventory playerInventory, AbstractHorseGenetic horse) {
        super(new ContainerHorseInventory(playerInventory, horse.getInventory(), horse, Minecraft.getMinecraft().player));
        this.playerInventory = playerInventory;
        this.horseInventory = horse.getInventory();
        this.horseEntity = horse;
        this.title = horse.getDisplayName();
        this.allowUserInput = false;
    }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.title.getFormattedText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

   /**
    * Draws the background layer of this container (behind the items).
    */
    @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        if (this.horseEntity instanceof AbstractChestHorse) {
            AbstractChestHorse abstractchestedhorseentity = (AbstractChestHorse)this.horseEntity;
            if (abstractchestedhorseentity.hasChest()) {
                this.drawTexturedModalRect(i + 79, j + 17, 0, this.ySize, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
            }
        }

        if (this.horseEntity.canBeSaddled()) {
            this.drawTexturedModalRect(i + 7, j + 35 - 18, 18, this.ySize + 54, 18, 18);
        }

        if (this.horseEntity.wearsArmor()) {/*
            if (this.horseEntity instanceof EntityLlama) {
                this.drawTexturedModalRect(i + 7, j + 35, 36, this.ySize + 54, 18, 18);
            } else {*/
                this.drawTexturedModalRect(i + 7, j + 35, 0, this.ySize + 54, 18, 18);
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
            this.drawTexturedModalRect(i + 157, j + 4, textureX, textureY, iconWidth, iconHeight);
        }

        GuiInventory.drawEntityOnScreen(i + 51, j + 60, 17, (float)(i + 51) - this.mousePosx, (float)(j + 75 - 50) - this.mousePosY, this.horseEntity);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.mousePosx = (float)mouseX;
        this.mousePosY = (float)mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public static void replaceGui(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiScreenHorseInventory) {
            GuiScreenHorseInventory screen = (GuiScreenHorseInventory)event.getGui();
            // field_147034_x = horseEntity
            AbstractHorse horse = ObfuscationReflectionHelper.getPrivateValue(GuiScreenHorseInventory.class, screen, "field_147034_x");
            if (horse instanceof AbstractHorseGenetic) {
                AbstractHorseGenetic horseGenetic = (AbstractHorseGenetic)horse;
                // field_147030_v = playerInventory (1.12)
                // field_213127_e = playerInventory
                IInventory inventory = ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, screen, "field_147030_v");
                event.setGui(new HorseGui(inventory, horseGenetic));
            }
        }
    }
}
