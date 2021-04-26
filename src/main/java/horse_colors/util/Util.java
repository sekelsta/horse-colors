package sekelsta.horse_colors.util;

import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.text.TranslationTextComponent;

import sekelsta.horse_colors.HorseColors;

public class Util {
    public static boolean horseCanMate(AbstractHorseEntity horse) {
        // This is the same as calling other.canMate() but doesn't require
        // reflection
        return !horse.isVehicle() && !horse.isPassenger() && horse.isTamed() && !horse.isBaby() && horse.getHealth() >= horse.getMaxHealth() && horse.isInLove();
    }

    public static String translate(String in) {
        return new TranslationTextComponent(HorseColors.MODID + "." + in).getString(10000);
    }
}
