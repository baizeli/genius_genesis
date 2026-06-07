package miku.united_as_one.genesis.handlers.item.weapon.bow;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.EchoingStrikeEntity;
import io.redspace.ironsspellbooks.entity.spells.FireEruptionAoe;
import io.redspace.ironsspellbooks.entity.spells.fire_arrow.FireArrowProjectile;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceProjectile;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.united_as_one.genesis.Genesis;
import miku.bai_ze_li.genesis.api.curios.ModCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import miku.united_as_one.genesis.item.weapon.bow.ThunderLongBow;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BowProjectileEvents {
    private BowProjectileEvents() {
    }

    @SubscribeEvent
    public static void onFlameArrowImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof FireArrowProjectile fireArrow)) {
            return;
        }
        if (!fireArrow.getPersistentData().getBoolean("FlameBow_Arrow")) {
            return;
        }
        if (fireArrow.level().isClientSide || !(fireArrow.getOwner() instanceof LivingEntity owner)) {
            return;
        }

        Vec3 impactLocation = event.getRayTraceResult().getLocation();
        FireEruptionAoe aoe = new FireEruptionAoe(fireArrow.level(), 5);
        aoe.setOwner(owner);
        aoe.moveTo(impactLocation.x, impactLocation.y, impactLocation.z);
        fireArrow.level().addFreshEntity(aoe);
        CameraShakeManager.addCameraShake(new CameraShakeData(40, impactLocation, 12));
    }

    @SubscribeEvent
    public static void onLightningLanceImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof LightningLanceProjectile lanceProjectile)) {
            return;
        }
        if (lanceProjectile.level().isClientSide || !(lanceProjectile.getOwner() instanceof ServerPlayer player)) {
            return;
        }

        Entity target = impactTarget(event, lanceProjectile);
        if (player.getMainHandItem().getItem() instanceof ThunderLongBow
                || player.getOffhandItem().getItem() instanceof ThunderLongBow) {
            summonShockwave(lanceProjectile.level(), 4.0F, lanceProjectile.getDamage(), target, player, lanceProjectile);
        }
        if (ModCurios.hasCurios(player, RunePlusItem::isLightning)) {
            summonShockwave(lanceProjectile.level(), 4.0F, lanceProjectile.getDamage() * 0.75F, target, player, lanceProjectile);
        }
    }

    @SubscribeEvent
    public static void onMagicArrowImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof MagicArrowProjectile magicArrow)) {
            return;
        }
        if (magicArrow.level().isClientSide || !(magicArrow.getOwner() instanceof ServerPlayer player)) {
            return;
        }
        Entity target = impactTarget(event, magicArrow);
        if (!(target instanceof LivingEntity) || !ModCurios.hasCurios(player, RunePlusItem::isEnder)) {
            return;
        }

        EchoingStrikeEntity echo = new EchoingStrikeEntity(player.level(), player, magicArrow.getDamage(), 2.0F);
        echo.setTracking(target);
        echo.setPos(target.getBoundingBox().getCenter().subtract(0.0F, echo.getBbHeight() * 0.5F, 0.0F));
        echo.tickCount = 10;
        player.level().addFreshEntity(echo);
    }

    private static Entity impactTarget(ProjectileImpactEvent event, Projectile projectile) {
        HitResult hitResult = event.getRayTraceResult();
        if (hitResult instanceof EntityHitResult entityHit) {
            return entityHit.getEntity();
        }
        if (hitResult instanceof BlockHitResult blockHit) {
            BlockPos blockPos = blockHit.getBlockPos();
            projectile.setPos(blockPos.getX(), blockPos.getY() + 1.0D, blockPos.getZ());
        }
        return projectile;
    }

    private static void summonShockwave(Level level, float radius, float damage, Entity target, Entity owner, Projectile projectile) {
        Vector3f edge = new Vector3f(0.7F, 1.0F, 1.0F);
        Vector3f center = new Vector3f(1.0F, 1.0F, 1.0F);
        MagicManager.spawnParticles(level, new BlastwaveParticleOptions(edge, radius * 1.02F), target.getX(), target.getY() + 0.15D, target.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F, true);
        MagicManager.spawnParticles(level, new BlastwaveParticleOptions(edge, radius * 0.98F), target.getX(), target.getY() + 0.15D, target.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F, true);
        MagicManager.spawnParticles(level, new BlastwaveParticleOptions(center, radius), target.getX(), target.getY() + 0.165D, target.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F, true);
        MagicManager.spawnParticles(level, new BlastwaveParticleOptions(center, radius), target.getX(), target.getY() + 0.135D, target.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F, true);
        MagicManager.spawnParticles(level, ParticleHelper.ELECTRICITY, target.getX(), target.getY() + 1.0D, target.getZ(), 80, 0.25F, 0.25F, 0.25F, 0.7F + radius * 0.1F, false);

        Vec3 start = target.getBoundingBox().getCenter();
        LightningBolt dummyLightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        dummyLightningBolt.setDamage(0.0F);
        dummyLightningBolt.setVisualOnly(true);

        level.getEntities(target, target.getBoundingBox().inflate(radius, radius, radius),
                entity -> !DamageSources.isFriendlyFireBetween(entity, target) && Utils.hasLineOfSight(level, target, entity, true)
        ).forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity && canHit(entity) && livingEntity.distanceToSqr(target) < radius * radius) {
                Vec3 dest = livingEntity.getBoundingBox().getCenter();
                ((ServerLevel) level).sendParticles(new ZapParticleOption(dest), start.x, start.y, start.z, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                MagicManager.spawnParticles(level, ParticleHelper.ELECTRICITY, livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() / 2.0F, livingEntity.getZ(), 10, livingEntity.getBbWidth() / 3.0F, livingEntity.getBbHeight() / 3.0F, livingEntity.getBbWidth() / 3.0F, 0.1D, false);
                DamageSources.applyDamage(livingEntity, damage, SpellDamageSource.source(projectile, owner, SpellRegistry.SHOCKWAVE_SPELL.get()));
            }
        });

        for (int i = 0; i < 7; i++) {
            Vec3 dest = start.add(Utils.getRandomVec3(1.0F).multiply(4.0F, 2.5F, 4.0F).add(0.0F, 4.0F, 0.0F));
            ((ServerLevel) level).sendParticles(new ZapParticleOption(dest), start.x, start.y, start.z, 1, 0.0F, 0.0F, 0.0F, 0.0F);
        }
    }

    private static boolean canHit(Entity target) {
        return target.isAlive() && target.isPickable() && !target.isSpectator();
    }
}
