package sekelsta.horse_colors.entity;

import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.genetics.*;
import sekelsta.horse_colors.init.ModEntities;
import sekelsta.horse_colors.util.Util;

public class DonkeyGeneticEntity extends DonkeyEntity implements IHorseShape, IGeneticEntity {
    private HorseGenome genes;
    protected static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(DonkeyGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT2 = EntityDataManager.<Integer>createKey(DonkeyGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT3 = EntityDataManager.<Integer>createKey(DonkeyGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_SPEED = EntityDataManager.<Integer>createKey(DonkeyGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_JUMP = EntityDataManager.<Integer>createKey(DonkeyGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_HEALTH = EntityDataManager.<Integer>createKey(DonkeyGeneticEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_RANDOM = EntityDataManager.<Integer>createKey(DonkeyGeneticEntity.class, DataSerializers.VARINT);

    public DonkeyGeneticEntity(EntityType<? extends DonkeyGeneticEntity> p_i50239_1_, World p_i50239_2_) {
        super(p_i50239_1_, p_i50239_2_);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(HORSE_VARIANT, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT2, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT3, Integer.valueOf(0));
        this.dataManager.register(HORSE_SPEED, Integer.valueOf(0));
        this.dataManager.register(HORSE_HEALTH, Integer.valueOf(0));
        this.dataManager.register(HORSE_JUMP, Integer.valueOf(0));
        this.dataManager.register(HORSE_RANDOM, Integer.valueOf(0));
    }
    /**
     * Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getChromosome("0"));
        compound.putInt("Variant2", this.getChromosome("1"));
        compound.putInt("Variant3", this.getChromosome("2"));
        compound.putInt("SpeedGenes", this.getChromosome("speed"));
        compound.putInt("JumpGenes", this.getChromosome("jump"));
        compound.putInt("HealthGenes", this.getChromosome("health"));
        compound.putInt("Random", this.getChromosome("random"));
    }

    /**
     *  Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setChromosome("0", compound.getInt("Variant"));
        this.setChromosome("1", compound.getInt("Variant2"));
        this.setChromosome("2", compound.getInt("Variant3"));
        this.setChromosome("speed", compound.getInt("SpeedGenes"));
        this.setChromosome("jump", compound.getInt("JumpGenes"));
        this.setChromosome("health", compound.getInt("HealthGenes"));
        this.setChromosome("random", compound.getInt("Random"));

        this.updateHorseSlots();
    }

    public HorseGenome getGenes() {
        return genes;
    }

    public java.util.Random getRand() {
        return this.rand;
    }

    private void useGeneticAttributes()
    {
        if (HorseConfig.COMMON.useGeneticStats.get())
        {
            // Default horse health ranges from 15 to 30, but ours goes from
            // 15 to 31
            float maxHealth = 15.0F + this.getGenes().getStat("health") * 0.5F;
            maxHealth += this.getGenes().getBaseHealth();
            // Vanilla horse speed ranges from 0.1125 to 0.3375
            // Vanilla donkeys have 0.175 speed
            double movementSpeed = 0.11D + this.getGenes().getStat("speed") * (0.2D / 32.0D);
            // Vanilla horse jump strength ranges from 0.4 to 1.0
            // Vanilla donkeys have 0.5 jump strength
            double jumpStrength = 0.4D + this.getGenes().getStat("jump") * (0.4D / 32.0D);

            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
            this.getAttribute(JUMP_STRENGTH).setBaseValue(jumpStrength);
        }
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.genes = new HorseGenome(this);
        float maxHealth = this.getModifiedMaxHealth() + this.getGenes().getBaseHealth();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
        this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
    }

    public boolean fluffyTail() {
        return false;
    }

    public boolean longEars() {
        return true;
    }

    public boolean thinMane() {
        return true;
    }

    public int getChromosome(String name)
    {
        switch(name) {
            case "0":
                return ((Integer)this.dataManager.get(HORSE_VARIANT)).intValue();
            case "1":
                return ((Integer)this.dataManager.get(HORSE_VARIANT2)).intValue();
            case "2":
                return ((Integer)this.dataManager.get(HORSE_VARIANT3)).intValue();
            case "speed":
                return ((Integer)this.dataManager.get(HORSE_SPEED)).intValue();
            case "jump":
                return ((Integer)this.dataManager.get(HORSE_JUMP)).intValue();
            case "health":
                return ((Integer)this.dataManager.get(HORSE_HEALTH)).intValue();
            case "random":
                return ((Integer)this.dataManager.get(HORSE_RANDOM)).intValue();
            default:
                System.out.print("Unrecognized horse data for getting: " 
                                + name + "\n");
                return 0;
        }
        
    }

    public void setChromosome(String name, int variant)
    {
        switch(name) {
            // Break for anything that affects color, return otherwise
            case "0":
                this.dataManager.set(HORSE_VARIANT, variant);
                break;
            case "1":
                this.dataManager.set(HORSE_VARIANT2, variant);
                break;
            case "2":
                this.dataManager.set(HORSE_VARIANT3, variant);
                break;
            case "speed":
                this.dataManager.set(HORSE_SPEED, variant);
                return;
            case "jump":
                this.dataManager.set(HORSE_JUMP, variant);
                return;
            case "health":
                this.dataManager.set(HORSE_HEALTH, variant);
                return;
            case "random":
                this.dataManager.set(HORSE_RANDOM, variant);
                return;
            default:
                System.out.print("Unrecognized horse data for setting: "
                                 + name + "\n");
        }
        this.getGenes().resetTexture();
    }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        if (otherAnimal == this)
        {
            return false;
        }
        else if (otherAnimal instanceof DonkeyGeneticEntity 
                || otherAnimal instanceof HorseGeneticEntity
                || otherAnimal instanceof DonkeyEntity 
                || otherAnimal instanceof HorseEntity)
        {
            return this.canMate() && Util.horseCanMate((AbstractHorseEntity)otherAnimal);
        }
        else
        {
            return false;
        }
    }

    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        AbstractHorseEntity abstracthorse;

        if (ageable instanceof HorseGeneticEntity)
        {
            abstracthorse = ModEntities.MULE_GENETIC.create(this.world);
            HorseGeneticEntity entityHorse = (HorseGeneticEntity)ageable;
            this.getGenes().setChildGenes(entityHorse.getGenes(), ((MuleGeneticEntity)abstracthorse));

            int i =  this.rand.nextInt();
            ((MuleGeneticEntity)abstracthorse).setChromosome("random", i);

            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (((MuleGeneticEntity)abstracthorse).getGenes().isEmbryonicLethal())
            {
                return null;
            }
            this.setOffspringAttributes(ageable, abstracthorse);
            ((MuleGeneticEntity)abstracthorse).useGeneticAttributes();
        }
        else if (ageable instanceof DonkeyGeneticEntity)
        {
            abstracthorse = ModEntities.DONKEY_GENETIC.create(this.world);
            DonkeyGeneticEntity entityHorse = (DonkeyGeneticEntity)ageable;
            this.getGenes().setChildGenes(entityHorse.getGenes(), ((DonkeyGeneticEntity)abstracthorse));

            int i =  this.rand.nextInt();
            ((DonkeyGeneticEntity)abstracthorse).setChromosome("random", i);

            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (((DonkeyGeneticEntity)abstracthorse).getGenes().isEmbryonicLethal())
            {
                return null;
            }
            this.setOffspringAttributes(ageable, abstracthorse);
            ((DonkeyGeneticEntity)abstracthorse).useGeneticAttributes();
        }
        else
        {
            return super.createChild(ageable);
        }

        return abstracthorse;
    }
}
