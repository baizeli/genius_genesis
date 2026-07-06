package miku.united_as_one.genesis.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class MeleeDamageTextEntity extends Entity {
    public static final int LIFE_TIME = 20;
    private static final EntityDataAccessor<Float> DATA_DAMAGE =
            SynchedEntityData.defineId(MeleeDamageTextEntity.class, EntityDataSerializers.FLOAT);

    public MeleeDamageTextEntity(EntityType<? extends MeleeDamageTextEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public MeleeDamageTextEntity(EntityType<? extends MeleeDamageTextEntity> type, Level level, float damage) {
        this(type, level);
        this.setDamage(damage);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DAMAGE, 0.0F);
    }

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    private void setDamage(float damage) {
        this.entityData.set(DATA_DAMAGE, damage);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(0.0D, 0.035D, 0.0D);
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        if (!this.level().isClientSide && this.tickCount >= LIFE_TIME) {
            this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setDamage(tag.getFloat("Damage"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Damage", this.getDamage());
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
