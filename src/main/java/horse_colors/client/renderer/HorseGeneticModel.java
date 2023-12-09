package sekelsta.horse_colors.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;

import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

@OnlyIn(Dist.CLIENT)
public class HorseGeneticModel<T extends AbstractHorse> extends AgeableListModel<T>
{
    private static final String BODY = "body";
    private static final String HEAD = "head";
    private static final String NECK = "neck";

    private static final String BACK_LEFT_THIGH = "back_left_thigh";
    private static final String BACK_LEFT_SHIN = "back_left_shin";
    private static final String BACK_LEFT_HOOF = "back_left_hoof";
    private static final String BACK_RIGHT_THIGH = "back_right_thigh";
    private static final String BACK_RIGHT_SHIN = "back_right_shin";
    private static final String BACK_RIGHT_HOOF = "back_right_hoof";
    private static final String FRONT_LEFT_LEG = "front_left_leg";
    private static final String FRONT_LEFT_SHIN = "front_left_shin";
    private static final String FRONT_LEFT_HOOF = "front_left_hoof";
    private static final String FRONT_RIGHT_LEG = "front_right_leg";
    private static final String FRONT_RIGHT_SHIN = "front_right_shin";
    private static final String FRONT_RIGHT_HOOF = "front_right_hoof";
    private static final String UPPER_MOUTH = "upper_mouth";
    private static final String LOWER_MOUTH = "lower_mouth";

    private static final String TAIL_BASE = "tail_base";
    private static final String TAIL_MIDDLE = "tail_middle";
    private static final String TAIL_TIP = "tail_tip";
    private static final String TAIL_THIN = "tail_thin";
    private static final String TAIL_TUFT = "tail_tuft";

    private static final String STIFF_MANE = "stiff_mane";

    private static final String HORSE_LEFT_EAR = "horse_left_ear";
    private static final String HORSE_RIGHT_EAR = "horse_right_ear";
    private static final String MULE_LEFT_EAR = "mule_left_ear";
    private static final String MULE_RIGHT_EAR = "mule_right_ear";

    private static final String HORN = "horn";
    private static final String BABY_HORN = "baby_horn";

    private static final String LEFT_CHEST = "left_chest";
    private static final String RIGHT_CHEST = "right_chest";

    private static final String FACE_ROPES = "face_ropes";
    private static final String LEFT_BIT = "left_bit";
    private static final String RIGHT_BIT = "right_bit";
    private static final String LEFT_REIN = "left_rein";
    private static final String RIGHT_REIN = "right_rein";

    private static final String SADDLE_BASE = "saddle_base";
    private static final String SADDLE_FRONT = "saddle_front";
    private static final String SADDLE_BACK = "saddle_back";
    private static final String LEFT_STIRRUP_LEATHER = "left_stirrup_leather";
    private static final String RIGHT_STIRRUP_LEATHER = "right_stirrup_leather";
    private static final String LEFT_STIRRUP = "left_stirrup";
    private static final String RIGHT_STIRRUP = "right_stirrup";


    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart neck;

    private final ModelPart backLeftThigh;
    private final ModelPart backLeftShin;
    private final ModelPart backLeftHoof;
    private final ModelPart backRightThigh;
    private final ModelPart backRightShin;
    private final ModelPart backRightHoof;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontLeftShin;
    private final ModelPart frontLeftHoof;
    private final ModelPart frontRightLeg;
    private final ModelPart frontRightShin;
    private final ModelPart frontRightHoof;
    private final ModelPart upperMouth;
    private final ModelPart lowerMouth;

    // Horse and mule tail
    private final ModelPart tailBase;
    private final ModelPart tailMiddle;
    private final ModelPart tailTip;
    // Donkey tail
    private final ModelPart tailThin;
    private final ModelPart tailTuft;

    private final ModelPart stiffMane;

    private final ModelPart horseLeftEar;
    private final ModelPart horseRightEar;
    private final ModelPart muleLeftEar;
    private final ModelPart muleRightEar;
    
    private final ModelPart horn;
    private final ModelPart babyHorn;

    private final ModelPart leftChest;
    private final ModelPart rightChest;

    private final ModelPart faceRopes;
    private final ModelPart leftBit;
    private final ModelPart rightBit;
    private final ModelPart leftRein;
    private final ModelPart rightRein;

    private final ModelPart saddleBase;
    private final ModelPart saddleFront;
    private final ModelPart saddleBack;
    private final ModelPart leftStirrupLeather;
    private final ModelPart rightStirrupLeather;
    private final ModelPart leftStirrup;
    private final ModelPart rightStirrup;

