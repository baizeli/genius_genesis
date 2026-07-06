package miku.united_as_one.genesis.mixin.client;

import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import miku.united_as_one.genesis.combat.meleeproj.MeleeSwingArmHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void genesis$aimMeleeSwingArm(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                          float ageInTicks, float netHeadYaw, float headPitch,
                                          CallbackInfo ci) {
        if (!(entity instanceof Player player)) {
            return;
        }
        MeleeProjBase proj = MeleeSwingArmHelper.findNewestHeld(player);
        if (proj == null) {
            return;
        }
        MeleeSwingArmHelper.aimMainArm((HumanoidModel<?>) (Object) this, player, proj,
                Minecraft.getInstance().getFrameTime());
    }
}
