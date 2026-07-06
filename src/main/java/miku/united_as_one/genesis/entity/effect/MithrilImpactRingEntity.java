package miku.united_as_one.genesis.entity.effect;

import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class MithrilImpactRingEntity extends Entity {
    public static final int LIFE_TIME = 14;
    private static final float MIN_RADIUS = 0.25F;
    private static final float MAX_RADIUS = 32.0F;
    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(MithrilImpactRingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(MithrilImpactRingEntity.class, EntityDataSerializers.INT);

    public MithrilImpactRingEntity(EntityType<? extends MithrilImpactRingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public MithrilImpactRingEntity(Level level, Vec3 position, float radius, int color) {
        this(EntityRegistry.MITHRIL_IMPACT_RING.get(), level);
        this.setRadius(radius);
        this.setColor(color);
        this.moveTo(position.x, position.y, position.z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, 3.5F);
        this.entityData.define(DATA_COLOR, 0xB8AEEBFF);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(Vec3.ZERO);
        if (!this.level().isClientSide && this.tickCount >= LIFE_TIME) {
            this.discard();
        }
    }

    public float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public void setRadius(float radius) {
        this.entityData.set(DATA_RADIUS, Mth.clamp(radius, MIN_RADIUS, MAX_RADIUS));
    }

    public int getColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(DATA_COLOR, color);
    }

    public float getAge(float partialTicks) {
        return (float) this.tickCount + partialTicks;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double range = Math.max(64.0D, (double) this.getRadius() * 16.0D);
        return distance < range * range;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.setRadius(tag.contains("Radius") ? tag.getFloat("Radius") : 3.5F);
        this.setColor(tag.contains("Color") ? tag.getInt("Color") : 0xB8AEEBFF);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putFloat("Radius", this.getRadius());
        tag.putInt("Color", this.getColor());
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
