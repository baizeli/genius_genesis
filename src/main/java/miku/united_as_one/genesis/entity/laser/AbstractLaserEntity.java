package miku.united_as_one.genesis.entity.laser;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractLaserEntity extends Entity {
    public LivingEntity caster;

    public float customDamage = 3;
    public double endPosX;
    public double endPosY;
    public double endPosZ;

    public double collidePosX;
    public double collidePosY;
    public double collidePosZ;

    public double prevCollidePosX;
    public double prevCollidePosY;
    public double prevCollidePosZ;

    public float renderYaw;
    public float renderPitch;

    public int appearTimer = 0;
    private final int APPEAR_DURATION = 3;

    public boolean on = true;
    public Direction blockSide = null;

    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_PLAYER = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CASTER = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> LASER_LENGTH = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LASER_RADIUS = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RENDER_START = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RENDER_END = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> OFFSET_X = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> OFFSET_Y = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> OFFSET_Z = SynchedEntityData.defineId(AbstractLaserEntity.class, EntityDataSerializers.FLOAT);

    public float prevYaw;
    public float prevPitch;

    @OnlyIn(Dist.CLIENT)
    private Vec3[] attractorPos;

    public AbstractLaserEntity(EntityType<? extends AbstractLaserEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
        if (world.isClientSide) this.attractorPos = new Vec3[]{new Vec3(0d, 0d, 0d)};
    }

    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public boolean hurt(@NotNull DamageSource p_19946_, float p_19947_) {
        return false;
    }

    public void tick() {
        super.tick();
        this.prevCollidePosX = this.collidePosX;
        this.prevCollidePosY = this.collidePosY;
        this.prevCollidePosZ = this.collidePosZ;
        this.xo = getX();
        this.yo = getY();
        this.zo = getZ();

        if (this.tickCount == 1 && this.level().isClientSide) this.caster = (LivingEntity)this.level().getEntity(getCasterID());
        if (this.caster != null) {
            if (!this.level().isClientSide) {
                setYaw(this.caster.yHeadRot);
                setPitch(-this.caster.getXRot());

                if (getHasPlayer()) {
                    updateWithPlayer();
                } else if (this.caster instanceof Salmon) {
                    updateWithBarako();
                }
            } else {
                if (getHasPlayer()) {
                    updateWithPlayer();
                }
            }
            this.renderYaw = getYaw();
            this.renderPitch = getPitch();

            this.setDeltaMovement(caster.getDeltaMovement().x, caster.getDeltaMovement().y, caster.getDeltaMovement().z);
        }

        this.prevYaw = this.renderYaw;
        this.prevPitch = this.renderPitch;

        if (!this.on && this.appearTimer == 0) discard();
        if (this.on && this.tickCount > 20) {
            if (this.appearTimer < this.APPEAR_DURATION) this.appearTimer++;
        } else {
            if (this.appearTimer > 0) this.appearTimer--;
        }

        if (this.caster != null && !this.caster.isAlive()) discard();

        onClientTick();

        if (this.tickCount > 20) {
            double radius = (this.caster instanceof Salmon) ? 30d : getLaserLength();
            if (this.level().isClientSide()) {
                this.endPosX = getX() + radius * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
                this.endPosZ = getZ() + radius * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
                this.endPosY = getY() + radius * Math.sin(this.renderPitch);
                raytraceEntities(this.level(), new Vec3(getX(), getY(), getZ()),
                        new Vec3(this.endPosX, this.endPosY, this.endPosZ), true
                );
            } else {
                this.endPosX = getX() + radius * Math.cos(getYaw()) * Math.cos(getPitch());
                this.endPosZ = getZ() + radius * Math.sin(getYaw()) * Math.cos(getPitch());
                this.endPosY = getY() + radius * Math.sin(getPitch());
            }
            if (this.blockSide != null && this.level().isClientSide()) spawnCollisionParticles(); spawnBeamParticles();
            if (!this.level().isClientSide && (this.tickCount - 20) % 10 == 0) {
                dealDamageToEntities();
            }
        }

        if (this.tickCount - 20 > getDuration()) this.on = false;
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientTick() {
        if (this.level().isClientSide && this.tickCount <= 10 && this.caster != null) {
            int particleCount = 8;
            while (--particleCount != 0) {
                double rootX = this.caster.getX();
                double rootY = this.caster.getY() + (this.caster.getBbHeight() / 2f) + 0.3d;
                double rootZ = this.caster.getZ();
                this.attractorPos[0] = new Vec3(rootX, rootY, rootZ);
            }
        }
    }

    protected void dealDamageToEntities() {
        for (LivingEntity target : raytraceEntities(this.level(), new Vec3(getX(), getY(), getZ()),
                new Vec3(this.endPosX, this.endPosY, this.endPosZ), true).entities) {
            target.invulnerableTime = 0;
            target.hurt(createDamageSource(), this.customDamage);
        }
    }

    protected abstract DamageSource createDamageSource();

    protected abstract void spawnCollisionParticles();
    @OnlyIn(Dist.CLIENT) protected abstract void spawnBeamParticles();

    protected void defineSynchedData() {
        getEntityData().define(YAW, 0f);
        getEntityData().define(PITCH, 0f);
        getEntityData().define(DURATION, 0);
        getEntityData().define(HAS_PLAYER, Boolean.FALSE);
        getEntityData().define(CASTER, -1);
        getEntityData().define(LASER_LENGTH, 20f);
        getEntityData().define(LASER_RADIUS, 1f);
        getEntityData().define(RENDER_START, false);
        getEntityData().define(RENDER_END, true);
        getEntityData().define(OFFSET_X, 0f);
        getEntityData().define(OFFSET_Y, 0f);
        getEntityData().define(OFFSET_Z, 0f);
    }

    public float getYaw() {
        return getEntityData().get(YAW);
    }

    public void setYaw(float yaw) {
        getEntityData().set(YAW, yaw);
    }

    public float getPitch() {
        return getEntityData().get(PITCH);
    }

    public void setPitch(float pitch) {
        getEntityData().set(PITCH, pitch);
    }

    public int getDuration() {
        return getEntityData().get(DURATION);
    }

    public void setDuration(int duration) {
        getEntityData().set(DURATION, duration);
    }

    public boolean getHasPlayer() {
        return getEntityData().get(HAS_PLAYER);
    }

    public int getCasterID() {
        return getEntityData().get(CASTER);
    }

    public void setFollowPlayer(boolean follow, float offsetX, float offsetY, float offsetZ) {
        getEntityData().set(HAS_PLAYER, follow);
        getEntityData().set(OFFSET_X, offsetX);
        getEntityData().set(OFFSET_Y, offsetY);
        getEntityData().set(OFFSET_Z, offsetZ);
    }

    public void setFollowPlayer(boolean follow) {
        setFollowPlayer(follow, 0f, 0f, 0f);
    }

    public void setCaster(LivingEntity caster) {
        if (caster != null) {
            this.caster = caster;
            getEntityData().set(CASTER, caster.getId());
        }
    }

    public void setCustomDamage(float damage) {
        this.customDamage = damage;
    }

    public void setLaserLength(float length) {
        getEntityData().set(LASER_LENGTH, length);
    }

    public float getLaserLength() {
        return getEntityData().get(LASER_LENGTH);
    }

    public float getLaserRadius() {
        return getEntityData().get(LASER_RADIUS);
    }

    public void setRenderStart(boolean render) {
        getEntityData().set(RENDER_START, render);
    }

    public boolean getRenderStart() {
        return getEntityData().get(RENDER_START);
    }

    public void setRenderEnd(boolean render) {
        getEntityData().set(RENDER_END, render);
    }

    public boolean getRenderEnd() {
        return getEntityData().get(RENDER_END);
    }

    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {}

    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {}

    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public SolarbeamHitResult raytraceEntities(Level world, Vec3 from, Vec3 to, boolean ignoreBlockWithoutBoundingBox) {
        SolarbeamHitResult result = new SolarbeamHitResult();
        result.setBlockHit(world.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)));
        if (result.blockHit != null) {
            Vec3 hitVec = result.blockHit.getLocation();
            this.collidePosX = hitVec.x;
            this.collidePosY = hitVec.y;
            this.collidePosZ = hitVec.z;
            this.blockSide = result.blockHit.getDirection();
        } else {
            this.collidePosX = this.endPosX;
            this.collidePosY = this.endPosY;
            this.collidePosZ = this.endPosZ;
            this.blockSide = null;
        }
        for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, (new AABB(
                Math.min(getX(), this.collidePosX), Math.min(getY(), this.collidePosY), Math.min(getZ(), this.collidePosZ),
                Math.max(getX(), this.collidePosX), Math.max(getY(), this.collidePosY), Math.max(getZ(), this.collidePosZ)
        )).inflate(1d, 1d, 1d))) {
            if (entity == this.caster) continue;
            float pad = entity.getPickRadius() + 0.5f;
            AABB aabb = entity.getBoundingBox().inflate(pad, pad, pad);
            Optional<Vec3> hit = aabb.clip(from, to);
            if (aabb.contains(from)) {
                result.addEntityHit(entity);
                continue;
            }
            if (hit.isPresent()) result.addEntityHit(entity);
        }
        return result;
    }

    public void push(@NotNull Entity entityIn) {}

    public boolean isPickable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024d;
    }

    private void updateWithPlayer() {
        setYaw((float)((this.caster.yHeadRot + 90d) * Math.PI / 180d));
        setPitch((float)(-this.caster.getXRot() * Math.PI / 180d));
        Vec3 vecOffset = this.caster.getLookAngle().normalize().scale(1d);
        setPos(this.caster.getX() + vecOffset.x() + getEntityData().get(OFFSET_X),
                this.caster.getY() + 1.2d + vecOffset.y() + getEntityData().get(OFFSET_Y),
                this.caster.getZ() + vecOffset.z() + getEntityData().get(OFFSET_Z)
        );
    }

    private void updateWithBarako() {
        setYaw((float)((this.caster.yHeadRot + 90f) * Math.PI / 180d));
        setPitch((float)(-this.caster.getXRot() * Math.PI / 180d));
        Vec3 vecOffset1 = (new Vec3(0d, 0d, 0.6d)).yRot((float)Math.toRadians(-this.caster.getYRot()));
        Vec3 vecOffset2 = (new Vec3(1.2d, 0d, 0d)).yRot(-getYaw()).xRot(getPitch());
        setPos(this.caster.getX() + vecOffset1.x() + vecOffset2.x(),
                this.caster.getY() + 1.4d + vecOffset1.y() + vecOffset2.y(),
                this.caster.getZ() + vecOffset1.z() + vecOffset2.z()
        );
    }

    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
    }

    public static class SolarbeamHitResult {
        private BlockHitResult blockHit;
        private final List<LivingEntity> entities = new ArrayList<>();

        public void setBlockHit(HitResult rayTraceResult) {
            if (rayTraceResult.getType() == HitResult.Type.BLOCK) this.blockHit = (BlockHitResult)rayTraceResult;
        }

        public void addEntityHit(LivingEntity entity) {
            this.entities.add(entity);
        }
    }
}
