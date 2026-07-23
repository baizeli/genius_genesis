package miku.united_as_one.genesis.entity.spell.celestial_source;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.bai_ze_li.genesis.api.entity.PositionTrailBuffer;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.SpellRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class MeteorStarEntity extends Projectile {
    private static final int TRAIL_LENGTH = 24;
    private static final int MAX_LIFE_TICKS = 80;
    private static final Vector3f IMPACT_COLOR = new Vector3f(0.55F, 0.9F, 1.0F);

    private final PositionTrailBuffer trailPositions = new PositionTrailBuffer(TRAIL_LENGTH + 1);
    private float damage = 4.0F;
    private float radius = 4.0F;

    public MeteorStarEntity(EntityType<? extends MeteorStarEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public MeteorStarEntity(Level level, Entity owner, float damage, float radius) {
        this(EntityRegistry.METEOR_STAR.get(), level);
        setOwner(owner);
        this.damage = damage;
        this.radius = radius;
    }

    @Override
    public void tick() {
        super.tick();

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            onHit(hitResult);
        }

        Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);

        if (!level().isClientSide && this.tickCount > MAX_LIFE_TICKS) {
            impact(position());
            discard();
        } else if (level().isClientSide) {
            this.trailPositions.record(position(), 0.001D);
        }
    }

    public java.util.List<Vec3> getTrailPositions(float partialTicks) {
        Vec3 renderPosition = new Vec3(
                Mth.lerp(partialTicks, this.xOld, getX()),
                Mth.lerp(partialTicks, this.yOld, getY()),
                Mth.lerp(partialTicks, this.zOld, getZ())
        );
        return this.trailPositions.renderSnapshot(renderPosition, getDeltaMovement());
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        Entity owner = getOwner();
        return super.canHitEntity(entity)
                && entity != owner
                && !(owner != null && entity.isAlliedTo(owner));
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (!level().isClientSide) {
            impact(result.getLocation());
            discard();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (!level().isClientSide) {
            impact(result.getLocation());
            discard();
        }
    }

    private void impact(Vec3 center) {
        MagicManager.spawnParticles(level(), ParticleHelper.UNSTABLE_ENDER, center.x, center.y, center.z,
                18, 0.0D, 0.0D, 0.0D, 0.13D, false);
        MagicManager.spawnParticles(level(), new BlastwaveParticleOptions(IMPACT_COLOR, this.radius * 0.9F),
                center.x, center.y + 0.05D, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D, true);
        level().addFreshEntity(new MeteorShockwaveEntity(level(), center.add(0.0D, 0.05D, 0.0D), this.radius * 0.45F, this.getId()));

        if (!(level() instanceof ServerLevel)) {
            return;
        }

        double radiusSqr = this.radius * this.radius;
        AABB area = new AABB(
                center.x - this.radius,
                center.y - this.radius,
                center.z - this.radius,
                center.x + this.radius,
                center.y + this.radius,
                center.z + this.radius
        );
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            if (target == getOwner() || target.distanceToSqr(center) > radiusSqr) {
                continue;
            }
            if (getOwner() != null && target.isAlliedTo(getOwner())) {
                continue;
            }
            DamageSources.applyDamage(target, this.damage, spellDamageSource());
        }
    }

    private SpellDamageSource spellDamageSource() {
        Entity owner = getOwner();
        return SpellDamageSource.source(this, owner == null ? this : owner, SpellRegistry.METEOR.get()).setIFrames(0);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putFloat("Damage", this.damage);
        tag.putFloat("Radius", this.radius);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.damage = tag.contains("Damage") ? tag.getFloat("Damage") : 4.0F;
        this.radius = tag.contains("Radius") ? tag.getFloat("Radius") : 4.0F;
        setNoGravity(true);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
