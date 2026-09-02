package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.nature;

import io.redspace.ironsspellbooks.spells.nature.AcidOrbSpell;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AcidOrbSpell.class, remap = false)
public class MixinAcidOrbSpell {
    @Inject(method = "getRadius", at = @At("RETURN"), cancellable = true)
    private void getRadius(int spellLevel, LivingEntity caster, CallbackInfoReturnable<Float> cir) {
        if (GenesisCurios.has(caster, RunePlusItem::isNature)) {
            cir.setReturnValue(cir.getReturnValue() * 1.5F);
        }
    }
}
