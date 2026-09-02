package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockProjectile;
import io.redspace.ironsspellbooks.spells.ice.IceBlockSpell;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.content.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = IceBlockSpell.class, remap = false)
public class MixinIceBlockSpell {
    @Inject(
            method = "onCast",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData,
                        CallbackInfo ci, Vec3 spawn, LivingEntity target, int spawnHeight, IceBlockProjectile iceBlock) {
        if (GenesisCurios.has(entity, RunePlusItem::isIce)) {
            iceBlock.setAirTime(0);
        }
    }
}
