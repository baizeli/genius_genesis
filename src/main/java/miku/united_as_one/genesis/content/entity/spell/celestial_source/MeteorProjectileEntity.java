package miku.united_as_one.genesis.content.entity.spell.celestial_source;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.bai_ze_li.genesis.api.entity.PositionTrailBuffer;
import miku.united_as_one.genesis.network.GenesisNetwork;
import miku.united_as_one.genesis.network.packet.MeteorGlowCubePacket;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.SpellRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class MeteorProjectileEntity extends Projectile {
    private static final int TRAIL_LENGTH = 36;
    private static final int MAX_LIFE_TICKS = 80;
    private static final double MAX_RANGE = 30.0D;
    private static final Vector3f BLASTWAVE_COLOR = new Vector3f(0.55F, 0.9F, 1.0F);
    private final PositionTrailBuffer trailPositions = new PositionTrailBuffer(TRAIL_LENGTH + 1);
    private float damage = 8.0F;
    private float starDamage = 4.0F;
    private float starRadius = 4.0F;
    private double traveledDistance;
    private Vec3 previousPosition = Vec3.ZERO;

    public MeteorProjectileEntity(EntityType<? extends MeteorProjectileEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public MeteorProjectileEntity(Level level, LivingEntity owner, float damage, float starDamage, float starRadius) {
        this(EntityRegistry.METEOR_PROJECTILE.get(), level);
        setOwner(owner);
        this.damage = damage;
        this.starDamage = starDamage;
        this.starRadius = starRadius;
        setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
    }

    @Override
    public void tick() {
        this.previousPosition = position();
        super.tick();

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            onHit(hitResult);
        }

        Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);

        if (!level().isClientSide) {
            this.traveledDistance += this.previousPosition.distanceTo(position());
            if (this.tickCount > MAX_LIFE_TICKS || this.traveledDistance >= MAX_RANGE) {
                discard();
            }
        } else {
            this.trailPositions.record(position(), 0.001D);
        }
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
        Entity hitEntity = result.getEntity();
        if (level().isClientSide) {
            explodeClient(result.getLocation());
            discard();
        } else if (hitEntity.canBeHitByProjectile()) {
            DamageSources.applyDamage(hitEntity, this.damage, spellDamageSource());
            explodeServer(result.getLocation());
            discard();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (level().isClientSide) {
            explodeClient(result.getLocation());
            discard();
        } else {
            explodeServer(result.getLocation());
            discard();
        }
    }

    private void explodeServer(Vec3 impact) {
        MagicManager.spawnParticles(level(), ParticleHelper.UNSTABLE_ENDER, impact.x, impact.y, impact.z,
                25, 0.0D, 0.0D, 0.0D, 0.18D, false);
        MagicManager.spawnParticles(level(), new BlastwaveParticleOptions(BLASTWAVE_COLOR, 3.6F),
                impact.x, impact.y + 0.05D, impact.z, 1, 0.0D, 0.0D, 0.0D, 0.0D, true);
        level().addFreshEntity(new MeteorShockwaveEntity(level(), impact.add(0.0D, 0.05D, 0.0D), Math.max(3.6F, this.starRadius * 1.15F) * 0.5F, this.getId()));
        spawnGlowCubeParticles(impact, 28);
        spawnFallingStars(impact);
    }

    private void explodeClient(Vec3 impact) {
    }

    private void spawnFallingStars(Vec3 impact) {
        for (int i = 0; i < 3; i++) {
            double spawnAngle = this.random.nextDouble() * Mth.TWO_PI;
            double spawnDistance = 3.0D + this.random.nextDouble() * 4.0D;
            double targetAngle = this.random.nextDouble() * Mth.TWO_PI;
            double targetDistance = this.random.nextDouble() * this.starRadius;
            Vec3 spawn = impact.add(Math.cos(spawnAngle) * spawnDistance,
                    15.0D + this.random.nextDouble() * 5.0D,
                    Math.sin(spawnAngle) * spawnDistance);
            Vec3 target = impact.add(Math.cos(targetAngle) * targetDistance,
                    0.0D,
                    Math.sin(targetAngle) * targetDistance);
            MeteorStarEntity star = new MeteorStarEntity(level(), getOwner(), this.starDamage, this.starRadius);
            star.setPos(spawn);
            star.setDeltaMovement(target.subtract(spawn).normalize().scale(1.45D));
            level().addFreshEntity(star);
        }
    }

    private void spawnGlowCubeParticles(Vec3 impact, int count) {
        if (!(level() instanceof ServerLevel serverLevel) || count <= 0) {
            return;
        }
        GenesisNetwork.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.NEAR.with(() -> new net.minecraftforge.network.PacketDistributor.TargetPoint(
                        impact.x, impact.y, impact.z, 96.0D, serverLevel.dimension())),
                new MeteorGlowCubePacket(impact.x, impact.y, impact.z, count, this.random.nextLong()));
    }

    public List<Vec3> getTrailPositions(float partialTicks) {
        Vec3 renderPosition = new Vec3(
                Mth.lerp(partialTicks, this.xOld, getX()),
                Mth.lerp(partialTicks, this.yOld, getY()),
                Mth.lerp(partialTicks, this.zOld, getZ())
        );
        return this.trailPositions.renderSnapshot(renderPosition, getDeltaMovement());
    }

    public SpellDamageSource spellDamageSource() {
        Entity owner = getOwner();
        return SpellDamageSource.source(this, owner == null ? this : owner, SpellRegistry.METEOR.get()).setIFrames(0);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putFloat("Damage", this.damage);
        tag.putFloat("StarDamage", this.starDamage);
        tag.putFloat("StarRadius", this.starRadius);
        tag.putDouble("TraveledDistance", this.traveledDistance);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.damage = tag.contains("Damage") ? tag.getFloat("Damage") : 8.0F;
        this.starDamage = tag.contains("StarDamage") ? tag.getFloat("StarDamage") : 4.0F;
        this.starRadius = tag.contains("StarRadius") ? tag.getFloat("StarRadius") : 4.0F;
        this.traveledDistance = tag.getDouble("TraveledDistance");
        setNoGravity(true);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
