package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.spells.ice.IcicleSpell;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.content.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IcicleSpell.class, remap = false)
public class MixinIcicleSpell {
    @Inject(method = "getDamage", at = @At("RETURN"), cancellable = true)
    private void getDamage(int spellLevel, LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        if (GenesisCurios.has(entity, RunePlusItem::isIce)) {
            cir.setReturnValue(cir.getReturnValue() + entity.getArmorValue() * 0.25F);
        }
    }
}
