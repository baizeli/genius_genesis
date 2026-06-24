package miku.united_as_one.genesis.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class WingModel<T extends Entity> extends HierarchicalModel<T> {
    private static final float TRANSITION_DURATION = 5.0F;

    private static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.0F).looping()
            .addAnimation("left_spell_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-71.5757F, 9.1708F, 12.2423F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(-60.0F, 15.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-71.5757F, 9.1708F, 12.2423F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("left_spell_wing", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(-10.0F, 20.0F, 5.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.posVec(-10.0F, 20.0F, 5.0F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("right_spell_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-71.5757F, -9.1708F, -12.2423F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(-60.0F, -15.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-71.5757F, -9.1708F, -12.2423F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("right_spell_wing", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(10.0F, 20.0F, 5.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.posVec(10.0F, 20.0F, 5.0F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -10.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
            .build();

    private static final AnimationDefinition FLY = AnimationDefinition.Builder.withLength(1.5F).looping()
            .addAnimation("left_spell_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 60.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("right_spell_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, -60.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
            .build();

    private static final AnimationDefinition RUN = AnimationDefinition.Builder.withLength(3.0F).looping()
            .addAnimation("left_spell_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 50.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 66.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 50.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("right_spell_wing", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -50.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, -66.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, -50.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
            .build();

    private final ModelPart root;
    private final ModelPart tempRoot;

    private final AnimationState idleAnimationState = new AnimationState();
    private final AnimationState flyAnimationState = new AnimationState();
    private final AnimationState runAnimationState = new AnimationState();
    private AnimationState activeAnimationState;
    private AnimationState previousAnimationState;
    private AnimationDefinition previousAnimationDefinition;
    private float transitionProgress = 1.0F;

    public WingModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDefinition = mesh.getRoot();

        rootDefinition.addOrReplaceChild("left_spell_wing",
                CubeListBuilder.create()
                        .texOffs(0, -77)
                        .addBox(0.0F, -20.0F, 0.0F, 0.0F, 43.0F, 77.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -1.309F, 0.0F));
        rootDefinition.addOrReplaceChild("right_spell_wing",
                CubeListBuilder.create()
                        .texOffs(0, -29)
                        .addBox(0.0F, -20.0F, 0.0F, 0.0F, 43.0F, 77.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 1.309F, 0.0F));

        LayerDefinition layerDefinition = LayerDefinition.create(mesh, 154, 128);
        this.root = layerDefinition.bakeRoot();
        this.tempRoot = layerDefinition.bakeRoot();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);

        boolean flying = false;
        boolean running = false;
        if (entity instanceof Player player) {
            flying = player.getAbilities().flying;
            running = player.isSprinting() && !flying;
        }

        AnimationState targetState = idleAnimationState;
        AnimationDefinition targetDefinition = IDLE;
        if (flying) {
            targetState = flyAnimationState;
            targetDefinition = FLY;
        } else if (running) {
            targetState = runAnimationState;
            targetDefinition = RUN;
        }

        if (activeAnimationState != targetState) {
            previousAnimationState = activeAnimationState;
            previousAnimationDefinition = currentAnimationDefinition();
            activeAnimationState = targetState;
            transitionProgress = 0.0F;
            if (!targetState.isStarted()) {
                targetState.start((int) ageInTicks);
            }
        }

        if (transitionProgress < 1.0F) {
            transitionProgress = Math.min(1.0F, transitionProgress + 1.0F / (TRANSITION_DURATION * 20.0F));
            if (previousAnimationState != null && previousAnimationDefinition != null) {
                applyTransitionPose(transitionProgress, previousAnimationState, previousAnimationDefinition,
                        targetState, targetDefinition, ageInTicks);
                return;
            }
        }

        animate(targetState, targetDefinition, ageInTicks, 1.0F);
    }

    private AnimationDefinition currentAnimationDefinition() {
        if (activeAnimationState == flyAnimationState) {
            return FLY;
        }
        if (activeAnimationState == runAnimationState) {
            return RUN;
        }
        return IDLE;
    }

    private void applyTransitionPose(float progress, AnimationState previousState, AnimationDefinition previousDefinition,
                                     AnimationState targetState, AnimationDefinition targetDefinition, float ageInTicks) {
        tempRoot.getAllParts().forEach(ModelPart::resetPose);

        animate(previousState, previousDefinition, ageInTicks, 1.0F);
        copyModelPose(root, tempRoot);

        root.getAllParts().forEach(ModelPart::resetPose);
        animate(targetState, targetDefinition, ageInTicks, 1.0F);

        interpolatePoses(tempRoot, root, progress);
    }

    private void copyModelPose(ModelPart source, ModelPart target) {
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
        copyChildPose(source, target, "left_spell_wing");
        copyChildPose(source, target, "right_spell_wing");
    }

    private void copyChildPose(ModelPart sourceRoot, ModelPart targetRoot, String childName) {
        if (sourceRoot.hasChild(childName) && targetRoot.hasChild(childName)) {
            copyModelPose(sourceRoot.getChild(childName), targetRoot.getChild(childName));
        }
    }

    private void interpolatePoses(ModelPart from, ModelPart to, float progress) {
        to.xRot = lerp(from.xRot, to.xRot, progress);
        to.yRot = lerp(from.yRot, to.yRot, progress);
        to.zRot = lerp(from.zRot, to.zRot, progress);
        to.x = lerp(from.x, to.x, progress);
        to.y = lerp(from.y, to.y, progress);
        to.z = lerp(from.z, to.z, progress);
        interpolateChildPose(from, to, "left_spell_wing", progress);
        interpolateChildPose(from, to, "right_spell_wing", progress);
    }

    private void interpolateChildPose(ModelPart fromRoot, ModelPart toRoot, String childName, float progress) {
        if (fromRoot.hasChild(childName) && toRoot.hasChild(childName)) {
            interpolatePoses(fromRoot.getChild(childName), toRoot.getChild(childName), progress);
        }
    }

    private float lerp(float from, float to, float progress) {
        return from + (to - from) * progress;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
