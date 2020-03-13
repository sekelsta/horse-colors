package sekelsta.horse_colors;


import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class HorseGeneticModel<T extends AbstractHorseEntity> extends AgeableModel<T>
{
    private final ModelRenderer head;
    private final ModelRenderer horn;
    private final ModelRenderer babyHorn;
    private final ModelRenderer upperMouth;
    private final ModelRenderer lowerMouth;
    private final ModelRenderer horseLeftEar;
    private final ModelRenderer horseRightEar;
    /** The left ear box for the mule model. */
    private final ModelRenderer muleLeftEar;
    /** The right ear box for the mule model. */
    private final ModelRenderer muleRightEar;
    private final ModelRenderer neck;
    /** The box for the horse's ropes on its face. */
    private final ModelRenderer horseFaceRopes;
    private final ModelRenderer mane;
    private final ModelRenderer body;
    private final ModelRenderer tailBase;
    private final ModelRenderer tailMiddle;
    private final ModelRenderer tailTip;
    private final ModelRenderer tailThin; // For donkeys
    private final ModelRenderer tailTuft; // For donkeys
    private final ModelRenderer backLeftLeg;
    private final ModelRenderer backLeftShin;
    private final ModelRenderer backLeftHoof;
    private final ModelRenderer backRightLeg;
    private final ModelRenderer backRightShin;
    private final ModelRenderer backRightHoof;
    private final ModelRenderer frontLeftLeg;
    private final ModelRenderer frontLeftShin;
    private final ModelRenderer frontLeftHoof;
    private final ModelRenderer frontRightLeg;
    private final ModelRenderer frontRightShin;
    private final ModelRenderer frontRightHoof;
    /** The left chest box on the mule model. */
    private final ModelRenderer muleLeftChest;
    /** The right chest box on the mule model. */
    private final ModelRenderer muleRightChest;
    // The stirrups
    private final ModelRenderer horseLeftSaddleRope;
    private final ModelRenderer horseRightSaddleRope;

   private final ModelRenderer[] tackArray;
   private final ModelRenderer[] extraTackArray;


    // 1.14's HorseModel takes a scale factor as constructor argument
    public HorseGeneticModel(float scaleFactor)
    {
        // Initialize AgeableModel
        super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.body = new ModelRenderer(this, 0, 34);
        this.body.func_228301_a_(-5.0F, -8.0F, -19.0F, 10.0F, 10.0F, 24.0F, scaleFactor);
        this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
        this.tailBase = new ModelRenderer(this, 44, 0);
        this.tailBase.func_228301_a_(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, scaleFactor);
        this.tailBase.setRotationPoint(0.0F, 3.0F, 14.0F);
        this.tailBase.rotateAngleX = -1.134464F;
        this.tailMiddle = new ModelRenderer(this, 38, 7);
        this.tailMiddle.func_228301_a_(-1.5F, -2.0F, 3.0F, 3.0F, 4.0F, 7.0F, scaleFactor);
        this.tailBase.addChild(tailMiddle);
        this.tailTip = new ModelRenderer(this, 24, 3);
        this.tailTip.func_228301_a_(-1.5F, -4.5F, 9.0F, 3.0F, 4.0F, 7.0F, scaleFactor);
        this.tailTip.rotateAngleX = -1.3962634F - -1.134464F;
        this.tailMiddle.addChild(tailTip);

        this.tailThin = new ModelRenderer(this, 37, 19);
        this.tailThin.func_228301_a_(-1.5F, -1.0F, 1.0F, 1.0F, 1.0F, 8.0F, scaleFactor);
        this.tailThin.setRotationPoint(1.0F, 3.0F, 13.0F);
        this.tailThin.rotateAngleX = -1.134464F; // This doesn't seem to matter at all
        this.tailTuft = new ModelRenderer(this, 49, 42);
        this.tailTuft.func_228301_a_(-2.0F, -1.5F, 1.0F, 2, 2, 7.0F, scaleFactor);
        this.tailTuft.setRotationPoint(0.0F, 0.0F, 8.0F);
        this.tailThin.addChild(tailTuft);

        // When making something a child that wasn't before, subtract the
        // parent's rotationPoint
        this.backLeftLeg = new ModelRenderer(this, 78, 29);
        this.backLeftLeg.func_228301_a_(-2.5F, -2.0F, -2.5F, 4, 9, 5.0F, scaleFactor);
        this.backLeftLeg.setRotationPoint(4.0F, 9.0F, 11.0F);
        this.backLeftShin = new ModelRenderer(this, 78, 43);
        this.backLeftShin.func_228301_a_(-2.0F, 0.0F, -1.5F, 3, 5, 3.0F, scaleFactor);
        this.backLeftShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.backLeftLeg.addChild(backLeftShin);
        this.backLeftHoof = new ModelRenderer(this, 78, 51);
        this.backLeftHoof.func_228301_a_(-2.5F, 5.1F, -2.0F, 4, 3, 4.0F, scaleFactor);
        this.backLeftShin.addChild(this.backLeftHoof);

        this.backRightLeg = new ModelRenderer(this, 96, 29);
        this.backRightLeg.func_228301_a_(-1.5F, -2.0F, -2.5F, 4, 9, 5.0F, scaleFactor);
        this.backRightLeg.setRotationPoint(-4.0F, 9.0F, 11.0F);
        this.backRightShin = new ModelRenderer(this, 96, 43);
        this.backRightShin.func_228301_a_(-1.0F, 0.0F, -1.5F, 3, 5, 3.0F, scaleFactor);
        this.backRightShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.backRightLeg.addChild(this.backRightShin);
        this.backRightHoof = new ModelRenderer(this, 96, 51);
        this.backRightHoof.func_228301_a_(-1.5F, 5.1F, -2.0F, 4, 3, 4.0F, scaleFactor);
        this.backRightShin.addChild(this.backRightHoof);

        this.frontLeftLeg = new ModelRenderer(this, 44, 29);
        this.frontLeftLeg.func_228301_a_(-1.9F, -1.0F, -2.1F, 3, 8, 4.0F, scaleFactor);
        this.frontLeftLeg.setRotationPoint(4.0F, 9.0F, -8.0F);
        this.frontLeftShin = new ModelRenderer(this, 44, 41);
        this.frontLeftShin.func_228301_a_(-1.9F, 0.0F, -1.6F, 3, 5, 3.0F, scaleFactor);
        this.frontLeftShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.frontLeftLeg.addChild(this.frontLeftShin);
        this.frontLeftHoof = new ModelRenderer(this, 44, 51);
        this.frontLeftHoof.func_228301_a_(-2.4F, 5.1F, -2.1F, 4, 3, 4.0F, scaleFactor);
        this.frontLeftShin.addChild(this.frontLeftHoof);

        this.frontRightLeg = new ModelRenderer(this, 60, 29);
        this.frontRightLeg.func_228301_a_(-1.1F, -1.0F, -2.1F, 3, 8, 4.0F, scaleFactor);
        this.frontRightLeg.setRotationPoint(-4.0F, 9.0F, -8.0F);
        this.frontRightShin = new ModelRenderer(this, 60, 41);
        this.frontRightShin.func_228301_a_(-1.1F, 0.0F, -1.6F, 3, 5, 3.0F, scaleFactor);
        this.frontRightShin.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.frontRightLeg.addChild(this.frontRightShin);
        this.frontRightHoof = new ModelRenderer(this, 60, 51);
        this.frontRightHoof.func_228301_a_(-1.6F, 5.1F, -2.1F, 4, 3, 4.0F, scaleFactor);
        this.frontRightShin.addChild(this.frontRightHoof);

        this.head = new ModelRenderer(this, 0, 0);
        this.head.func_228301_a_(-2.5F, -10.0F, -1.5F, 5, 5, 7.0F, scaleFactor);
        this.upperMouth = new ModelRenderer(this, 24, 18);
        this.upperMouth.func_228301_a_(-2.0F, -10.0F, -7.0F, 4, 3, 6.0F, scaleFactor);
        this.lowerMouth = new ModelRenderer(this, 24, 27);
        this.lowerMouth.func_228301_a_(-2.0F, -7.0F, -6.5F, 4, 2, 5.0F, scaleFactor);
        this.head.addChild(this.upperMouth);
        this.head.addChild(this.lowerMouth);
        this.horseLeftEar = new ModelRenderer(this, 0, 0);
        this.horseLeftEar.func_228301_a_(0.45F, -12.0F, 4.0F, 2, 3, 1.0F, scaleFactor);
        this.horseRightEar = new ModelRenderer(this, 0, 0);
        this.horseRightEar.func_228301_a_(-2.45F, -12.0F, 4.0F, 2, 3, 1.0F, scaleFactor);
        this.muleLeftEar = new ModelRenderer(this, 0, 12);
        this.muleLeftEar.func_228301_a_(-2.0F, -16.0F, 4.0F, 2, 7, 1.0F, scaleFactor);
        this.muleLeftEar.rotateAngleZ = 0.2617994F;
        this.muleRightEar = new ModelRenderer(this, 0, 12);
        this.muleRightEar.func_228301_a_(0.0F, -16.0F, 4.0F, 2, 7, 1.0F, scaleFactor);
        this.muleRightEar.rotateAngleZ = -0.2617994F;
        this.head.addChild(horseLeftEar);
        this.head.addChild(horseRightEar);
        this.head.addChild(muleLeftEar);
        this.head.addChild(muleRightEar);

        this.neck = new ModelRenderer(this, 0, 12);
        this.neck.func_228301_a_(-2.05F, -9.8F, -2.0F, 4, 14, 8.0F, scaleFactor);
        this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.neck.rotateAngleX = 0.5235988F;
        this.neck.addChild(this.head);


        this.muleLeftChest = new ModelRenderer(this, 0, 34);
        this.muleLeftChest.func_228301_a_(-3.0F, 0.0F, 0.0F, 8, 8, 3.0F, scaleFactor);
        this.muleLeftChest.setRotationPoint(-7.5F, 3.0F, 10.0F);
        this.muleLeftChest.rotateAngleY = ((float)Math.PI / 2F);
        this.muleRightChest = new ModelRenderer(this, 0, 47);
        this.muleRightChest.func_228301_a_(-3.0F, 0.0F, 0.0F, 8, 8, 3.0F, scaleFactor);
        this.muleRightChest.setRotationPoint(4.5F, 3.0F, 10.0F);
        this.muleRightChest.rotateAngleY = ((float)Math.PI / 2F);
        ModelRenderer horseSaddleBottom = new ModelRenderer(this, 80, 0);
        horseSaddleBottom.func_228301_a_(-5.0F, 0.0F, -3.0F, 10, 1, 8.0F, scaleFactor);
        horseSaddleBottom.setRotationPoint(0.0F, -9.0F, -7.0F);
        body.addChild(horseSaddleBottom);

        ModelRenderer horseSaddleFront = new ModelRenderer(this, 106, 9);
        horseSaddleFront.func_228301_a_(-1.5F, -1.0F, -3.0F, 3, 1, 2.0F, scaleFactor);
        horseSaddleBottom.addChild(horseSaddleFront);

        ModelRenderer horseSaddleBack = new ModelRenderer(this, 80, 9);
        horseSaddleBack.func_228301_a_(-4.0F, -1.0F, 3.0F, 8, 1, 2.0F, scaleFactor);
        horseSaddleBottom.addChild(horseSaddleBack);

        this.horseLeftSaddleRope = new ModelRenderer(this, 70, 0);
        this.horseLeftSaddleRope.func_228301_a_(-0.5F, 0.0F, -0.5F, 1, 6, 1.0F, scaleFactor);
        this.horseLeftSaddleRope.setRotationPoint(5.0F, 1.0F, 0.0F);
        horseSaddleBottom.addChild(this.horseLeftSaddleRope);

        ModelRenderer horseLeftSaddleMetal = new ModelRenderer(this, 74, 0);
        horseLeftSaddleMetal.func_228301_a_(-0.5F, 6.0F, -1.0F, 1, 2, 2.0F, scaleFactor);
        horseLeftSaddleRope.addChild(horseLeftSaddleMetal);

        this.horseRightSaddleRope = new ModelRenderer(this, 80, 0);
        this.horseRightSaddleRope.func_228301_a_(-0.5F, 0.0F, -0.5F, 1, 6, 1.0F, scaleFactor);
        this.horseRightSaddleRope.setRotationPoint(-5.0F, 1.0F, 0.0F);
        horseSaddleBottom.addChild(this.horseRightSaddleRope);

        ModelRenderer horseRightSaddleMetal = new ModelRenderer(this, 74, 4);
        horseRightSaddleMetal.func_228301_a_(-0.5F, 6.0F, -1.0F, 1, 2, 2.0F, scaleFactor);
        horseRightSaddleRope.addChild(horseRightSaddleMetal);

        ModelRenderer horseLeftFaceMetal = new ModelRenderer(this, 74, 13);
        horseLeftFaceMetal.func_228301_a_(1.5F, -8.0F, -4.0F, 1, 2, 2.0F, scaleFactor);
        this.head.addChild(horseLeftFaceMetal);
        ModelRenderer horseRightFaceMetal = new ModelRenderer(this, 74, 13);
        horseRightFaceMetal.func_228301_a_(-2.5F, -8.0F, -4.0F, 1, 2, 2.0F, scaleFactor);
        this.head.addChild(horseRightFaceMetal);
        this.horseFaceRopes = new ModelRenderer(this, 80, 12);
        this.horseFaceRopes.func_228301_a_(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
        this.head.addChild(horseFaceRopes);

        this.tackArray = new ModelRenderer[]{horseSaddleBottom, horseSaddleFront, horseSaddleBack, horseLeftSaddleMetal, horseLeftSaddleRope, horseRightSaddleMetal, horseRightSaddleRope, horseLeftFaceMetal, horseRightFaceMetal, horseFaceRopes};

        ModelRenderer horseLeftRein = new ModelRenderer(this, 44, 10);
        horseLeftRein.func_228301_a_(2.6F, -6.0F, -6.0F, 0, 3, 16.0F, scaleFactor);
        ModelRenderer horseRightRein = new ModelRenderer(this, 44, 5);
        horseRightRein.func_228301_a_(-2.6F, -6.0F, -6.0F, 0, 3, 16.0F, scaleFactor);
        this.neck.addChild(horseLeftRein);
        this.neck.addChild(horseRightRein);
        this.extraTackArray = new ModelRenderer[]{horseLeftRein, horseRightRein};

        this.mane = new ModelRenderer(this, 58, 0);
        this.mane.func_228301_a_(-1.0F, -11.5F, 5.0F, 2, 16, 4.0F, scaleFactor);
        this.neck.addChild(mane);

        int hornLength = 7;
        this.horn = new ModelRenderer(this, 84, 0);
        this.horn.func_228301_a_(-0.5F, -10.0F - hornLength, 2.0F, 1, hornLength, 1, scaleFactor);
        this.head.addChild(horn);
        int babyHornLength = 3;
        this.babyHorn = new ModelRenderer(this, 84, 0);
        this.babyHorn.func_228301_a_(-0.5F, -10.0F - babyHornLength, 2.0F, 1, babyHornLength, 1, scaleFactor);
        this.head.addChild(babyHorn);
    }

    @Override
    public void func_225597_a_(T entityIn, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        // Disable things that are not for horses
        this.muleLeftChest.showModel = false;
        this.muleRightChest.showModel = false;
        this.muleLeftEar.showModel = false;
        this.muleRightEar.showModel = false;
        this.tailThin.showModel = false;
        this.horn.showModel = false;
        this.babyHorn.showModel = false;

        boolean isSaddled = entityIn.isHorseSaddled();
        boolean isRidden = entityIn.isBeingRidden();

        for(ModelRenderer tack_piece : this.tackArray) {
            tack_piece.showModel = isSaddled;
        }

        for(ModelRenderer extra_tack : this.extraTackArray) {
            extra_tack.showModel = isRidden && isSaddled;
        }

        // Probably because the body only rotates for rearing
        this.body.rotationPointY = 11.0F;
     }

    /**
     * Fixes and offsets a rotation in the ModelHorse class.
     *//* Replaced by MathHelper.func_226167_j_() */
    private float updateHorseRotation(float p_110683_1_, float p_110683_2_, float p_110683_3_)
    {
        float bodyRotation;

        for (bodyRotation = p_110683_2_ - p_110683_1_; bodyRotation < -180.0F; bodyRotation += 360.0F)
        {
            ;
        }

        while (bodyRotation >= 180.0F)
        {
            bodyRotation -= 360.0F;
        }

        return p_110683_1_ + p_110683_3_ * bodyRotation;
    }

    // One or both of these might be what to render that isn't a child of something else
    public Iterable<ModelRenderer> func_225602_a_() {
        return ImmutableList.of(this.neck);
    }

    protected Iterable<ModelRenderer> func_225600_b_() {
        return ImmutableList.of(this.body, this.tailBase, this.tailThin, this.backLeftLeg, this.backRightLeg, this.frontLeftLeg, this.frontRightLeg, muleLeftChest, muleRightChest);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTickTime);
        float bodyRotation = this.updateHorseRotation(entityIn.prevRenderYawOffset, entityIn.renderYawOffset, partialTickTime);
        float headRotation = this.updateHorseRotation(entityIn.prevRotationYawHead, entityIn.rotationYawHead, partialTickTime);
        float f2 = entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTickTime;
        float f3 = headRotation - bodyRotation;
        float f4 = f2 * 0.017453292F;

        if (f3 > 20.0F)
        {
            f3 = 20.0F;
        }

        if (f3 < -20.0F)
        {
            f3 = -20.0F;
        }

        if (limbSwingAmount > 0.2F)
        {
            f4 += MathHelper.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount;
        }

        AbstractHorseEntity abstracthorse = (AbstractHorseEntity)entityIn;
        float grassEatingAmount = abstracthorse.getGrassEatingAmount(partialTickTime);
        float rearingAmount = abstracthorse.getRearingAmount(partialTickTime);
        float f7 = 1.0F - rearingAmount;
        float mouthOpenAmount = abstracthorse.getMouthOpennessAngle(partialTickTime);
        boolean flag = abstracthorse.tailCounter != 0;
        boolean isSaddled = abstracthorse.isHorseSaddled();
        boolean isBeingRidden = abstracthorse.isBeingRidden();
        float f9 = (float)entityIn.ticksExisted + partialTickTime;
        float headRotation0 = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI);
        float headRotation1 = headRotation0 * 0.8F * limbSwingAmount;

        this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
        this.tailBase.rotationPointY = 3.0F;
        this.tailThin.rotationPointY = 3.0F;
        this.muleRightChest.rotationPointY = 3.0F;
        this.muleRightChest.rotationPointZ = 10.0F;
        this.body.rotateAngleX = 0.0F;
        this.neck.rotateAngleX = 0.5235988F + f4;
        this.neck.rotateAngleY = f3 * 0.017453292F;
        this.neck.rotateAngleX = rearingAmount * (0.2617994F + f4) + grassEatingAmount * 2.1816616F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.rotateAngleX;
        this.neck.rotateAngleY = rearingAmount * f3 * 0.017453292F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.rotateAngleY;
        this.neck.rotationPointY = rearingAmount * -6.0F + grassEatingAmount * 11.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.rotationPointY;
        this.neck.rotationPointZ = rearingAmount * -1.0F + grassEatingAmount * -10.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.rotationPointZ;
        this.tailBase.rotationPointY = rearingAmount * 9.0F + f7 * this.tailBase.rotationPointY;
        this.tailThin.rotationPointY = rearingAmount * 9.0F + f7 * this.tailThin.rotationPointY;
        this.muleRightChest.rotationPointY = rearingAmount * 5.5F + f7 * this.muleRightChest.rotationPointY;
        this.muleRightChest.rotationPointZ = rearingAmount * 15.0F + f7 * this.muleRightChest.rotationPointZ;
        this.body.rotateAngleX = rearingAmount * -((float)Math.PI / 4F) + f7 * this.body.rotateAngleX;
        this.upperMouth.rotationPointY = 0.02F;
        this.lowerMouth.rotationPointY = 0.0F;
        this.upperMouth.rotationPointZ = 0.02F - mouthOpenAmount;
        this.lowerMouth.rotationPointZ = mouthOpenAmount;
        this.head.rotateAngleX = 0.0F;
        this.upperMouth.rotateAngleX = -0.09424778F * mouthOpenAmount;
        this.lowerMouth.rotateAngleX = 0.15707964F * mouthOpenAmount;
        this.head.rotateAngleY = 0.0F;
        this.upperMouth.rotateAngleY = 0.0F;
        this.lowerMouth.rotateAngleY = 0.0F;
        this.muleLeftChest.rotateAngleX = headRotation1 / 5.0F;
        this.muleRightChest.rotateAngleX = -headRotation1 / 5.0F;
        float headRotation2 = 0.2617994F * rearingAmount;
        float headRotation3 = MathHelper.cos(f9 * 0.6F + (float)Math.PI);
        this.frontLeftLeg.rotationPointY = -2.0F * rearingAmount + 9.0F * f7;
        this.frontLeftLeg.rotationPointZ = -2.0F * rearingAmount + -8.0F * f7;
        this.frontRightLeg.rotationPointY = this.frontLeftLeg.rotationPointY;
        this.frontRightLeg.rotationPointZ = this.frontLeftLeg.rotationPointZ;
        float headRotation4 = (-1.0471976F + headRotation3) * rearingAmount + headRotation1 * f7;
        float headRotation5 = (-1.0471976F - headRotation3) * rearingAmount + -headRotation1 * f7;
        this.backLeftLeg.rotateAngleX = headRotation2 + -headRotation0 * 0.5F * limbSwingAmount * f7;
        this.backRightLeg.rotateAngleX = headRotation2 + headRotation0 * 0.5F * limbSwingAmount * f7;
        this.frontLeftLeg.rotateAngleX = headRotation4;
        this.frontLeftShin.rotateAngleX = (this.frontLeftLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F + headRotation3 * 0.2F)) * rearingAmount + (headRotation1 + Math.max(0.0F, headRotation0 * 0.5F * limbSwingAmount)) * f7 - this.frontLeftLeg.rotateAngleX;
        // This might do the same thing
        //this.frontLeftShin.rotateAngleX = ((float)Math.PI * Math.max(0.0F, 0.2F + headRotation3 * 0.2F)) * rearingAmount;
        this.frontRightLeg.rotateAngleX = headRotation5;
        this.frontRightShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + (float)Math.PI * Math.max(0.0F, 0.2F - headRotation3 * 0.2F)) * rearingAmount + (-headRotation1 + Math.max(0.0F, -headRotation0 * 0.5F * limbSwingAmount)) * f7 - this.frontRightLeg.rotateAngleX;
        //this.frontRightShin.rotateAngleX = ((float)Math.PI * Math.max(0.0F, 0.2F - headRotation3 * 0.2F)) * rearingAmount;

        if (isSaddled)
        {
            this.muleLeftChest.rotationPointY = this.muleRightChest.rotationPointY;
            this.muleLeftChest.rotationPointZ = this.muleRightChest.rotationPointZ;

            if (isBeingRidden)
            {
                this.horseLeftSaddleRope.rotateAngleX = -1.0471976F;
                this.horseRightSaddleRope.rotateAngleX = -1.0471976F;
                this.horseLeftSaddleRope.rotateAngleZ = 0.0F;
                this.horseRightSaddleRope.rotateAngleZ = 0.0F;
            }
            else
            {
                this.horseLeftSaddleRope.rotateAngleX = headRotation1 / 3.0F;
                this.horseRightSaddleRope.rotateAngleX = headRotation1 / 3.0F;
                this.horseLeftSaddleRope.rotateAngleZ = headRotation1 / 5.0F;
                this.horseRightSaddleRope.rotateAngleZ = -headRotation1 / 5.0F;
            }
        }

        float tailRotation = -1.3089969F + limbSwingAmount * 1.5F;
        float donkeyTailRotate = -1.4F + limbSwingAmount;

        if (tailRotation > 0.0F)
        {
            tailRotation = 0.0F;
        }

        if (flag)
        {
            this.tailBase.rotateAngleY = MathHelper.cos(f9 * 0.7F);
            this.tailThin.rotateAngleY = MathHelper.cos(f9 * 0.7F);
            tailRotation = 0.0F;
            donkeyTailRotate = 0.0F;
        }
        else
        {
            this.tailBase.rotateAngleY = 0.0F;
            this.tailThin.rotateAngleY = 0.0F;
        }

        this.tailBase.rotateAngleX = tailRotation;
        this.tailThin.rotateAngleX = donkeyTailRotate;
    }
}
