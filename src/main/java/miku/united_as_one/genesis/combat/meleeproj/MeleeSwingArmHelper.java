/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package miku.united_as_one.genesis.combat.meleeproj;

import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class MeleeSwingArmHelper {
    private MeleeSwingArmHelper() {
    }

    public static MeleeProjBase findNewestHeld(Player player) {
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            return null;
        }
        UUID id = player.getUUID();
        MeleeProjBase best = null;
        for (Entity e : level.entitiesForRendering()) {
            UUID owner;
            MeleeProjBase mp;
            if (!(e instanceof MeleeProjBase) || !(mp = (MeleeProjBase)e).isAlive() || !mp.isHeldByOwner() || (owner = mp.getOwnerUUID()) == null || !owner.equals(id) || best != null && mp.getId() <= best.getId()) continue;
            best = mp;
        }
        return best;
    }

    public static boolean isUsingMelee(Player player) {
        return MeleeSwingArmHelper.findNewestHeld(player) != null;
    }

    public static void aimMainArm(HumanoidModel<?> model, Player player, MeleeProjBase proj, float partialTicks) {
        Vec3 dir = proj.getTipDirWorld(partialTicks);
        double dx = dir.x;
        double dy = dir.y;
        double dz = dir.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float targetYawDeg = (float)(Mth.atan2((double)(-dx), (double)dz) * 57.29577951308232);
        float targetPitchDeg = (float)(-(Mth.atan2((double)dy, (double)horiz) * 57.29577951308232));
        float bodyYaw = Mth.rotLerp((float)partialTicks, (float)player.yBodyRotO, (float)player.yBodyRot);
        float relYaw = (float)Math.toRadians(Mth.wrapDegrees((float)(targetYawDeg - bodyYaw)));
        float pitch = (float)Math.toRadians(targetPitchDeg);
        boolean left = player.getMainArm() == HumanoidArm.LEFT;
        ModelPart arm = left ? model.leftArm : model.rightArm;
        arm.xRot = pitch - 1.5707964f;
        arm.yRot = left ? -relYaw : relYaw;
        arm.zRot = 0.0f;
    }

    public static boolean hasOwnedProj(Player player) {
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            return false;
        }
        UUID id = player.getUUID();
        for (Entity e : level.entitiesForRendering()) {
            UUID owner;
            MeleeProjBase mp;
            if (!(e instanceof MeleeProjBase) || !(mp = (MeleeProjBase)e).isAlive() || (owner = mp.getOwnerUUID()) == null || !owner.equals(id)) continue;
            return true;
        }
        return false;
    }
}