    private final ModelPart[] tackArray;
    private final ModelPart[] extraTackArray;

    private float ageScale = 0.5f;

    public HorseGeneticModel(ModelPart root)
    {
        // Initialize AgeableModel
        super(false, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
        this.body = root.getChild(BODY);
        this.head = root.getChild(HEAD);
        this.neck = root.getChild(NECK);
        this.backLeftThigh = root.getChild(BACK_LEFT_THIGH);
        this.backLeftShin = this.backLeftThigh.getChild(BACK_LEFT_SHIN);
        this.backLeftHoof = this.backLeftShin.getChild(BACK_LEFT_HOOF);
        this.backRightThigh = root.getChild(BACK_RIGHT_THIGH);
        this.backRightShin = this.backRightThigh.getChild(BACK_RIGHT_SHIN);
        this.backRightHoof = this.backRightShin.getChild(BACK_RIGHT_HOOF);
        this.frontLeftLeg = root.getChild(FRONT_LEFT_LEG);
        this.frontLeftShin = this.frontLeftLeg.getChild(FRONT_LEFT_SHIN);
        this.frontLeftHoof = this.frontLeftShin.getChild(FRONT_LEFT_HOOF);
        this.frontRightLeg = root.getChild(FRONT_RIGHT_LEG);
        this.frontRightShin = this.frontRightLeg.getChild(FRONT_RIGHT_SHIN);
        this.frontRightHoof = this.frontRightShin.getChild(FRONT_RIGHT_HOOF);
        this.upperMouth = this.head.getChild(UPPER_MOUTH);
        this.lowerMouth = this.head.getChild(LOWER_MOUTH);
        
        this.tailBase = this.body.getChild(TAIL_BASE);
        this.tailMiddle = this.tailBase.getChild(TAIL_MIDDLE);
        this.tailTip = this.tailMiddle.getChild(TAIL_TIP);
        this.tailThin = this.body.getChild(TAIL_THIN);
        this.tailTuft = this.tailThin.getChild(TAIL_TUFT);
        
        this.stiffMane = this.neck.getChild(STIFF_MANE);
        
        this.horseLeftEar = this.head.getChild(HORSE_LEFT_EAR);
        this.horseRightEar = this.head.getChild(HORSE_RIGHT_EAR);
        this.muleLeftEar = this.head.getChild(MULE_LEFT_EAR);
        this.muleRightEar = this.head.getChild(MULE_RIGHT_EAR);
        
        this.horn = this.head.getChild(HORN);
        this.babyHorn = this.head.getChild(BABY_HORN);

        this.leftChest = root.getChild(LEFT_CHEST);
        this.rightChest = root.getChild(RIGHT_CHEST);
        
        this.faceRopes = this.head.getChild(FACE_ROPES);
        this.leftBit = this.head.getChild(LEFT_BIT);
        this.rightBit = this.head.getChild(RIGHT_BIT);
        this.leftRein = this.neck.getChild(LEFT_REIN);
        this.rightRein = this.neck.getChild(RIGHT_REIN);

        this.saddleBase = this.body.getChild(SADDLE_BASE);
        this.saddleFront = this.saddleBase.getChild(SADDLE_FRONT);
        this.saddleBack = this.saddleBase.getChild(SADDLE_BACK);
        this.leftStirrupLeather = this.saddleBase.getChild(LEFT_STIRRUP_LEATHER);
        this.rightStirrupLeather = this.saddleBase.getChild(RIGHT_STIRRUP_LEATHER);
        this.leftStirrup = this.leftStirrupLeather.getChild(LEFT_STIRRUP);
        this.rightStirrup = this.rightStirrupLeather.getChild(RIGHT_STIRRUP);


        this.tackArray = new ModelPart[]{saddleBase, saddleFront, saddleBack, leftStirrup, leftStirrupLeather, rightStirrup, rightStirrupLeather, leftBit, rightBit, faceRopes};
        this.extraTackArray = new ModelPart[]{leftRein, rightRein};
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createBodyMesh(CubeDeformation.NONE), 128, 128);
    }

    public static LayerDefinition createArmorLayer() {
        return LayerDefinition.create(createBodyMesh(new CubeDeformation(0.1F)), 128, 128);
    }

