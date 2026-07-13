package show.hadean.spells.client.renderer.player;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import show.hadean.spells.HadeanSpellsMod;
import vzling.hadean.breathe.init.ModMobEffect;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(modid = HadeanSpellsMod.MODID, value = Dist.CLIENT)
public class PlayerShadowRenderer {

    private static final Map<Integer, List<Shadow>> shadows = new ConcurrentHashMap<>();
    private static final Map<Integer, Float> lastShadowTick = new ConcurrentHashMap<>();
    private static final Map<Integer, Vec3> lastRecordedPos = new ConcurrentHashMap<>();
    private static final Set<Integer> activeShadowEntities = ConcurrentHashMap.newKeySet();

    public static void updateClientState(int id, boolean hasShadow) {
        if (hasShadow) activeShadowEntities.add(id);
        else activeShadowEntities.remove(id);
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        shadows.clear();
        lastShadowTick.clear();
        lastRecordedPos.clear();
        activeShadowEntities.clear();
    }

    @SubscribeEvent
    public static void onPlayerRenderPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer clientPlayer) || player.isSpectator() || player.isInvisible())
            return;

        int id = player.getId();
        float partial = event.getPartialTick();
        float now = player.level().getGameTime() + partial;

        boolean canRecord = activeShadowEntities.contains(id) || player.hasEffect(ModMobEffect.DEMONIZE.get());

        if (canRecord) {
            Vec3 currentPos = player.getPosition(partial);
            Vec3 lastPos = lastRecordedPos.get(id);
            boolean moved = lastPos == null || currentPos.distanceToSqr(lastPos) >= 0.0001D;

            if (moved) {
                float last = lastShadowTick.getOrDefault(id, 0f);
                if (shadows.get(id) == null || now - last >= 2f) {
                    shadows.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>())
                            .add(new Shadow(currentPos, player.yBodyRot, player.yHeadRot,
                                    player.getXRot(), now, player.walkAnimation.position(partial),
                                    player.walkAnimation.speed(partial), player.isCrouching()));
                    lastShadowTick.put(id, now);
                    lastRecordedPos.put(id, currentPos);
                }
            }
        }

        List<Shadow> list = shadows.get(id);
        if (list == null || list.isEmpty()) return;

        list.removeIf(s -> now - s.age >= 15f);
        if (list.isEmpty()) return;

        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        if (renderer instanceof PlayerRenderer pr) {
            PlayerModel<AbstractClientPlayer> model = pr.getModel();
            VertexConsumer buf = event.getMultiBufferSource()
                    .getBuffer(RenderType.entityTranslucent(pr.getTextureLocation(clientPlayer)));
            PoseStack stack = event.getPoseStack();
            Vec3 pos = player.getPosition(partial);

            for (Shadow s : list) {
                float pct = (now - s.age) / 10f;
                float a = Math.max(0f, 1f - pct);
                if (a <= 0f) continue;

                stack.pushPose();
                stack.translate(s.pos.x - pos.x, s.pos.y - pos.y, s.pos.z - pos.z);
                stack.mulPose(Axis.YP.rotationDegrees(180f - s.yBodyRot));
                stack.scale(1f - 0.2f * pct, 1f - 0.2f * pct, 1f - 0.2f * pct);
                stack.scale(-1f, -1f, 1f);
                stack.translate(0f, -1.501f, 0f);

                model.crouching = s.crouching;
                model.prepareMobModel(clientPlayer, s.limbSwing, s.limbSwingAmount, partial);
                model.setupAnim(clientPlayer, s.limbSwing, s.limbSwingAmount, 0, s.yHeadRot - s.yBodyRot, s.xRot);

                float c = 1f - 0.5f * pct;
                model.renderToBuffer(stack, buf, event.getPackedLight(), OverlayTexture.NO_OVERLAY, c, c, c, a);
                stack.popPose();
            }

            model.crouching = clientPlayer.isCrouching();
            model.setupAnim(clientPlayer, clientPlayer.walkAnimation.position(partial),
                    clientPlayer.walkAnimation.speed(partial), now,
                    clientPlayer.yHeadRot - clientPlayer.yBodyRot, clientPlayer.getXRot());
        }
    }

    private record Shadow(Vec3 pos, float yBodyRot, float yHeadRot, float xRot, float age,
                          float limbSwing, float limbSwingAmount, boolean crouching) {}
}