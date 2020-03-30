package sekelsta.horse_colors.entity;
import net.minecraft.entity.passive.horse.*;

import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ai.RandomWalkGroundTie;
import sekelsta.horse_colors.genetics.*;
import sekelsta.horse_colors.init.ModEntities;

public class MuleGeneticEntity extends MuleEntity implements IHorseShape, IGeneticEntity {
    private HorseGenome genes;
    protected static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT2 = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT3 = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_SPEED = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_JUMP = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_HEALTH = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_RANDOM = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);

    public MuleGeneticEntity(EntityType<? extends MuleGeneticEntity> p_i50239_1_, World p_i50239_2_) {
        super(p_i50239_1_, p_i50239_2_);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomWalkGroundTie(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.initExtraAI();
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

    public void useGeneticAttributes()
    {
        if (HorseConfig.COMMON.useGeneticStats.get())
        {
            // Default horse health ranges from 15 to 30, but ours goes from
            // 15 to 31
            float maxHealth = 15.0F + this.getGenes().getStat("health") * 0.5F;
            maxHealth += this.getGenes().getBaseHealth();
            // Vanilla horse speed ranges from 0.1125 to 0.3375
            // Vanilla mules have 0.175 speed
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
        return true;
    }

    public boolean longEars() {
        return true;
    }

    public boolean thinMane() {
        return false;
    }

    public HorseGenome getGenes() {
        return genes;
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

    public java.util.Random getRand() {
        return this.rand;
    }

    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        MuleGeneticEntity child = ModEntities.MULE_GENETIC.create(this.world);
        MuleGeneticEntity entityHorse = (MuleGeneticEntity)ageable;
        this.getGenes().setChildGenes(entityHorse.getGenes(), child);
        int i =  this.rand.nextInt();
        child.setChromosome("random", i);
        // Dominant white is homozygous lethal early in pregnancy. No child
        // is born.
        if (child.getGenes().isEmbryonicLethal())
        {
            return null;
        }
        this.setOffspringAttributes(ageable, child);
        child.useGeneticAttributes();

        return child;
    }
}