    public static MeshDefinition createBodyMesh(CubeDeformation cubeDeformation) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bodyDef = root.addOrReplaceChild(
            BODY, 
            CubeListBuilder.create().texOffs(0, 34).addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24, new CubeDeformation(0.05F)), 
            PartPose.offset(0.0F, 11.0F, 9.0F)
        );
        PartDefinition headDef = root.addOrReplaceChild(
            HEAD,
            CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7),
            PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F, ((float)Math.PI / 6F), 0.0F, 0.0F)
        );
        PartDefinition neckDef = root.addOrReplaceChild(
            NECK,
            CubeListBuilder.create().texOffs(0, 12).addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8),
            PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F, ((float)Math.PI / 6F), 0.0F, 0.0F)
        );

        // When making something a child that wasn't before, subtract the
        // parent's rotationPoint (now PartPose offset)
        PartDefinition backLeftThighDef = root.addOrReplaceChild(
            BACK_LEFT_THIGH,
            CubeListBuilder.create().texOffs(78, 29).addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5),
            PartPose.offset(4.0F, 9.0F, 11.0F)
        );
        PartDefinition backLeftShinDef = backLeftThighDef.addOrReplaceChild(
            BACK_LEFT_SHIN,
            CubeListBuilder.create().texOffs(78, 43).addBox(-2.0F, 0.0F, -1F, 3, 5, 3),
            PartPose.offset(0.0F, 7.0F, 0.0F)
        );
        PartDefinition backLeftHoofDef = backLeftShinDef.addOrReplaceChild(
            BACK_LEFT_HOOF,
            CubeListBuilder.create().texOffs(78, 51).addBox(-2.5F, 5.0F, -1.5F, 4, 3, 4),
            PartPose.ZERO
        );

        PartDefinition backRightThighDef = root.addOrReplaceChild(
            BACK_RIGHT_THIGH,
            CubeListBuilder.create().texOffs(96, 29).addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5),
            PartPose.offset(-4.0F, 9.0F, 11.0F)
        );
        PartDefinition backRightShinDef = backRightThighDef.addOrReplaceChild(
            BACK_RIGHT_SHIN,
            CubeListBuilder.create().texOffs(96, 43).addBox(-1.0F, 0.0F, -1F, 3, 5, 3),
            PartPose.offset(0.0F, 7.0F, 0.0F)
        );
        PartDefinition backRightHoofDef = backRightShinDef.addOrReplaceChild(
            BACK_RIGHT_HOOF,
            CubeListBuilder.create().texOffs(96, 51).addBox(-1.5F, 5.0F, -1.5F, 4, 3, 4),
            PartPose.ZERO
        );

        PartDefinition frontLeftLegDef = root.addOrReplaceChild(
            FRONT_LEFT_LEG,
            CubeListBuilder.create().texOffs(44, 29).addBox(-1.9F, -1.0F, -1.0F, 3, 8, 4),
            PartPose.offset(4.0F, 9.0F, -8.0F)
        );
        PartDefinition frontLeftShinDef = frontLeftLegDef.addOrReplaceChild(
            FRONT_LEFT_SHIN,
            CubeListBuilder.create().texOffs(44, 41).addBox(-1.9F, 0.0F, -0.5F, 3, 5, 3),
            PartPose.offset(0.0F, 7.0F, 0.0F)
        );
        PartDefinition frontLeftHoofDef = frontLeftShinDef.addOrReplaceChild(
            FRONT_LEFT_HOOF,
            CubeListBuilder.create().texOffs(44, 51).addBox(-2.4F, 5.0F, -1.0F, 4, 3, 4),
            PartPose.ZERO
        );

        PartDefinition frontRightLegDef = root.addOrReplaceChild(
            FRONT_RIGHT_LEG,
            CubeListBuilder.create().texOffs(60, 29).addBox(-1.1F, -1.0F, -1.0F, 3, 8, 4),
            PartPose.offset(-4.0F, 9.0F, -8.0F)
        );
        PartDefinition frontRightShinDef = frontRightLegDef.addOrReplaceChild(
            FRONT_RIGHT_SHIN,
            CubeListBuilder.create().texOffs(60, 41).addBox(-1.1F, 0.0F, -0.5F, 3, 5, 3),
            PartPose.offset(0.0F, 7.0F, 0.0F)
        );
        PartDefinition frontRightHoofDef = frontRightShinDef.addOrReplaceChild(
            FRONT_RIGHT_HOOF,
            CubeListBuilder.create().texOffs(60, 51).addBox(-1.6F, 5.0F, -1.0F, 4, 3, 4),
            PartPose.ZERO
        );

        PartDefinition upperMouthDef = headDef.addOrReplaceChild(
            UPPER_MOUTH,
            CubeListBuilder.create().texOffs(24, 18).addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6),
            PartPose.ZERO
        );
        PartDefinition lowerMouthDef = headDef.addOrReplaceChild(
            LOWER_MOUTH,
            CubeListBuilder.create().texOffs(24, 27).addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5),
            PartPose.ZERO
        );

        PartDefinition tailBaseDef = bodyDef.addOrReplaceChild(
            TAIL_BASE,
            CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3),
            PartPose.offsetAndRotation(0.0F, -8.0F, 5.0F, -1.134464F, 0F, 0F)
        );
        PartDefinition tailMiddleDef = tailBaseDef.addOrReplaceChild(
            TAIL_MIDDLE,
            CubeListBuilder.create().texOffs(38, 7).addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7),
            PartPose.ZERO
        );
        PartDefinition tailTipDef = tailMiddleDef.addOrReplaceChild(
            TAIL_TIP,
            CubeListBuilder.create().texOffs(24, 3).addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7),
            PartPose.offsetAndRotation(0F, 0F, 0F, -0.2618004F, 0F, 0F)
        );
        PartDefinition tailThinDef = bodyDef.addOrReplaceChild(
            TAIL_THIN,
            CubeListBuilder.create().texOffs(116, 0).addBox(-0.5F, 0.0F, 0.5F, 1, 5, 1),
            // Rotation may not actually be needed
            PartPose.offsetAndRotation(0.0F, -6F, 4.0F, -1.134464F, 0F, 0F)
        );
        PartDefinition tailTuftDef = tailThinDef.addOrReplaceChild(
            TAIL_TUFT,
            CubeListBuilder.create().texOffs(120, 0).addBox(-1.0F, 0F, 0.25F, 2, 6, 2),
            PartPose.offset(0.0F, 5.0F, 0.0F)
        );
            // Rotation amy 

        PartDefinition stiffManeDef = neckDef.addOrReplaceChild(
            STIFF_MANE,
            CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4),
            PartPose.offset(0.0F, 0.0F, -1.0F)
        );

        PartDefinition horseLeftEarDef = headDef.addOrReplaceChild(
            HORSE_LEFT_EAR,
            CubeListBuilder.create().texOffs(0, 0).addBox(0.45F, -12.0F, 4.0F, 2, 3, 1),
            PartPose.ZERO
        );
        PartDefinition horseRightEarDef = headDef.addOrReplaceChild(
            HORSE_RIGHT_EAR,
            CubeListBuilder.create().texOffs(0, 0).addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1),
            PartPose.ZERO
        );
        PartDefinition muleLeftEarDef = headDef.addOrReplaceChild(
            MULE_LEFT_EAR,
            CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1),
            PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.2617994F)
        );
        PartDefinition muleRightEarDef = headDef.addOrReplaceChild(
            MULE_RIGHT_EAR,
            CubeListBuilder.create().texOffs(0, 12).addBox(0.0F, -16.0F, 4.0F, 2, 7, 1),
            PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.2617994F)
        );

        final int hornLength = 7;
        PartDefinition hornDef = headDef.addOrReplaceChild(
            HORN,
            CubeListBuilder.create().texOffs(84, 0).addBox(-0.5F, -10.0F - hornLength, 2.0F, 1, hornLength, 1),
            PartPose.ZERO
        );
        final int babyHornLength = 3;
        PartDefinition babyHornDef = headDef.addOrReplaceChild(
            BABY_HORN,
            CubeListBuilder.create().texOffs(84, 0).addBox(-0.5F, -10.0F - babyHornLength, 2.0F, 1, babyHornLength, 1),
            PartPose.ZERO
        );

        PartDefinition leftChestDef = root.addOrReplaceChild(
            LEFT_CHEST,
            CubeListBuilder.create().texOffs(0, 34).addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3),
            PartPose.offsetAndRotation(-7.5F, 3.0F, 10.0F, 0F, (float)Math.PI / 2F, 0F)
        );
        PartDefinition rightChestDef = root.addOrReplaceChild(
            RIGHT_CHEST,
            CubeListBuilder.create().texOffs(0, 47).addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3),
            PartPose.offsetAndRotation(4.5F, 3.0F, 10.0F, 0F, (float)Math.PI / 2F, 0F)
        );

        PartDefinition faceRopesDef = headDef.addOrReplaceChild(
            FACE_ROPES,
            CubeListBuilder.create().texOffs(80, 12).addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, new CubeDeformation(0.2F)),
            PartPose.ZERO
        );
        PartDefinition leftBitDef = headDef.addOrReplaceChild(
            LEFT_BIT,
            CubeListBuilder.create().texOffs(74, 13).addBox(1.5F, -8.0F, -4.0F, 1, 2, 2),
            PartPose.ZERO
        );
        PartDefinition rightBitDef = headDef.addOrReplaceChild(
            RIGHT_BIT,
            CubeListBuilder.create().texOffs(74, 13).addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2),
            PartPose.ZERO
        );
        PartDefinition leftReinDef = neckDef.addOrReplaceChild(
            LEFT_REIN,
            CubeListBuilder.create().texOffs(44, 10).addBox(2.6F, -6.0F, -6.0F, 0, 3, 16),
            PartPose.offsetAndRotation(0F, 0F, 0F, -0.5235988F, 0F, 0F)
        );
        PartDefinition rightReinDef = neckDef.addOrReplaceChild(
            RIGHT_REIN,
            CubeListBuilder.create().texOffs(44, 5).addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16),
            PartPose.offsetAndRotation(0F, 0F, 0F, -0.5235988F, 0F, 0F)
        );

        PartDefinition saddleBaseDef = bodyDef.addOrReplaceChild(
            SADDLE_BASE,
            CubeListBuilder.create().texOffs(80, 0).addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8),
            PartPose.offset(0.0F, -9.0F, -7.0F)
        );
        PartDefinition saddleFrontDef = saddleBaseDef.addOrReplaceChild(
            SADDLE_FRONT,
            CubeListBuilder.create().texOffs(106, 9).addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2),
            PartPose.ZERO
        );
        PartDefinition saddleBackDef = saddleBaseDef.addOrReplaceChild(
            SADDLE_BACK,
            CubeListBuilder.create().texOffs(80, 9).addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2),
            PartPose.ZERO
        );
        PartDefinition leftStirrupLeatherDef = saddleBaseDef.addOrReplaceChild(
            LEFT_STIRRUP_LEATHER,
            CubeListBuilder.create().texOffs(70, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),
            PartPose.offset(5.0F, 1.0F, 0.0F)
        );
        PartDefinition rightStirrupLeatherDef = saddleBaseDef.addOrReplaceChild(
            RIGHT_STIRRUP_LEATHER,
            CubeListBuilder.create().texOffs(80, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),
            PartPose.offset(-5.0F, 1.0F, 0.0F)
        );
        PartDefinition leftStirrupDef = leftStirrupLeatherDef.addOrReplaceChild(
            LEFT_STIRRUP,
            CubeListBuilder.create().texOffs(74, 0).addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2),
            PartPose.ZERO
        );
        PartDefinition rightStirrupDef = rightStirrupLeatherDef.addOrReplaceChild(
            RIGHT_STIRRUP,
            CubeListBuilder.create().texOffs(74, 4).addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2),
            PartPose.ZERO
        );

        return meshDefinition;
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
        if (entityIn instanceof AbstractChestedHorse) {
            hasChest = ((AbstractChestedHorse)entityIn).hasChest();
        }
        this.leftChest.visible = hasChest;
        this.rightChest.visible = hasChest;

        boolean isSaddled = entityIn.isSaddled();
        boolean showReins = entityIn.getControllingPassenger() != null;

        for(ModelPart tack_piece : this.tackArray) {
            tack_piece.visible = isSaddled;
        }

        for(ModelPart extra_tack : this.extraTackArray) {
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
    public Iterable<ModelPart> headParts() {
        // In vanilla the neck goes here
        return ImmutableList.of();
    }

    // This function renders the list of things given
    // I suspect this is for parts that are always the same proportions
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.neck, this.backLeftThigh, this.backRightThigh, this.frontLeftLeg, this.frontRightLeg, this.leftChest, this.rightChest);
    }

    // Copied and modified from the familiar horses mod as allowed by the Unlicense
    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void renderToBuffer(@Nonnull PoseStack matrixStackIn, @Nonnull VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        Consumer<ModelPart> render = model -> model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        // ageScale is 0.5f for the smallest foals
        if (this.young) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(ageScale, 0.5F + ageScale * 0.5F, ageScale);
            // Move the foal's legs downward so they reach the ground
            matrixStackIn.translate(0.0F, 0.95F * (1.0F - ageScale), 0.0F);
        }

        ImmutableList.of(this.backLeftThigh, this.backRightThigh, 
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

        ImmutableList.of(this.leftChest, this.rightChest).forEach(render);
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
        float interpolatedPitch = entityIn.xRotO + (entityIn.getXRot() - entityIn.xRotO) * partialTickTime;
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
            f4 += Mth.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount;
        }

        AbstractHorse abstracthorse = (AbstractHorse)entityIn;
        float grassEatingAmount = abstracthorse.getEatAnim(partialTickTime);
        float rearingAmount = abstracthorse.getStandAnim(partialTickTime);
        float notRearingAmount = 1.0F - rearingAmount;
        boolean isSwishingTail = abstracthorse.tailCounter != 0;
        boolean isSaddled = abstracthorse.isSaddled();
        boolean areStirrupsForward = abstracthorse.isControlledByLocalInstance();
        float ticks = (float)entityIn.tickCount + partialTickTime;
        float legRotationBase = Mth.cos(limbSwing * 0.6662F + (float)Math.PI);
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
        float legRotationTicks = Mth.cos(ticks * 0.6F + (float)Math.PI);
        this.frontLeftLeg.y = -2.0F * rearingAmount + 9.0F * notRearingAmount;
        this.frontLeftLeg.z = -2.0F * rearingAmount + -8.0F * notRearingAmount;
        this.frontRightLeg.y = this.frontLeftLeg.y;
        this.frontRightLeg.z = this.frontLeftLeg.z;
        float legRotation4 = (-1.0471976F + legRotationTicks) * rearingAmount + legRotation1 * notRearingAmount;
        float legRotation5 = (-1.0471976F - legRotationTicks) * rearingAmount + -legRotation1 * notRearingAmount;
        this.backLeftThigh.xRot = legRotationRearing + -legRotationBase * 0.5F * limbSwingAmount * notRearingAmount;
        this.backRightThigh.xRot = legRotationRearing + legRotationBase * 0.5F * limbSwingAmount * notRearingAmount;
        this.frontLeftLeg.xRot = legRotation4;
        this.frontLeftShin.xRot = (this.frontLeftLeg.xRot + (float)Math.PI * Math.max(0.0F, 0.2F + legRotationTicks * 0.2F)) * rearingAmount + (legRotation1 + Math.max(0.0F, legRotationBase * 0.5F * limbSwingAmount)) * notRearingAmount - this.frontLeftLeg.xRot;
        // This might do the same thing
        //this.frontLeftShin.xRot = ((float)Math.PI * Math.max(0.0F, 0.2F + legRotationTicks * 0.2F)) * rearingAmount;
        this.frontRightLeg.xRot = legRotation5;
        this.frontRightShin.xRot = (this.frontRightLeg.xRot + (float)Math.PI * Math.max(0.0F, 0.2F - legRotationTicks * 0.2F)) * rearingAmount + (-legRotation1 + Math.max(0.0F, -legRotationBase * 0.5F * limbSwingAmount)) * notRearingAmount - this.frontRightLeg.xRot;
        //this.frontRightShin.xRot = ((float)Math.PI * Math.max(0.0F, 0.2F - legRotationTicks * 0.2F)) * rearingAmount;

        this.rightChest.y = 3.0F;
        this.rightChest.z = 10.0F;
        this.rightChest.y = rearingAmount * 5.5F + notRearingAmount * this.rightChest.y;
        this.rightChest.z = rearingAmount * 15.0F + notRearingAmount * this.rightChest.z;
        this.leftChest.xRot = legRotation1 / 5.0F;
        this.rightChest.xRot = -legRotation1 / 5.0F;

        if (isSaddled)
        {
            this.leftChest.y = this.rightChest.y;
            this.leftChest.z = this.rightChest.z;

            if (areStirrupsForward)
            {
                this.leftStirrupLeather.xRot = -1.0471976F;
                this.rightStirrupLeather.xRot = -1.0471976F;
                this.leftStirrupLeather.zRot = 0.0F;
                this.rightStirrupLeather.zRot = 0.0F;
            }
            else
            {
                this.leftStirrupLeather.xRot = legRotation1 / 3.0F;
                this.rightStirrupLeather.xRot = legRotation1 / 3.0F;
                this.leftStirrupLeather.zRot = legRotation1 / 5.0F;
                this.rightStirrupLeather.zRot = -legRotation1 / 5.0F;
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
            this.tailBase.yRot = Mth.cos(ticks * 0.7F);
            this.tailThin.yRot = Mth.cos(ticks * 0.7F);
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
            this.stiffMane.z = -1.0F;
        }
        else {
            this.stiffMane.z = 0.0F;
        }
    }
}
