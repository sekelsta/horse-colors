package sekelsta.horse_colors.entity;
import net.minecraft.entity.passive.horse.*;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class MuleGeneticEntity extends AbstractChestHorseGenetic {
   public MuleGeneticEntity(EntityType<? extends MuleGeneticEntity> p_i50236_1_, World p_i50236_2_) {
      super(p_i50236_1_, p_i50236_2_);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ENTITY_MULE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_MULE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      super.getHurtSound(damageSourceIn);
      return SoundEvents.ENTITY_MULE_HURT;
   }

    public boolean fluffyTail() {
        return true;
    }

    public boolean longEars() {
        return true;
    }

    public boolean thinMane() {
        return false;
    }

   protected void playChestEquipSound() {
      this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
   }

   public AgeableEntity createChild(AgeableEntity ageable) {
      return EntityType.MULE.create(this.world);
   }
}