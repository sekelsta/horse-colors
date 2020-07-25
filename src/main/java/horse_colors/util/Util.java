package sekelsta.horse_colors.util;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.util.text.TextComponentTranslation;

import sekelsta.horse_colors.HorseColors;

public class Util {
    public static boolean horseCanMate(AbstractHorse horse) {
        // This is the same as calling other.canMate() but doesn't require
        // reflection
        return !horse.isBeingRidden() && !horse.isRiding() && horse.isTame() && !horse.isChild() && horse.getHealth() >= horse.getMaxHealth() && horse.isInLove();
    }

    public static String translate(String in) {
        return new TextComponentTranslation(HorseColors.MODID + "." + in).getFormattedText();
    }
}
