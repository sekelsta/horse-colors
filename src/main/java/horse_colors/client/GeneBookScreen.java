package sekelsta.horse_colors.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.ReadBookScreen;
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
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.genetics.Genome;

@OnlyIn(Dist.CLIENT)
public class GeneBookScreen extends Screen {
    public GeneBookScreen(Genome genome) {
        super(NarratorChatListener.EMPTY);
    }


    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        // Render the book picture in the back
        this.renderBackground();
        this.setFocused((IGuiEventListener)null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(ReadBookScreen.BOOK_TEXTURES);
        int i = (this.width - 192) / 2;
        int j = 2;
        this.blit(i, 2, 0, 0, 192, 192);/*
      if (this.bookGettingSigned) {
         String s = this.bookTitle;
         if (this.updateCount / 6 % 2 == 0) {
            s = s + "" + TextFormatting.BLACK + "_";
         } else {
            s = s + "" + TextFormatting.GRAY + "_";
         }

         String s1 = I18n.format("book.editTitle");
         int k = this.getTextWidth(s1);
         this.font.drawString(s1, (float)(i + 36 + (114 - k) / 2), 34.0F, 0);
         int l = this.getTextWidth(s);
         this.font.drawString(s, (float)(i + 36 + (114 - l) / 2), 50.0F, 0);
         String s2 = I18n.format("book.byAuthor", this.editingPlayer.getName().getString());
         int i1 = this.getTextWidth(s2);
         this.font.drawString(TextFormatting.DARK_GRAY + s2, (float)(i + 36 + (114 - i1) / 2), 60.0F, 0);
         String s3 = I18n.format("book.finalizeWarning");
         this.font.drawSplitString(s3, i + 36, 82, 114, 0);
      } else {
         String s4 = I18n.format("book.pageIndicator", this.currPage + 1, this.getPageCount());
         String s5 = this.getCurrPageText();
         int j1 = this.getTextWidth(s4);
         this.font.drawString(s4, (float)(i - j1 + 192 - 44), 18.0F, 0);
         this.font.drawSplitString(s5, i + 36, 32, 114, 0);
         this.highlightSelectedText(s5);
         if (this.updateCount / 6 % 2 == 0) {
            EditBookScreen.Point editbookscreen$point = this.func_214194_c(s5, this.selectionEnd);
            if (this.font.getBidiFlag()) {
               this.func_214227_a(editbookscreen$point);
               editbookscreen$point.x = editbookscreen$point.x - 4;
            }

            this.func_214224_c(editbookscreen$point);
            if (this.selectionEnd < s5.length()) {
               AbstractGui.fill(editbookscreen$point.x, editbookscreen$point.y - 1, editbookscreen$point.x + 1, editbookscreen$point.y + 9, -16777216);
            } else {
               this.font.drawString("_", (float)editbookscreen$point.x, (float)editbookscreen$point.y, 0);
            }
         }
      }*/

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
