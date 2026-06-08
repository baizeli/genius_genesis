package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.spells.fire.MagmaBombSpell;
import miku.bai_ze_li.genesis.api.curios.ModCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MagmaBombSpell.class, remap = false)
public class MixinMagmaBombSpell {
    @Inject(method = "getAoeDamage", at = @At("RETURN"), cancellable = true)
    private void getDamage(int spellLevel, LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (ModCurios.hasCurios(caster, RunePlusItem::isFire)) {
            cir.setReturnValue(cir.getReturnValue() * 1.5F);
        }
    }

    @Inject(method = "getRadius", at = @At("RETURN"), cancellable = true)
    private void getRadius(int spellLevel, LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (ModCurios.hasCurios(caster, RunePlusItem::isFire)) {
            cir.setReturnValue(cir.getReturnValue() + 2.0F);
        }
    }
}
