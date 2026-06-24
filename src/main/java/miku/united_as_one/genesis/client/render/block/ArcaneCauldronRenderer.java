package miku.united_as_one.genesis.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.gui.overlays.ScreenTooltipOverlay;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.workbench.arcane_cauldron.ArcaneCauldronBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ArcaneCauldronRenderer implements BlockEntityRenderer<ArcaneCauldronBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ArcaneCauldronRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ArcaneCauldronBlockEntity cauldron, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = cauldron.getLevel();
        int fluidAmount = cauldron.getFluidAmount();
        float fluidOffset = Mth.lerp(fluidAmount / 1000.0F, 0.25F, 0.9F);

        if (level != null && fluidAmount > 0) {
            renderFluid(cauldron, poseStack, bufferSource, packedLight, fluidOffset);
        }

        for (int i = 0; i < cauldron.inputItems.size(); i++) {
            ItemStack itemStack = cauldron.inputItems.get(i);
            if (!itemStack.isEmpty()) {
                float time = level != null && fluidAmount > 0 ? level.getGameTime() + partialTick : 15.0F;
                Vec2 floatOffset = getFloatingItemOffset(time, i * 587);
                float yRot = (time + i * 213) / (i + 1) * 1.5F;
                renderItem(
                        itemStack,
                        new Vec3(floatOffset.x, fluidOffset + i * 0.01F, floatOffset.y),
                        yRot,
                        cauldron,
                        poseStack,
                        bufferSource,
                        packedOverlay
                );
            }
        }

        renderLookTooltip(cauldron);
    }

    private Vec2 getFloatingItemOffset(float time, int offset) {
        float xSpeed = offset % 2 == 0 ? 0.0075F : 0.025F * (1 + (offset % 88) * 0.001F);
        float ySpeed = offset % 2 == 0 ? 0.025F : 0.0075F * (1 + (offset % 88) * 0.001F);
        float x = (time + offset) * xSpeed;
        x = (Math.abs((x % 2) - 1) + 1) / 2;
        float y = (time + offset + 4356) * ySpeed;
        y = (Math.abs((y % 2) - 1) + 1) / 2;
        return new Vec2(Mth.lerp(x, -0.2F, 0.75F), Mth.lerp(y, -0.2F, 0.75F));
    }

    private void renderFluid(ArcaneCauldronBlockEntity cauldron, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float fluidOffset) {
        Matrix4f pose = poseStack.last().pose();
        float totalFluid = cauldron.getFluidAmount();
        float runningFluid = totalFluid;
        float yOffset = 0.0F;
        float padding = 1.0F / 16.0F;

        for (FluidStack fluid : cauldron.fluidInventory.fluids()) {
            int skylight = packedLight >> 4 & 15;
            int luminosity = Math.max(skylight, fluid.getFluid().getFluidType().getLightLevel(fluid));
            int fluidLight = packedLight & 0xF00000 | luminosity << 4;
            IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid.getFluid());
            Function<ResourceLocation, TextureAtlasSprite> spriteAtlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            TextureAtlasSprite texture = spriteAtlas.apply(clientFluid.getStillTexture(fluid.getFluid().defaultFluidState(), cauldron.getLevel(), cauldron.getBlockPos()));
            VertexConsumer consumer = texture.wrap(bufferSource.getBuffer(RenderType.translucent()));
            Vector3f rgb = colorFromLong(clientFluid.getTintColor(fluid) & clientFluid.getTintColor(fluid.getFluid().defaultFluidState(), cauldron.getLevel(), cauldron.getBlockPos()));
            float opacity = runningFluid / totalFluid;
            runningFluid -= fluid.getAmount();

            consumer.vertex(pose, 1 - padding, fluidOffset + yOffset, padding).color(rgb.x(), rgb.y(), rgb.z(), opacity).uv(1 - padding, padding).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fluidLight).normal(0, 1, 0).endVertex();
            consumer.vertex(pose, padding, fluidOffset + yOffset, padding).color(rgb.x(), rgb.y(), rgb.z(), opacity).uv(padding, padding).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fluidLight).normal(0, 1, 0).endVertex();
            consumer.vertex(pose, padding, fluidOffset + yOffset, 1 - padding).color(rgb.x(), rgb.y(), rgb.z(), opacity).uv(padding, 1 - padding).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fluidLight).normal(0, 1, 0).endVertex();
            consumer.vertex(pose, 1 - padding, fluidOffset + yOffset, 1 - padding).color(rgb.x(), rgb.y(), rgb.z(), opacity).uv(1 - padding, 1 - padding).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fluidLight).normal(0, 1, 0).endVertex();
            yOffset += 0.001F;
        }
    }

    private Vector3f colorFromLong(long color) {
        return new Vector3f(
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F
        );
    }

    private void renderItem(ItemStack itemStack, Vec3 offset, float yRot, ArcaneCauldronBlockEntity cauldron, PoseStack poseStack, MultiBufferSource bufferSource, int packedOverlay) {
        Level level = cauldron.getLevel();
        if (level == null) {
            return;
        }

        poseStack.pushPose();
        int renderId = (int) cauldron.getBlockPos().asLong();
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(0.4F, 0.4F, 0.4F);
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, LevelRenderer.getLightColor(level, cauldron.getBlockPos()), packedOverlay, poseStack, bufferSource, level, renderId);
        poseStack.popPose();
    }

    private void renderLookTooltip(ArcaneCauldronBlockEntity cauldron) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (Math.abs(player.getX() - cauldron.getBlockPos().getX()) >= 5
                || Math.abs(player.getY() - cauldron.getBlockPos().getY()) >= 5
                || Math.abs(player.getZ() - cauldron.getBlockPos().getZ()) >= 5) {
            return;
        }

        if (!player.isCrouching()
                || !(Minecraft.getInstance().hitResult instanceof BlockHitResult hit)
                || !hit.getBlockPos().equals(cauldron.getBlockPos())) {
            return;
        }

        List<Component> text = new ArrayList<>();
        text.add(Component.translatable("block." + Genesis.MOD_ID + ".arcane_cauldron").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.WHITE));
        List<FluidStack> fluids = cauldron.fluidInventory.fluids();
        if (fluids.isEmpty()) {
            text.add(Component.translatable("ui.irons_spellbooks.empty").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else {
            List<ObjectIntImmutablePair<MutableComponent>> fluidInfo = new ArrayList<>();
            for (int i = fluids.size() - 1; i >= 0; i--) {
                FluidStack fluid = fluids.get(i);
                fluidInfo.add(new ObjectIntImmutablePair<>(fluid.getFluid().getFluidType().getDescription(fluid).copy().withStyle(ChatFormatting.DARK_AQUA), fluid.getAmount()));
            }

            for (ObjectIntImmutablePair<MutableComponent> info : fluidInfo) {
                text.add(Component.literal("  ").append(info.left()).append(": ").append(Component.literal(info.rightInt() + "mb").withStyle(ChatFormatting.GOLD)));
            }
        }
        ScreenTooltipOverlay.renderTooltip(text, (screenWidth, screenHeight, mouseX, mouseY, tooltipWidth, tooltipHeight) -> new Vector2i(screenWidth / 2 + 30, screenHeight / 2 - tooltipHeight / 2));
    }
}
