package miku.united_as_one.genesis.entity.effect;

import miku.united_as_one.genesis.item.weapon.MithrilSword;
import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class MithrilMeleeSlashEntity extends Entity {
    public static final int LIFE_TIME = 9;
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID =
            SynchedEntityData.defineId(MithrilMeleeSlashEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STAGE =
            SynchedEntityData.defineId(MithrilMeleeSlashEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(MithrilMeleeSlashEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_DAMAGE_MULTIPLIER =
            SynchedEntityData.defineId(MithrilMeleeSlashEntity.class, EntityDataSerializers.FLOAT);
    private boolean hasHit;

    public MithrilMeleeSlashEntity(EntityType<? extends MithrilMeleeSlashEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public MithrilMeleeSlashEntity(Level level, LivingEntity owner, int stage, int color, float damageMultiplier) {
        this(EntityRegistry.MITHRIL_MELEE_SLASH.get(), level);
        this.setOwnerId(owner.getId());
        this.setStage(stage);
        this.setColor(color);
        this.setDamageMultiplier(damageMultiplier);
        this.setYRot(owner.getYRot());
        this.setXRot(owner.getXRot());
        this.moveTo(renderPosition(owner));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_OWNER_ID, -1);
        this.entityData.define(DATA_STAGE, 0);
        this.entityData.define(DATA_COLOR, 0xC0AEEBFF);
        this.entityData.define(DATA_DAMAGE_MULTIPLIER, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(Vec3.ZERO);
        Entity owner = this.level().getEntity(this.getOwnerId());
        if (owner instanceof LivingEntity living) {
            this.setYRot(living.getYRot());
            this.setXRot(living.getXRot());
            this.moveTo(renderPosition(living));
            if (!this.level().isClientSide && !this.hasHit && this.tickCount >= 2) {
                this.hasHit = true;
                this.hitTargets(living);
            }
        }
        if (!this.level().isClientSide && this.tickCount >= LIFE_TIME) {
            this.discard();
        }
    }

    public int getOwnerId() {
        return this.entityData.get(DATA_OWNER_ID);
    }

    public void setOwnerId(int ownerId) {
        this.entityData.set(DATA_OWNER_ID, ownerId);
    }

    public int getStage() {
        return this.entityData.get(DATA_STAGE);
    }

    public void setStage(int stage) {
        this.entityData.set(DATA_STAGE, Mth.clamp(stage, 0, 2));
    }

    public int getColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(DATA_COLOR, color);
    }

    public float getDamageMultiplier() {
        return this.entityData.get(DATA_DAMAGE_MULTIPLIER);
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.entityData.set(DATA_DAMAGE_MULTIPLIER, Mth.clamp(damageMultiplier, 0.1F, 16.0F));
    }

    public float getAge(float partialTicks) {
        return (float) this.tickCount + partialTicks;
    }

    public static void spawn(ServerLevel level, LivingEntity owner, int stage, int color, float damageMultiplier) {
        level.addFreshEntity(new MithrilMeleeSlashEntity(level, owner, stage, color, damageMultiplier));
    }

    private void hitTargets(LivingEntity owner) {
        ItemStack stack = owner.getMainHandItem();
        if (!(stack.getItem() instanceof MithrilSword)) {
            return;
        }
        float range = this.getStage() == 2 ? 5.6F : 4.2F;
        float halfAngleCos = this.getStage() == 2 ? 0.10F : 0.24F;
        float damage = (float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.getDamageMultiplier();
        if (damage <= 0.0F) {
            return;
        }

        Vec3 origin = owner.getEyePosition();
        Vec3 look = owner.getLookAngle().normalize();
        AABB area = owner.getBoundingBox().inflate(range, 2.0D, range);
        DamageSource source = owner instanceof Player player
                ? owner.damageSources().playerAttack(player)
                : owner.damageSources().mobAttack(owner);

        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity.isAlive() && entity != owner && !owner.isAlliedTo(entity))) {
            Vec3 toTarget = target.getBoundingBox().getCenter().subtract(origin);
            double distanceSqr = toTarget.lengthSqr();
            if (distanceSqr > range * range || distanceSqr < 0.0001D) {
                continue;
            }
            Vec3 direction = toTarget.normalize();
            if (direction.dot(look) < halfAngleCos) {
                continue;
            }

            target.hurt(source, damage);
        }
    }

    private static Vec3 renderPosition(LivingEntity owner) {
        Vec3 look = owner.getLookAngle();
        return owner.position()
                .add(0.0D, owner.getBbHeight() * 0.62D, 0.0D)
                .add(look.x * 0.65D, 0.0D, look.z * 0.65D);
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
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.setOwnerId(tag.getInt("OwnerId"));
        this.setStage(tag.getInt("Stage"));
        this.setColor(tag.contains("Color") ? tag.getInt("Color") : 0xC0AEEBFF);
        this.setDamageMultiplier(tag.contains("DamageMultiplier") ? tag.getFloat("DamageMultiplier") : 1.0F);
        this.hasHit = tag.getBoolean("HasHit");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("OwnerId", this.getOwnerId());
        tag.putInt("Stage", this.getStage());
        tag.putInt("Color", this.getColor());
        tag.putFloat("DamageMultiplier", this.getDamageMultiplier());
        tag.putBoolean("HasHit", this.hasHit);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
