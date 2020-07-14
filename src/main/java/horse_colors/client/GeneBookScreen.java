package sekelsta.horse_colors.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.*;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.genetics.Genome;

@OnlyIn(Dist.CLIENT)
public class GeneBookScreen extends Screen {
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
    /** Determines if a sound is played when the page is turned */
    private final boolean pageTurnSounds = true;

    public GeneBookScreen(Genome genomeIn) {
        super(NarratorChatListener.field_216868_a);
        this.genome = genomeIn;
    }


    public void render(int mouseX, int mouseY, float partialTicks) {
        // Render the book picture in the back
        this.renderBackground();
        this.setFocused((IGuiEventListener)null);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(new ResourceLocation(HorseColors.MODID + ":textures/gui/book.png"));
        int x = (this.width - bookWidth) / 2;
        int y = 2;
        //x = 0;
        this.blit(x, y, 0, 0, bookWidth, bookHeight, 512, 256);

        renderPage(currPage, this.width / 2 - pageWidth - pageCrease);
        if (this.getPageCount() > currPage + 1) {
            renderPage(currPage + 1, this.width / 2 + pageCrease);
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    private void renderPage(int pagenum, int x) {
        String pageindicator = I18n.format("book.pageIndicator", pagenum + 1, this.getPageCount());
        String pagetext = this.getPageText(pagenum);
        int j1 = this.getTextWidth(pageindicator);
        this.font.drawString(pageindicator, (float)(x - j1 + pageWidth), 18.0F, 0);
        // String, x, y, wrapwidth, textcolor
        this.font.drawSplitString(pagetext, x, 32, lineWrapWidth, 0);
    }

    protected void init() {
        this.contents = genome.getBookContents();
        this.pages = new ArrayList<String>();
        for (int ch = 0; ch < contents.size(); ++ch) {
            String s = "";
            int lines = 0;
            for (int ln = 0; ln < contents.get(ch).size(); ++ln) {
                String text = contents.get(ch).get(ln);
                int wrapped = font.listFormattedStringToWidth(text, lineWrapWidth).size();
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
      this.addButton(new Button(this.width / 2 - 100, 196, 200, 20, I18n.format("gui.done"), (p_214161_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
      }));
   }

    protected void addChangePageButtons() {
        int x1 = (this.width - bookWidth) / 2;
        int x2 = (this.width + bookWidth) / 2;
        int j = 2;
        int y = 159;
        this.buttonNextPage = this.addButton(new ChangePageButton(x2 - 43 - 24, y, true, (p_214159_1_) -> {
            this.nextPage();
        }, this.pageTurnSounds));
        this.buttonPreviousPage = this.addButton(new ChangePageButton(x1 + 43, y, false, (p_214158_1_) -> {
            this.previousPage();
        }, this.pageTurnSounds));
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
        return this.font.getStringWidth(this.font.getBidiFlag() ? this.font.bidiReorder(text) : text);
    }
}
