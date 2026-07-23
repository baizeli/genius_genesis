// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class NEW_BLOODCustomArmor - Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "new_bloodcustomarmor_- converted"), "main");
	private final ModelPart bipedHead;
	private final ModelPart armorHead;
	private final ModelPart dyeHat;
	private final ModelPart bone3;
	private final ModelPart skull;
	private final ModelPart bone2;
	private final ModelPart side;
	private final ModelPart bone;
	private final ModelPart bone9;
	private final ModelPart bipedBody;
	private final ModelPart armorBody;
	private final ModelPart bone4;
	private final ModelPart bipedRightArm;
	private final ModelPart armorRightArm;
	private final ModelPart bone8;
	private final ModelPart bone5;
	private final ModelPart bipedLeftArm;
	private final ModelPart armorLeftArm;
	private final ModelPart bone7;
	private final ModelPart bone6;
	private final ModelPart bipedLeftLeg;
	private final ModelPart armorLeftLeg;
	private final ModelPart leftskirt;
	private final ModelPart armorLeftBoot;
	private final ModelPart bipedRightLeg;
	private final ModelPart armorRightLeg;
	private final ModelPart rightskirt;
	private final ModelPart armorRightBoot;

	public NEW_BLOODCustomArmor - Converted(ModelPart root) {
		this.bipedHead = root.getChild("bipedHead");
		this.armorHead = this.bipedHead.getChild("armorHead");
		this.dyeHat = this.armorHead.getChild("dyeHat");
		this.bone3 = this.dyeHat.getChild("bone3");
		this.skull = this.dyeHat.getChild("skull");
		this.bone2 = this.skull.getChild("bone2");
		this.side = this.dyeHat.getChild("side");
		this.bone = this.dyeHat.getChild("bone");
		this.bone9 = this.dyeHat.getChild("bone9");
		this.bipedBody = root.getChild("bipedBody");
		this.armorBody = this.bipedBody.getChild("armorBody");
		this.bone4 = this.armorBody.getChild("bone4");
		this.bipedRightArm = root.getChild("bipedRightArm");
		this.armorRightArm = this.bipedRightArm.getChild("armorRightArm");
		this.bone8 = this.armorRightArm.getChild("bone8");
		this.bone5 = this.bone8.getChild("bone5");
		this.bipedLeftArm = root.getChild("bipedLeftArm");
		this.armorLeftArm = this.bipedLeftArm.getChild("armorLeftArm");
		this.bone7 = this.armorLeftArm.getChild("bone7");
		this.bone6 = this.bone7.getChild("bone6");
		this.bipedLeftLeg = root.getChild("bipedLeftLeg");
		this.armorLeftLeg = this.bipedLeftLeg.getChild("armorLeftLeg");
		this.leftskirt = this.armorLeftLeg.getChild("leftskirt");
		this.armorLeftBoot = this.bipedLeftLeg.getChild("armorLeftBoot");
		this.bipedRightLeg = root.getChild("bipedRightLeg");
		this.armorRightLeg = this.bipedRightLeg.getChild("armorRightLeg");
		this.rightskirt = this.armorRightLeg.getChild("rightskirt");
		this.armorRightBoot = this.bipedRightLeg.getChild("armorRightBoot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bipedHead = partdefinition.addOrReplaceChild("bipedHead", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition armorHead = bipedHead.addOrReplaceChild("armorHead", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition dyeHat = armorHead.addOrReplaceChild("dyeHat", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, 0.25F, -9.0F, 18.0F, 1.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(0, 20).addBox(-5.0F, -3.25F, -5.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(73, 0).addBox(-7.0F, 0.25F, 9.0F, 14.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(33, 35).addBox(-12.0F, 0.25F, -7.0F, 3.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(73, 5).addBox(-7.0F, 0.25F, -12.0F, 14.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(33, 51).addBox(9.0F, 0.25F, -7.0F, 3.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.0436F, 0.2182F, 0.2182F));

		PartDefinition cube_r1 = dyeHat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(41, 20).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.75F, -3.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r2 = dyeHat.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(116, 36).addBox(-5.0F, -4.0F, -1.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -5.8128F, -0.7954F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r3 = dyeHat.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(74, 25).addBox(-3.0F, 0.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -9.1842F, 1.2402F, 0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r4 = dyeHat.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(120, 9).addBox(0.0F, -2.0F, -2.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.75F, 5.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition bone3 = dyeHat.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(114, 30).addBox(-5.5F, -1.75F, 0.5F, 11.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(97, 20).addBox(-1.0F, -0.75F, -0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -1.75F, 4.5F, 0.4363F, 0.0F, 0.0F));

		PartDefinition cube_r5 = bone3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(33, 67).addBox(-4.5F, -5.0F, -7.0F, 9.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.5F, 3.75F, 1.0472F, 0.0F, 0.0F));

		PartDefinition skull = dyeHat.addOrReplaceChild("skull", CubeListBuilder.create(), PartPose.offset(6.0F, 0.75F, -4.0F));

		PartDefinition cube_r6 = skull.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 108).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -1.75F, 2.5F, -0.1309F, 0.3927F, -0.3927F));

		PartDefinition bone2 = skull.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0F, -1.75F, 3.5F));

		PartDefinition cube_r7 = bone2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 119).addBox(-3.0F, -4.25F, -3.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -3.5F, 3.75F, -0.4363F, 0.2618F, -0.3054F));

		PartDefinition cube_r8 = bone2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(116, 58).addBox(-2.0F, -6.0F, -4.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 2.0F, 2.5F, -0.3927F, 0.2618F, -0.3054F));

		PartDefinition side = dyeHat.addOrReplaceChild("side", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 6.0F));

		PartDefinition cube_r9 = side.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(68, 63).addBox(-6.0F, 0.0F, 0.0F, 12.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 5.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r10 = side.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 71).addBox(-1.0F, 0.0F, -6.0F, 1.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 0.25F, -6.0F, 0.0F, 0.0F, -1.5708F));

		PartDefinition cube_r11 = side.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(41, 33).addBox(-6.0F, 0.0F, -1.0F, 12.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, -17.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r12 = side.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(68, 50).addBox(0.0F, 0.0F, -6.0F, 1.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 0.25F, -6.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition bone = dyeHat.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(115, 95).addBox(-0.75F, -2.5F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(82, 100).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-4.0F, -4.75F, -4.25F, -0.7854F, -1.0472F, 1.0472F));

		PartDefinition cube_r13 = bone.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(99, 117).addBox(-0.5F, -0.5F, -2.5F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -1.0F, -0.5F, -0.1309F, -0.0436F, -0.1745F));

		PartDefinition bone9 = dyeHat.addOrReplaceChild("bone9", CubeListBuilder.create(), PartPose.offset(8.0F, 0.25F, 0.0F));

		PartDefinition HatLayer_r1 = bone9.addOrReplaceChild("HatLayer_r1", CubeListBuilder.create().texOffs(0, 238).addBox(0.0F, 1.0F, -5.0F, 9.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.0F, -0.25F, -1.0F, 0.0F, 0.2182F, -0.1309F));

		PartDefinition bipedBody = partdefinition.addOrReplaceChild("bipedBody", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition armorBody = bipedBody.addOrReplaceChild("armorBody", CubeListBuilder.create().texOffs(66, 67).addBox(-4.5F, 0.0F, -2.5F, 9.0F, 10.0F, 5.0F, new CubeDeformation(0.1F))
		.texOffs(0, 35).addBox(-5.0F, -0.25F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition belt_r1 = armorBody.addOrReplaceChild("belt_r1", CubeListBuilder.create().texOffs(122, 107).addBox(-1.5F, -1.5F, 0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(122, 112).addBox(-1.5F, -1.5F, 0.7F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.05F, 11.0F, -4.05F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r14 = armorBody.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(21, 117).addBox(-2.45F, -5.5F, -3.0F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(3.8F, 10.5F, 0.5F, 0.0F, 0.0F, -0.1745F));

		PartDefinition cube_r15 = armorBody.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(61, 117).addBox(-0.5F, -5.5F, -3.0F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-3.85F, 10.5F, 0.5F, 0.0F, 0.0F, 0.1745F));

		PartDefinition Chestplate_r1 = armorBody.addOrReplaceChild("Chestplate_r1", CubeListBuilder.create().texOffs(78, 124).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 8.8F, 7.4F, 0.0F, 0.0F, -0.7854F));

		PartDefinition Chestplate_r2 = armorBody.addOrReplaceChild("Chestplate_r2", CubeListBuilder.create().texOffs(78, 124).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, 3.8F, -2.85F, 0.0F, 0.0F, -0.7854F));

		PartDefinition Chestplate_r3 = armorBody.addOrReplaceChild("Chestplate_r3", CubeListBuilder.create().texOffs(114, 117).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 6.55F, -0.1F, -0.1309F, 0.0873F, 0.1745F));

		PartDefinition bone4 = armorBody.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(117, 83).addBox(-2.0F, -2.5F, 1.75F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 3.0F));

		PartDefinition cube_r16 = bone4.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(116, 71).mirror().addBox(-6.0F, -2.0F, -1.0F, 7.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 5.75F, 4.0F, 0.0F, 0.2182F, 0.2182F));

		PartDefinition cube_r17 = bone4.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(108, 0).mirror().addBox(-9.0F, -2.0F, -1.0F, 10.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.5F, 3.5F, 2.0F, -0.1309F, 0.2182F, -0.2182F));

		PartDefinition cube_r18 = bone4.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(92, 83).addBox(-6.0F, -2.0F, -1.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 2.0F, 0.0F, 0.3927F, 0.1745F));

		PartDefinition cube_r19 = bone4.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(0, 84).addBox(-2.0F, -2.0F, -1.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, 2.0F, 0.0F, -0.3927F, -0.1745F));

		PartDefinition cube_r20 = bone4.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(108, 0).addBox(-1.0F, -2.0F, -1.0F, 10.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, 3.5F, 2.0F, -0.1309F, -0.2182F, 0.2182F));

		PartDefinition cube_r21 = bone4.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(116, 71).addBox(-1.0F, -2.0F, -1.0F, 7.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 5.75F, 4.0F, 0.0F, -0.2182F, -0.2182F));

		PartDefinition bipedRightArm = partdefinition.addOrReplaceChild("bipedRightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition armorRightArm = bipedRightArm.addOrReplaceChild("armorRightArm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightArm_r1 = armorRightArm.addOrReplaceChild("rightArm_r1", CubeListBuilder.create().texOffs(116, 46).mirror().addBox(-2.5F, -0.5F, -3.0F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(-0.75F, 4.0F, 0.5F, 0.0F, 0.0F, 0.0873F));

		PartDefinition bone8 = armorRightArm.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(92, 95).addBox(-2.5F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition bone5 = bone8.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(115, 95).addBox(0.75F, -2.5F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(82, 100).addBox(0.5F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offset(-4.0F, -0.75F, -0.25F));

		PartDefinition cube_r22 = bone5.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(99, 117).addBox(-0.5F, -0.5F, -2.5F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, -1.0F, -0.5F, 0.2182F, 0.0F, 0.0F));

		PartDefinition bipedLeftArm = partdefinition.addOrReplaceChild("bipedLeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition armorLeftArm = bipedLeftArm.addOrReplaceChild("armorLeftArm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftArm_r1 = armorLeftArm.addOrReplaceChild("leftArm_r1", CubeListBuilder.create().texOffs(116, 46).addBox(-1.5F, -0.5F, -3.0F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.75F, 4.0F, 0.5F, 0.0F, 0.0F, -0.0873F));

		PartDefinition bone7 = armorLeftArm.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(0, 96).addBox(-2.5F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition bone6 = bone7.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(115, 95).addBox(0.75F, -2.5F, -4.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(82, 100).addBox(0.5F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(4.0F, -0.75F, -0.25F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r23 = bone6.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(99, 117).addBox(1.0F, -0.5F, -2.5F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -1.0F, -0.5F, -0.2182F, 0.0F, 0.0F));

		PartDefinition bipedLeftLeg = partdefinition.addOrReplaceChild("bipedLeftLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition armorLeftLeg = bipedLeftLeg.addOrReplaceChild("armorLeftLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftskirt = armorLeftLeg.addOrReplaceChild("leftskirt", CubeListBuilder.create(), PartPose.offsetAndRotation(1.375F, -1.75F, 0.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition lp_4_r1 = leftskirt.addOrReplaceChild("lp_4_r1", CubeListBuilder.create().texOffs(114, 20).addBox(0.5F, -18.0F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.6388F, 25.6232F, 0.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition lp_4_r2 = leftskirt.addOrReplaceChild("lp_4_r2", CubeListBuilder.create().texOffs(27, 83).addBox(-2.0F, -5.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3038F, 4.4547F, 0.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition lp_5_r1 = leftskirt.addOrReplaceChild("lp_5_r1", CubeListBuilder.create().texOffs(74, 10).addBox(0.25F, -29.25F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(97, 10).addBox(0.25F, -22.25F, -3.5F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(18.0171F, 22.8241F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition armorLeftBoot = bipedLeftLeg.addOrReplaceChild("armorLeftBoot", CubeListBuilder.create().texOffs(93, 25).addBox(-2.25F, 4.5F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(95, 67).addBox(-2.25F, 4.5F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bipedRightLeg = partdefinition.addOrReplaceChild("bipedRightLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition armorRightLeg = bipedRightLeg.addOrReplaceChild("armorRightLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightskirt = armorRightLeg.addOrReplaceChild("rightskirt", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.125F, -1.625F, 0.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition rp_4_r1 = rightskirt.addOrReplaceChild("rp_4_r1", CubeListBuilder.create().texOffs(71, 83).addBox(-2.0F, -5.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.375F, 4.2949F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition rp_5_r1 = rightskirt.addOrReplaceChild("rp_5_r1", CubeListBuilder.create().texOffs(48, 83).addBox(-4.25F, -29.25F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(99, 107).addBox(-4.25F, -22.25F, -3.5F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-18.2671F, 22.6991F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition rp_4_r2 = rightskirt.addOrReplaceChild("rp_4_r2", CubeListBuilder.create().texOffs(40, 115).addBox(-2.0F, -1.5F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.625F, 9.924F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition armorRightBoot = bipedRightLeg.addOrReplaceChild("armorRightBoot", CubeListBuilder.create().texOffs(95, 39).addBox(-2.75F, -1.25F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(95, 53).addBox(-2.75F, -1.25F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 5.75F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bipedHead.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bipedBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bipedRightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bipedLeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bipedLeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bipedRightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}