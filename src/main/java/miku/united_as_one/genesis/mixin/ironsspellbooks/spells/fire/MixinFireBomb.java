package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireBomb;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireField;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.content.item.curios.RunePlusItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = FireBomb.class, remap = false)
public abstract class MixinFireBomb extends AbstractMagicProjectile {
    public MixinFireBomb(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "createFireField",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void createFireField(Vec3 location, CallbackInfo ci, FireField fire) {
        Entity owner = getOwner();
        if (owner instanceof LivingEntity entity && GenesisCurios.has(entity, RunePlusItem::isFire)) {
            fire.setDuration(fire.getDuration() * 2);
        }
    }
}
