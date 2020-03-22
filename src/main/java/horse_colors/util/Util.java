package sekelsta.horse_colors.util;

import net.minecraft.entity.passive.horse.AbstractHorseEntity;

public class Util {
    public static boolean horseCanMate(AbstractHorseEntity horse) {
        // This is the same as calling other.canMate() but doesn't require
        // reflection
        return !horse.isBeingRidden() && !horse.isPassenger() && horse.isTame() && !horse.isChild() && horse.getHealth() >= horse.getMaxHealth() && horse.isInLove();
    }
}
