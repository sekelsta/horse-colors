package sekelsta.horse_colors.client.renderer;

import net.minecraft.entity.passive.horse.*;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;

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

    private float ageScale = 0.5f;

    public HorseGeneticModel() {
        this(0.0F);
    }
    // 1.14's HorseModel takes a scale factor as constructor argument
    public HorseGeneticModel(float scaleFactor)
    {
        // Initialize AgeableModel
        super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
        this.texWidth = 128;
        this.texHeight = 128;
        this.body = new ModelRenderer(this, 0, 34);
        this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24, scaleFactor);
        this.body.setPos(0.0F, 11.0F, 9.0F);
        this.tailBase = new ModelRenderer(this, 44, 0);
        this.tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3, scaleFactor);
        this.tailBase.setPos(0.0F, -8.0F, 5.0F);
        this.tailBase.xRot = -1.134464F;
        this.tailMiddle = new ModelRenderer(this, 38, 7);
        this.tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7, scaleFactor);
        this.tailBase.addChild(tailMiddle);
        this.tailTip = new ModelRenderer(this, 24, 3);
        this.tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7, scaleFactor);
        this.tailTip.xRot = -0.2618004F;
        this.tailMiddle.addChild(tailTip);
        this.body.addChild(tailBase);

        this.tailThin = new ModelRenderer(this, 116, 0);
        this.tailThin.addBox(-0.5F, 0.0F, 0.5F, 1, 5, 1, scaleFactor);
        this.tailThin.setPos(0.0F, -6F, 4.0F);
        this.tailThin.xRot = -1.134464F; // This doesn't seem to matter at all
        this.tailTuft = new ModelRenderer(this, 120, 0);
        this.tailTuft.addBox(-1.0F, 0F, 0.25F, 2, 6, 2, scaleFactor);
        this.tailTuft.setPos(0.0F, 5.0F, 0.0F);
        this.tailThin.addChild(tailTuft);
        this.body.addChild(tailThin);

        // When making something a child that wasn't before, subtract the
        // parent's rotationPoint
        this.backLeftLeg = new ModelRenderer(this, 78, 29);
        this.backLeftLeg.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5, scaleFactor);
        this.backLeftLeg.setPos(4.0F, 9.0F, 11.0F);
        this.backLeftShin = new ModelRenderer(this, 78, 43);
        this.backLeftShin.addBox(-2.0F, 0.0F, -1F, 3, 5, 3, scaleFactor);
        this.backLeftShin.setPos(0.0F, 7.0F, 0.0F);
        this.backLeftLeg.addChild(backLeftShin);
        this.backLeftHoof = new ModelRenderer(this, 78, 51);
        this.backLeftHoof.addBox(-2.5F, 5.0F, -1.5F, 4, 3, 4, scaleFactor);
        this.backLeftShin.addChild(this.backLeftHoof);

        this.backRightLeg = new ModelRenderer(this, 96, 29);
        this.backRightLeg.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5, scaleFactor);
        this.backRightLeg.setPos(-4.0F, 9.0F, 11.0F);
        this.backRightShin = new ModelRenderer(this, 96, 43);
        this.backRightShin.addBox(-1.0F, 0.0F, -1F, 3, 5, 3, scaleFactor);
        this.backRightShin.setPos(0.0F, 7.0F, 0.0F);
        this.backRightLeg.addChild(this.backRightShin);
        this.backRightHoof = new ModelRenderer(this, 96, 51);
        this.backRightHoof.addBox(-1.5F, 5.0F, -1.5F, 4, 3, 4, scaleFactor);
        this.backRightShin.addChild(this.backRightHoof);

        this.frontLeftLeg = new ModelRenderer(this, 44, 29);
        this.frontLeftLeg.addBox(-1.9F, -1.0F, -1.0F, 3, 8, 4, scaleFactor);
        this.frontLeftLeg.setPos(4.0F, 9.0F, -8.0F);
        this.frontLeftShin = new ModelRenderer(this, 44, 41);
        this.frontLeftShin.addBox(-1.9F, 0.0F, -0.5F, 3, 5, 3, scaleFactor);
        this.frontLeftShin.setPos(0.0F, 7.0F, 0.0F);
        this.frontLeftLeg.addChild(this.frontLeftShin);
        this.frontLeftHoof = new ModelRenderer(this, 44, 51);
        this.frontLeftHoof.addBox(-2.4F, 5.0F, -1.0F, 4, 3, 4, scaleFactor);
        this.frontLeftShin.addChild(this.frontLeftHoof);

        this.frontRightLeg = new ModelRenderer(this, 60, 29);
        this.frontRightLeg.addBox(-1.1F, -1.0F, -1.0F, 3, 8, 4, scaleFactor);
        this.frontRightLeg.setPos(-4.0F, 9.0F, -8.0F);
        this.frontRightShin = new ModelRenderer(this, 60, 41);
        this.frontRightShin.addBox(-1.1F, 0.0F, -0.5F, 3, 5, 3, scaleFactor);
        this.frontRightShin.setPos(0.0F, 7.0F, 0.0F);
        this.frontRightLeg.addChild(this.frontRightShin);
        this.frontRightHoof = new ModelRenderer(this, 60, 51);
        this.frontRightHoof.addBox(-1.6F, 5.0F, -1.0F, 4, 3, 4, scaleFactor);
        this.frontRightShin.addChild(this.frontRightHoof);

        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7, scaleFactor);
        this.head.setPos(0.0F, 4.0F, -10.0F);
        this.head.xRot = 0.5235988F;
        this.upperMouth = new ModelRenderer(this, 24, 18);
        this.upperMouth.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6, scaleFactor);
        this.lowerMouth = new ModelRenderer(this, 24, 27);
        this.lowerMouth.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5, scaleFactor);
        this.head.addChild(this.upperMouth);
        this.head.addChild(this.lowerMouth);
        this.horseLeftEar = new ModelRenderer(this, 0, 0);
        this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1, scaleFactor);
        this.horseRightEar = new ModelRenderer(this, 0, 0);
        this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1, scaleFactor);
        this.muleLeftEar = new ModelRenderer(this, 0, 12);
        this.muleLeftEar.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1, scaleFactor);
        this.muleLeftEar.zRot = 0.2617994F;
        this.muleRightEar = new ModelRenderer(this, 0, 12);
        this.muleRightEar.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1, scaleFactor);
        this.muleRightEar.zRot = -0.2617994F;
        this.head.addChild(horseLeftEar);
        this.head.addChild(horseRightEar);
        this.head.addChild(muleLeftEar);
        this.head.addChild(muleRightEar);

        this.neck = new ModelRenderer(this, 0, 12);
        this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8, scaleFactor);
        this.neck.setPos(0.0F, 4.0F, -10.0F);
        this.neck.xRot = 0.5235988F;
        //this.neck.addChild(this.head);


        this.muleLeftChest = new ModelRenderer(this, 0, 34);
        this.muleLeftChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, scaleFactor);
        this.muleLeftChest.setPos(-7.5F, 3.0F, 10.0F);
        this.muleLeftChest.yRot = ((float)Math.PI / 2F);
        this.muleRightChest = new ModelRenderer(this, 0, 47);
        this.muleRightChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, scaleFactor);
        this.muleRightChest.setPos(4.5F, 3.0F, 10.0F);
        this.muleRightChest.yRot = ((float)Math.PI / 2F);
        ModelRenderer horseSaddleBottom = new ModelRenderer(this, 80, 0);
        horseSaddleBottom.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8, scaleFactor);
        horseSaddleBottom.setPos(0.0F, -9.0F, -7.0F);
        body.addChild(horseSaddleBottom);

        ModelRenderer horseSaddleFront = new ModelRenderer(this, 106, 9);
        horseSaddleFront.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2, scaleFactor);
        horseSaddleBottom.addChild(horseSaddleFront);

        ModelRenderer horseSaddleBack = new ModelRenderer(this, 80, 9);
        horseSaddleBack.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2, scaleFactor);
        horseSaddleBottom.addChild(horseSaddleBack);

        this.horseLeftSaddleRope = new ModelRenderer(this, 70, 0);
        this.horseLeftSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, scaleFactor);
        this.horseLeftSaddleRope.setPos(5.0F, 1.0F, 0.0F);
        horseSaddleBottom.addChild(this.horseLeftSaddleRope);

        ModelRenderer horseLeftSaddleMetal = new ModelRenderer(this, 74, 0);
        horseLeftSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2, scaleFactor);
        horseLeftSaddleRope.addChild(horseLeftSaddleMetal);

        this.horseRightSaddleRope = new ModelRenderer(this, 80, 0);
        this.horseRightSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, scaleFactor);
        this.horseRightSaddleRope.setPos(-5.0F, 1.0F, 0.0F);
        horseSaddleBottom.addChild(this.horseRightSaddleRope);

        ModelRenderer horseRightSaddleMetal = new ModelRenderer(this, 74, 4);
        horseRightSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2, scaleFactor);
        horseRightSaddleRope.addChild(horseRightSaddleMetal);

        ModelRenderer horseLeftFaceMetal = new ModelRenderer(this, 74, 13);
        horseLeftFaceMetal.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2, scaleFactor);
        this.head.addChild(horseLeftFaceMetal);
        ModelRenderer horseRightFaceMetal = new ModelRenderer(this, 74, 13);
        horseRightFaceMetal.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2, scaleFactor);
        this.head.addChild(horseRightFaceMetal);
        this.horseFaceRopes = new ModelRenderer(this, 80, 12);
        this.horseFaceRopes.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
        this.head.addChild(horseFaceRopes);

        this.tackArray = new ModelRenderer[]{horseSaddleBottom, horseSaddleFront, horseSaddleBack, horseLeftSaddleMetal, horseLeftSaddleRope, horseRightSaddleMetal, horseRightSaddleRope, horseLeftFaceMetal, horseRightFaceMetal, horseFaceRopes};

        // Do not scale the reins, otherwise they don't display properly
        // I assume scaling doesn't work for things with 0 width./gradle
        ModelRenderer horseLeftRein = new ModelRenderer(this, 44, 10);
        horseLeftRein.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
        ModelRenderer horseRightRein = new ModelRenderer(this, 44, 5);
        horseRightRein.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
        this.neck.addChild(horseLeftRein);
        this.neck.addChild(horseRightRein);
        horseLeftRein.xRot = -0.5235988F;
        horseRightRein.xRot = -0.5235988F;
        this.extraTackArray = new ModelRenderer[]{horseLeftRein, horseRightRein};

        this.mane = new ModelRenderer(this, 58, 0);
        this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4, scaleFactor);
        this.mane.setPos(0.0F, 0.0F, -1.0F);
        this.neck.addChild(mane);

        int hornLength = 7;
        this.horn = new ModelRenderer(this, 84, 0);
        this.horn.addBox(-0.5F, -10.0F - hornLength, 2.0F, 1, hornLength, 1, scaleFactor);
        this.head.addChild(horn);
        int babyHornLength = 3;
        this.babyHorn = new ModelRenderer(this, 84, 0);
        this.babyHorn.addBox(-0.5F, -10.0F - babyHornLength, 2.0F, 1, babyHornLength, 1, scaleFactor);
        this.head.addChild(babyHorn);
    }

    @Override
    public void setupAnim(T entityIn, float p_225597_2_, float p_225597_3_, float p_225597_4_, float limbSwingAmount, float partialTickTime) {
        if (entityIn instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic horse = (AbstractHorseGenetic)entityIn;
            this.muleLeftEar.visible = horse.longEars();
            this.muleRightEar.visible = horse.longEars();
            this.horseLeftEar.visible = !horse.longEars();
            this.horseRightEar.visible = !horse.longEars();
            this.tailBase.visible = horse.fluffyTail();
            this.tailThin.visible = !horse.fluffyTail();
            this.ageScale = horse.getGangliness();
        }
        else {
            System.out.println("Attempting to use HorseGeneticModel on an unsupported entity type");
        }
        this.horn.visible = false;
        this.babyHorn.visible = false;

        boolean hasChest = false;
        if (entityIn instanceof AbstractChestedHorseEntity) {
            hasChest = ((AbstractChestedHorseEntity)entityIn).hasChest();
        }
        this.muleLeftChest.visible = hasChest;
        this.muleRightChest.visible = hasChest;

        boolean isSaddled = entityIn.isSaddled();
        boolean showReins = entityIn.isControlledByLocalInstance();

        for(ModelRenderer tack_piece : this.tackArray) {
            tack_piece.visible = isSaddled;
        }

        for(ModelRenderer extra_tack : this.extraTackArray) {
            extra_tack.visible = showReins && isSaddled;
        }

        // Probably because the body only rotates for rearing
        this.body.y = 11.0F;
     }

    /**
     * Fixes and offsets a rotation in the ModelHorse class.
     *//* Replaced by MathHelper.func_226167_j_() */
    private float updateHorseRotation(float prevRotation, float currentRotation, float partialTickTime)
    {
        float bodyRotation;

        for (bodyRotation = currentRotation - prevRotation; bodyRotation < -180.0F; bodyRotation += 360.0F)
        {
            ;
        }

        while (bodyRotation >= 180.0F)
        {
            bodyRotation -= 360.0F;
        }

        return prevRotation + partialTickTime * bodyRotation;
    }

    // This function renders the list of things given
    // I suspect this is for parts that are proportionally bigger on children
    public Iterable<ModelRenderer> headParts() {
        // In vanilla the neck goes here
        return ImmutableList.of();
    }

    // This function renders the list of things given
    // I suspect this is for parts that are always the same proportions
    protected Iterable<ModelRenderer> bodyParts() {
        return ImmutableList.of(this.body, this.neck, this.backLeftLeg, this.backRightLeg, this.frontLeftLeg, this.frontRightLeg, this.muleLeftChest, this.muleRightChest);
    }

    // Copied and modified from the familiar horses mod as allowed by the Unlicense
    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void renderToBuffer(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        Consumer<ModelRenderer> render = model -> model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        // ageScale is 0.5f for the smallest foals
        if (this.young) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(ageScale, 0.5F + ageScale * 0.5F, ageScale);
            // Move the foal's legs downward so they reach the ground
            matrixStackIn.translate(0.0F, 0.95F * (1.0F - ageScale), 0.0F);
        }

        ImmutableList.of(this.backLeftLeg, this.backRightLeg, 
                         this.frontLeftLeg, this.frontRightLeg).forEach(render);

        if (this.young) {

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            // Move the body downwards but not as far as the legs
            matrixStackIn.translate(0.0F, ageScale * 1.35F * (1.0F - ageScale), 0.0F);
            matrixStackIn.scale(ageScale, ageScale, ageScale);
        }

        ImmutableList.of(this.body, this.neck).forEach(render);

        if (this.young) {
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            float headScale = 0.5F + ageScale * ageScale * 0.5F;
            // Translate to match the body position
            matrixStackIn.translate(0.0F, ageScale * 1.35F * (1.0F - ageScale), 0.0F);
            matrixStackIn.scale(headScale, headScale, headScale);
            float extra = (headScale - ageScale) * 1.35F * (1.0F - ageScale);
            // The head's rest angle is 0.5235988F, 30 degrees
            matrixStackIn.translate(0.0F, extra * Math.cos(this.head.xRot), extra * Math.sin(this.head.xRot));
        }

        ImmutableList.of(this.head).forEach(render);

        if (this.young) {
            matrixStackIn.popPose();
        }

        ImmutableList.of(this.muleLeftChest, this.muleRightChest).forEach(render);
    }

    private void setMouthAnimations(float mouthOpenAmount) {
        this.upperMouth.y = 0.02F;
        this.lowerMouth.y = 0.0F;
        this.upperMouth.z = 0.02F - mouthOpenAmount;
        this.lowerMouth.z = mouthOpenAmount;
        this.upperMouth.xRot = -0.09424778F * mouthOpenAmount;
        this.lowerMouth.xRot = 0.15707964F * mouthOpenAmount;
        this.upperMouth.yRot = 0.0F;
        this.lowerMouth.yRot = 0.0F;
    }


    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setupAnim method.
     */
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTickTime);
        float bodyRotation = this.updateHorseRotation(entityIn.yBodyRotO, entityIn.yBodyRot, partialTickTime);
        float headRotation = this.updateHorseRotation(entityIn.yHeadRotO, entityIn.yHeadRot, partialTickTime);
        float interpolatedPitch = entityIn.xRotO + (entityIn.xRot - entityIn.xRotO) * partialTickTime;
        float headRelativeRotation = headRotation - bodyRotation;
        float f4 = interpolatedPitch * 0.017453292F;

        if (headRelativeRotation > 20.0F)
        {
            headRelativeRotation = 20.0F;
        }

        if (headRelativeRotation < -20.0F)
        {
            headRelativeRotation = -20.0F;
        }

        if (limbSwingAmount > 0.2F)
        {
            f4 += MathHelper.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount;
        }

        AbstractHorseEntity abstracthorse = (AbstractHorseEntity)entityIn;
        float grassEatingAmount = abstracthorse.getEatAnim(partialTickTime);
        float rearingAmount = abstracthorse.getStandAnim(partialTickTime);
        float notRearingAmount = 1.0F - rearingAmount;
        boolean isSwishingTail = abstracthorse.tailCounter != 0;
        boolean isSaddled = abstracthorse.isSaddled();
        boolean areStirrupsForward = abstracthorse.isControlledByLocalInstance();
        float ticks = (float)entityIn.tickCount + partialTickTime;
        float legRotationBase = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI);
        float legRotation1 = legRotationBase * 0.8F * limbSwingAmount;
        float neckBend = rearingAmount + 1.0F - Math.max(rearingAmount, grassEatingAmount);


        float mouthOpenAmount = abstracthorse.getMouthAnim(partialTickTime);
        this.setMouthAnimations(mouthOpenAmount);

        this.neck.setPos(0.0F, 4.0F, -10.0F);
        this.neck.xRot = rearingAmount * (0.2617994F + f4) + grassEatingAmount * 2.1816616F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * 0.5235988F + f4;
        this.neck.yRot = neckBend * headRelativeRotation * 0.017453292F;
        this.neck.y = rearingAmount * -6.0F + grassEatingAmount * 11.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.y;
        this.neck.z = rearingAmount * -1.0F + grassEatingAmount * -10.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.neck.z;
        this.body.xRot = rearingAmount * -((float)Math.PI / 4F);
        this.head.x = this.neck.x;
        this.head.y = this.neck.y;
        this.head.z = this.neck.z;
        this.head.xRot = this.neck.xRot;
        this.head.yRot = this.neck.yRot;
        float legRotationRearing = 0.2617994F * rearingAmount;
        float legRotationTicks = MathHelper.cos(ticks * 0.6F + (float)Math.PI);
        this.frontLeftLeg.y = -2.0F * rearingAmount + 9.0F * notRearingAmount;
        this.frontLeftLeg.z = -2.0F * rearingAmount + -8.0F * notRearingAmount;
        this.frontRightLeg.y = this.frontLeftLeg.y;
        this.frontRightLeg.z = this.frontLeftLeg.z;
        float legRotation4 = (-1.0471976F + legRotationTicks) * rearingAmount + legRotation1 * notRearingAmount;
        float legRotation5 = (-1.0471976F - legRotationTicks) * rearingAmount + -legRotation1 * notRearingAmount;
        this.backLeftLeg.xRot = legRotationRearing + -legRotationBase * 0.5F * limbSwingAmount * notRearingAmount;
        this.backRightLeg.xRot = legRotationRearing + legRotationBase * 0.5F * limbSwingAmount * notRearingAmount;
        this.frontLeftLeg.xRot = legRotation4;
        this.frontLeftShin.xRot = (this.frontLeftLeg.xRot + (float)Math.PI * Math.max(0.0F, 0.2F + legRotationTicks * 0.2F)) * rearingAmount + (legRotation1 + Math.max(0.0F, legRotationBase * 0.5F * limbSwingAmount)) * notRearingAmount - this.frontLeftLeg.xRot;
        // This might do the same thing
        //this.frontLeftShin.xRot = ((float)Math.PI * Math.max(0.0F, 0.2F + legRotationTicks * 0.2F)) * rearingAmount;
        this.frontRightLeg.xRot = legRotation5;
        this.frontRightShin.xRot = (this.frontRightLeg.xRot + (float)Math.PI * Math.max(0.0F, 0.2F - legRotationTicks * 0.2F)) * rearingAmount + (-legRotation1 + Math.max(0.0F, -legRotationBase * 0.5F * limbSwingAmount)) * notRearingAmount - this.frontRightLeg.xRot;
        //this.frontRightShin.xRot = ((float)Math.PI * Math.max(0.0F, 0.2F - legRotationTicks * 0.2F)) * rearingAmount;

        this.muleRightChest.y = 3.0F;
        this.muleRightChest.z = 10.0F;
        this.muleRightChest.y = rearingAmount * 5.5F + notRearingAmount * this.muleRightChest.y;
        this.muleRightChest.z = rearingAmount * 15.0F + notRearingAmount * this.muleRightChest.z;
        this.muleLeftChest.xRot = legRotation1 / 5.0F;
        this.muleRightChest.xRot = -legRotation1 / 5.0F;

        if (isSaddled)
        {
            this.muleLeftChest.y = this.muleRightChest.y;
            this.muleLeftChest.z = this.muleRightChest.z;

            if (areStirrupsForward)
            {
                this.horseLeftSaddleRope.xRot = -1.0471976F;
                this.horseRightSaddleRope.xRot = -1.0471976F;
                this.horseLeftSaddleRope.zRot = 0.0F;
                this.horseRightSaddleRope.zRot = 0.0F;
            }
            else
            {
                this.horseLeftSaddleRope.xRot = legRotation1 / 3.0F;
                this.horseRightSaddleRope.xRot = legRotation1 / 3.0F;
                this.horseLeftSaddleRope.zRot = legRotation1 / 5.0F;
                this.horseRightSaddleRope.zRot = -legRotation1 / 5.0F;
            }
        }

        float tailRotation = -1.3089969F + limbSwingAmount * 1.5F;
        float donkeyTailRotate = 0.17F + limbSwingAmount;

        if (tailRotation > 0.0F)
        {
            tailRotation = 0.0F;
        }

        if (isSwishingTail)
        {
            this.tailBase.yRot = MathHelper.cos(ticks * 0.7F);
            this.tailThin.yRot = MathHelper.cos(ticks * 0.7F);
            tailRotation = 0.0F;
            donkeyTailRotate = (float)Math.PI / 2f;
        }
        else
        {
            this.tailBase.yRot = 0.0F;
            this.tailThin.yRot = 0.0F;
        }

        this.tailBase.xRot = tailRotation;
        this.tailThin.xRot = donkeyTailRotate;

        // Make donkeys have a thinner mane
        if (abstracthorse instanceof AbstractHorseGenetic && ((AbstractHorseGenetic)abstracthorse).thinMane()) {
            this.mane.z = -1.0F;
        }
        else {
            this.mane.z = 0.0F;
        }
    }
}
