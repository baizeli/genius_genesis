package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import io.redspace.ironsspellbooks.spells.ender.StarfallSpell;
import miku.bai_ze_li.genesis.api.curios.ModCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = StarfallSpell.class, remap = false)
public class MixinStarfallSpell {
    @Inject(
            method = "shootComet",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void cometEnhanced(Level world, int spellLevel, LivingEntity entity, Vec3 spawn, Vec3 trajectory, CallbackInfo ci, Comet fireball) {
        if (ModCurios.hasCurios(entity, RunePlusItem::isEnder)) {
            fireball.setExplosionRadius(fireball.getExplosionRadius() * 4.0F);
        }
    }

    @Inject(method = "getDamage", at = @At("RETURN"), cancellable = true)
    private void getDamage(int spellLevel, LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (ModCurios.hasCurios(caster, RunePlusItem::isEnder)) {
            cir.setReturnValue(cir.getReturnValue() * 2.0F);
        }
    }
}
