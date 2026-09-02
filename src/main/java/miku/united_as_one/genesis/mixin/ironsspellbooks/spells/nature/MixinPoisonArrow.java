package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.nature;

import io.redspace.ironsspellbooks.entity.spells.poison_arrow.PoisonArrow;
import io.redspace.ironsspellbooks.entity.spells.poison_cloud.PoisonCloud;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PoisonArrow.class, remap = false)
public class MixinPoisonArrow {
    @Inject(
            method = "createPoisonCloud",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void arrowEnhanced(Vec3 location, CallbackInfo ci, PoisonCloud poisonCloud) {
        PoisonArrow arrow = PoisonArrow.class.cast(this);
        if (arrow.getOwner() instanceof LivingEntity entity && GenesisCurios.has(entity, RunePlusItem::isNature)) {
            poisonCloud.setRadius(poisonCloud.getRadius() * 2.0F);
        }
    }
}
