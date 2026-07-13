package miku.united_as_one.genesis.client.render.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PlayerShadowRenderer {
    private static final Map<Integer, List<Shadow>> SHADOWS = new ConcurrentHashMap<>();
    private static final Map<Integer, Float> LAST_SHADOW_TICK = new ConcurrentHashMap<>();
    private static final Map<Integer, Vec3> LAST_RECORDED_POS = new ConcurrentHashMap<>();
    private static final Map<Integer, Float> ACTIVE_SHADOW_ENTITIES = new ConcurrentHashMap<>();
    private static final Set<Integer> CLEANUP = ConcurrentHashMap.newKeySet();

    private PlayerShadowRenderer() {
    }

    public static void updateClientState(int id, int durationTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || durationTicks <= 0) {
            ACTIVE_SHADOW_ENTITIES.remove(id);
            return;
        }
        ACTIVE_SHADOW_ENTITIES.put(id, minecraft.level.getGameTime() + (float) durationTicks);
    }

    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        SHADOWS.clear();
        LAST_SHADOW_TICK.clear();
        LAST_RECORDED_POS.clear();
        ACTIVE_SHADOW_ENTITIES.clear();
    }

    public static void onPlayerRenderPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer clientPlayer) || player.isSpectator() || player.isInvisible()) {
            return;
        }

        int id = player.getId();
        float partialTick = event.getPartialTick();
        float now = player.level().getGameTime() + partialTick;
        boolean canRecord = isActive(id, now);

        if (canRecord) {
            recordShadow(player, id, partialTick, now);
        }

        renderShadows(event, clientPlayer, id, partialTick, now);
    }

    private static boolean isActive(int id, float now) {
        Float expiry = ACTIVE_SHADOW_ENTITIES.get(id);
        if (expiry == null) {
            return false;
        }
        if (now <= expiry) {
            return true;
        }
        ACTIVE_SHADOW_ENTITIES.remove(id);
        CLEANUP.add(id);
        return false;
    }

    private static void recordShadow(Player player, int id, float partialTick, float now) {
        Vec3 currentPos = player.getPosition(partialTick);
        Vec3 lastPos = LAST_RECORDED_POS.get(id);
        boolean moved = lastPos == null || currentPos.distanceToSqr(lastPos) >= 0.0001D;
        if (!moved) {
            return;
        }

        float last = LAST_SHADOW_TICK.getOrDefault(id, 0.0F);
        if (SHADOWS.get(id) == null || now - last >= 1.0F) {
            SHADOWS.computeIfAbsent(id, key -> new CopyOnWriteArrayList<>())
                    .add(new Shadow(currentPos, player.yBodyRot, player.yHeadRot, player.getXRot(), now,
                            player.walkAnimation.position(partialTick),
                            player.walkAnimation.speed(partialTick),
                            player.isCrouching()));
            LAST_SHADOW_TICK.put(id, now);
            LAST_RECORDED_POS.put(id, currentPos);
        }
    }

    private static void renderShadows(RenderPlayerEvent.Post event, AbstractClientPlayer clientPlayer,
                                      int id, float partialTick, float now) {
        List<Shadow> list = SHADOWS.get(id);
        if (list == null || list.isEmpty()) {
            cleanupIfExpired(id);
            return;
        }

        list.removeIf(shadow -> now - shadow.age >= 15.0F);
        if (list.isEmpty()) {
            cleanupIfExpired(id);
            return;
        }

        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(clientPlayer);
        if (!(renderer instanceof PlayerRenderer playerRenderer)) {
            return;
        }

        PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();
        VertexConsumer buffer = event.getMultiBufferSource()
                .getBuffer(RenderType.entityTranslucent(playerRenderer.getTextureLocation(clientPlayer)));
        PoseStack poseStack = event.getPoseStack();
        Vec3 pos = clientPlayer.getPosition(partialTick);

        for (Shadow shadow : list) {
            float progress = (now - shadow.age) / 10.0F;
            float alpha = Math.max(0.0F, 1.0F - progress);
            if (alpha <= 0.0F) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(shadow.pos.x - pos.x, shadow.pos.y - pos.y, shadow.pos.z - pos.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - shadow.yBodyRot));
            float scale = 1.0F - 0.2F * progress;
            poseStack.scale(scale, scale, scale);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, -1.501F, 0.0F);

            model.crouching = shadow.crouching;
            model.prepareMobModel(clientPlayer, shadow.limbSwing, shadow.limbSwingAmount, partialTick);
            model.setupAnim(clientPlayer, shadow.limbSwing, shadow.limbSwingAmount, 0.0F,
                    shadow.yHeadRot - shadow.yBodyRot, shadow.xRot);

            float color = 1.0F - 0.5F * progress;
            model.renderToBuffer(poseStack, buffer, event.getPackedLight(), OverlayTexture.NO_OVERLAY,
                    color, color, color, alpha * 0.65F);
            poseStack.popPose();
        }

        model.crouching = clientPlayer.isCrouching();
        model.setupAnim(clientPlayer, clientPlayer.walkAnimation.position(partialTick),
                clientPlayer.walkAnimation.speed(partialTick), now,
                clientPlayer.yHeadRot - clientPlayer.yBodyRot, clientPlayer.getXRot());
    }

    private static void cleanupIfExpired(int id) {
        if (!CLEANUP.remove(id)) {
            return;
        }
        SHADOWS.remove(id);
        LAST_SHADOW_TICK.remove(id);
        LAST_RECORDED_POS.remove(id);
    }

    private record Shadow(Vec3 pos, float yBodyRot, float yHeadRot, float xRot, float age,
                          float limbSwing, float limbSwingAmount, boolean crouching) {
    }
}
