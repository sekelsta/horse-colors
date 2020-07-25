package sekelsta.horse_colors.client;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.genetics.Genome;

@SideOnly(Side.CLIENT)
public class GeneBookScreen extends GuiScreen {
    private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation(HorseColors.MODID + ":textures/gui/book.png");
    private static final int linesPerPage = 14;
    private static final int lineWrapWidth = 124;
    private static final int bookWidth = 360;
    private static final int bookHeight = 192;
    private static final int pageWidth = 130;
    private static final int pageCrease = 10;
    int currPage = 0;
    private Genome genome;
    private List<List<String>> contents;
    private List<String> pages;

    private ChangePageButton buttonNextPage;
    private ChangePageButton buttonPreviousPage;
    private GuiButton buttonDone;
    /** Determines if a sound is played when the page is turned */
    private final boolean pageTurnSounds = true;

    public GeneBookScreen(Genome genomeIn) {
        this.genome = genomeIn;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Render the book picture in the back
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        int x = (this.width - bookWidth) / 2;
        int y = 2;
        this.drawModalRectWithCustomSizedTexture(x, y, 0, 0, bookWidth, bookHeight, 512, 256);

        renderPage(currPage, this.width / 2 - pageWidth - pageCrease);
        if (this.getPageCount() > currPage + 1) {
            renderPage(currPage + 1, this.width / 2 + pageCrease);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void renderPage(int pagenum, int x) {
        String pageindicator = I18n.format("book.pageIndicator", pagenum + 1, this.getPageCount());
        String pagetext = this.getPageText(pagenum);
        int j1 = this.getTextWidth(pageindicator);
        this.fontRenderer.drawString(pageindicator, x - j1 + pageWidth, 18, 0);
        // String, x, y, wrapwidth, textcolor
        this.fontRenderer.drawSplitString(pagetext, x, 32, lineWrapWidth, 0);
    }

    @Override
    public void initGui() {
        this.contents = genome.getBookContents();
        this.pages = new ArrayList<String>();
        for (int ch = 0; ch < contents.size(); ++ch) {
            String s = "";
            int lines = 0;
            for (int ln = 0; ln < contents.get(ch).size(); ++ln) {
                String text = contents.get(ch).get(ln);
                int wrapped = fontRenderer.listFormattedStringToWidth(text, lineWrapWidth).size();
                if (lines + wrapped > linesPerPage && lines > 0) {
                    pages.add(s);
                    s = "";
                    lines = 0;
                }
                lines += wrapped;
                s += text + "\n";
            }
            pages.add(s);
        }
        this.addDoneButton();
        this.addChangePageButtons();
    }

    protected void addDoneButton() {
        // Unlike in 1.14+, event handling is done by a function from here
        this.buttonDone = this.addButton(new GuiButton(0, this.width / 2 - 100, 196, 200, 20, I18n.format("gui.done")));
    }

    protected void addChangePageButtons() {
        int x1 = (this.width - bookWidth) / 2;
        int x2 = (this.width + bookWidth) / 2;
        int j = 2;
        int y = 159;/*
        this.buttonNextPage = this.addButton(new ChangePageButton(x2 - 43 - 24, y, true, (p_214159_1_) -> {
            this.nextPage();
        }, this.pageTurnSounds));
        this.buttonPreviousPage = this.addButton(new ChangePageButton(x1 + 43, y, false, (p_214158_1_) -> {
            this.previousPage();
        }, this.pageTurnSounds));*/
        // TODO
        this.buttonNextPage = (ChangePageButton)this.addButton(new ChangePageButton(1, x2 - 43 - 24, y, true));
        this.buttonPreviousPage = (ChangePageButton)this.addButton(new ChangePageButton(2, x1 + 43, y, false));
        this.updateButtons();
    }

    private int getPageCount() {
        return pages.size();
    }
   /**
    * Moves the display back one page
    */
    protected void previousPage() {
        this.currPage = Math.max(this.currPage - 2, 0);
        this.updateButtons();
    }

   /**
    * Moves the display forward one page
    */
   protected void nextPage() {
      if (this.currPage < this.getPageCount() - 2) {
         this.currPage += 2;
      }

      this.updateButtons();
   }

   private void updateButtons() {
      this.buttonNextPage.visible = this.currPage < this.getPageCount() - 2;
      this.buttonPreviousPage.visible = this.currPage > 0;
   }

    private String getPageText(int pagenum) {
        if (pages.size() > 0) {
            return pages.get(pagenum);
        }
        else {
            return "";
        }
    }

    private int getTextWidth(String text) {
        return this.fontRenderer.getStringWidth(text);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0) // Done
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (button.id == 1) // Next page
            {
                this.nextPage();
            }
            else if (button.id == 2) // Previous page
            {
                this.previousPage();
            }

            this.updateButtons();
        }
    }

    @SideOnly(Side.CLIENT)
    static class ChangePageButton extends GuiButton
        {
            private final boolean isForward;

            public ChangePageButton(int buttonId, int x, int y, boolean isForwardIn)
            {
                super(buttonId, x, y, 23, 13, "");
                this.isForward = isForwardIn;
            }

            /**
             * Draws this button to the screen.
             */
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
            {
                if (this.visible)
                {
                    boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(GeneBookScreen.BOOK_GUI_TEXTURES);
                    int i = 0;
                    int j = 192;

                    if (flag)
                    {
                        i += 23;
                    }

                    if (!this.isForward)
                    {
                        j += 13;
                    }

                    this.drawModalRectWithCustomSizedTexture(this.x, this.y, i, j, 23, 13, 512, 256);
                }
            }
        }
}
