package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.nature;

import io.redspace.ironsspellbooks.entity.spells.poison_cloud.PoisonCloud;
import io.redspace.ironsspellbooks.entity.spells.poison_cloud.PoisonSplash;
import miku.bai_ze_li.genesis.api.curios.ModCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PoisonSplash.class, remap = false)
public class MixinPoisonSplash {
    @Inject(
            method = "createPoisonCloud",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void createPoisonCloud(CallbackInfo ci, PoisonCloud poisonCloud) {
        PoisonSplash splash = PoisonSplash.class.cast(this);
        if (splash.getOwner() instanceof LivingEntity entity && ModCurios.hasCurios(entity, RunePlusItem::isNature)) {
            poisonCloud.setRadius(poisonCloud.getRadius() * 2.0F);
            poisonCloud.setDamage(poisonCloud.getDamage() * 2.0F);
        }
    }
}
