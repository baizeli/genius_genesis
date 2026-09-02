package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.entity.spells.HealingAoe;
import io.redspace.ironsspellbooks.spells.holy.HealingCircleSpell;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = HealingCircleSpell.class, remap = false)
public class MixinHealingCircleSpell {
    @Inject(method = "getRadius", at = @At("RETURN"), cancellable = true)
    private void getRadius(int spellLevel, LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (GenesisCurios.has(caster, RunePlusItem::isHoly)) {
            cir.setReturnValue(cir.getReturnValue() * 3.0F);
        }
    }

    @Inject(
            method = "onCast",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData,
                        CallbackInfo ci, Vec3 spawn, int duration, float radius, HealingAoe aoeEntity) {
        if (GenesisCurios.has(entity, RunePlusItem::isHoly)) {
            aoeEntity.setReapplicationDelay(Mth.ceil((float) aoeEntity.getReapplicationDelay() / 2.0F));
        }
    }
}
