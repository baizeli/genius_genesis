package miku.united_as_one.genesis.content.entity.spell.celestial_source;

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

public class MeteorShockwaveEntity extends Entity {
    public static final int LIFE_TIME = 16;
    private static final float MIN_MAX_SIZE = 0.25F;
    private static final float MAX_MAX_SIZE = 128.0F;
    private static final EntityDataAccessor<Float> DATA_MAX_SIZE =
            SynchedEntityData.defineId(MeteorShockwaveEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR_SEED =
            SynchedEntityData.defineId(MeteorShockwaveEntity.class, EntityDataSerializers.INT);

    public MeteorShockwaveEntity(EntityType<? extends MeteorShockwaveEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public MeteorShockwaveEntity(Level level, Vec3 position, float maxSize, int colorSeed) {
        this(EntityRegistry.METEOR_SHOCKWAVE.get(), level);
        this.setMaxSize(maxSize);
        this.setColorSeed(colorSeed);
        this.moveTo(position.x, position.y, position.z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_MAX_SIZE, 6.0F);
        this.entityData.define(DATA_COLOR_SEED, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(Vec3.ZERO);
        if (!this.level().isClientSide && this.tickCount >= LIFE_TIME) {
            this.discard();
        }
    }

    public float getMaxSize() {
        return this.entityData.get(DATA_MAX_SIZE);
    }

    public void setMaxSize(float maxSize) {
        this.entityData.set(DATA_MAX_SIZE, Mth.clamp(maxSize, MIN_MAX_SIZE, MAX_MAX_SIZE));
    }

    public int getColorSeed() {
        return this.entityData.get(DATA_COLOR_SEED);
    }

    public void setColorSeed(int colorSeed) {
        this.entityData.set(DATA_COLOR_SEED, colorSeed);
    }

    public float getLifeProgress(float partialTicks) {
        return Mth.clamp(((float) this.tickCount + partialTicks) / (float) LIFE_TIME, 0.0F, 1.0F);
    }

    public float getRenderRadius(float partialTicks) {
        float progress = this.getLifeProgress(partialTicks);
        float inverted = 1.0F - progress;
        float eased = 1.0F - inverted * inverted * inverted * inverted;
        return Math.max(0.05F, this.getMaxSize() * eased);
    }

    public float getAlpha(float partialTicks) {
        float fade = 1.0F - this.getLifeProgress(partialTicks);
        return Mth.clamp(0.38F * fade * fade, 0.0F, 0.38F);
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
        double range = Math.max(64.0D, (double) this.getMaxSize() * 16.0D);
        return distance < range * range;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.setMaxSize(tag.contains("MaxSize") ? tag.getFloat("MaxSize") : 6.0F);
        this.setColorSeed(tag.getInt("ColorSeed"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putFloat("MaxSize", this.getMaxSize());
        tag.putInt("ColorSeed", this.getColorSeed());
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
