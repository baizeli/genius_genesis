package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireField;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import io.redspace.ironsspellbooks.spells.fire.ScorchSpell;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
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

@Mixin(value = ScorchSpell.class, remap = false)
public class MixinScorchSpell {
    @Inject(method = "getDamage", at = @At("RETURN"), cancellable = true)
    private void getDamage(int spellLevel, LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (GenesisCurios.has(caster, RunePlusItem::isFire)) {
            cir.setReturnValue(cir.getReturnValue() * 1.5F);
        }
    }

    @Inject(method = "getRadius", at = @At("RETURN"), cancellable = true)
    private void getRadius(LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (GenesisCurios.has(caster, RunePlusItem::isFire)) {
            cir.setReturnValue(cir.getReturnValue() + 2.0F);
        }
    }

    @Inject(
            method = "onCast",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData,
                        CallbackInfo ci, TargetAreaCastData castData, Vec3 targetArea, float radius, float radiusSqr,
                        float damage, SpellDamageSource source, FireField fire) {
        if (GenesisCurios.has(entity, RunePlusItem::isFire)) {
            fire.setDuration(fire.getDuration() * 2);
        }
    }
}
