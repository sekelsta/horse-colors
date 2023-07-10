package sekelsta.horse_colors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.genetics.Genome;

@OnlyIn(Dist.CLIENT)
public class GeneBookScreen extends Screen {
    private static final int linesPerPage = 14;
    private static final int lineWrapWidth = 124;
    private static final int bookWidth = 360;
    private static final int bookHeight = 192;
    private static final int pageWidth = 130;
    private static final int pageCrease = 10;
    private static final ResourceLocation BACKGROUND_TEXTURE_LOCATION = new ResourceLocation(HorseColors.MODID + ":textures/gui/book.png");
    int currPage = 0;
    private Genome genome;

    private List<List<String>> contents;
    private List<String> pages;
   /** Holds a copy of the page text, split into page width lines */
    private List<FormattedCharSequence> cachedPageLinesLeft = Collections.emptyList();
    private List<FormattedCharSequence> cachedPageLinesRight = Collections.emptyList();
    private int cachedPage = -1;

    private PageButton buttonNextPage;
    private PageButton buttonPreviousPage;
    /** Determines if a sound is played when the page is turned */
    private final boolean pageTurnSounds = true;

    public GeneBookScreen(Genome genomeIn) {
        super(GameNarrator.NO_TITLE);
        this.genome = genomeIn;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Render the book picture in the back
        this.renderBackground(guiGraphics);

        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE_LOCATION);

        int x = (this.width - bookWidth) / 2;
        int y = 2;
        //x = 0;
        guiGraphics.blit(BACKGROUND_TEXTURE_LOCATION, x, y, 0, 0, bookWidth, bookHeight, 512, 256);

        if (this.cachedPage != this.currPage) {
            this.cachedPageLinesLeft = cachePageLines(this.currPage);
            this.cachedPageLinesRight = cachePageLines(this.currPage + 1);
        }
        this.cachedPage = this.currPage;

        renderPage(guiGraphics, currPage, cachedPageLinesLeft, this.width / 2 - pageWidth - pageCrease);
        if (this.getPageCount() > currPage + 1) {
            renderPage(guiGraphics, currPage + 1, cachedPageLinesRight, this.width / 2 + pageCrease);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private List<FormattedCharSequence> cachePageLines(int page) {
        FormattedText itextproperties;
        if (page < 0 || page >= this.getPageCount()) {
            itextproperties = FormattedText.EMPTY;
        }
        else {
            String pagetext = this.getPageText(page);
            itextproperties = FormattedText.of(pagetext);
        }
        return this.font.split(itextproperties, lineWrapWidth);
    }

    private void renderPage(GuiGraphics guiGraphics, int pagenum, List<FormattedCharSequence> cachedPageLines, int x) {
        String pageindicator = I18n.get("book.pageIndicator", pagenum + 1, this.getPageCount());

        String pagetext = this.getPageText(pagenum);
        int j1 = this.getTextWidth(pageindicator);
        guiGraphics.drawString(this.font, pageindicator, (float)(x - j1 + pageWidth), 18.0F, 0, false);

        int lines = Math.min(linesPerPage, cachedPageLines.size());
        for(int i = 0; i < lines; ++i) {
            FormattedCharSequence text = cachedPageLines.get(i);
            guiGraphics.drawString(this.font, text, (float)x, (float)(32 + i * 9), 0, false);
        }
    }

    @Override
    protected void init() {
        this.contents = genome.getBookContents();
        this.pages = new ArrayList<String>();
        for (int ch = 0; ch < contents.size(); ++ch) {
            String s = "";
            int lines = 0;
            for (int ln = 0; ln < contents.get(ch).size(); ++ln) {
                String text = contents.get(ch).get(ln);
                FormattedText itextproperties = FormattedText.of(text);
                // Get the number of lines for this block of text
                int wrapped = this.font.getSplitter().splitLines(itextproperties, lineWrapWidth, Style.EMPTY).size();
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
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_98299_) -> {
            this.minecraft.setScreen((Screen)null);
        }).bounds(this.width / 2 - 100, 196, 200, 20).build());
    }

    protected void addChangePageButtons() {
        int x1 = (this.width - bookWidth) / 2;
        int x2 = (this.width + bookWidth) / 2;
        int j = 2;
        int y = 159;
        this.buttonNextPage = this.addRenderableWidget(new PageButton(x2 - 43 - 24, y, true, (p_214159_1_) -> {
            this.nextPage();
        }, this.pageTurnSounds));
        this.buttonPreviousPage = this.addRenderableWidget(new PageButton(x1 + 43, y, false, (p_214158_1_) -> {
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
        return this.font.width(this.font.isBidirectional() ? this.font.bidirectionalShaping(text) : text);
    }
}
