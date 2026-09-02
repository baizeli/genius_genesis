package miku.united_as_one.genesis.combat.protectedhealth;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class ProtectedZombieDummy extends Zombie implements ProtectedHealthCarrier {
    public static final float PROTECTED_MAX = 800F;
    public static final float MAX_DAMAGE_PER_HIT = 10F;
    private static final EntityDataAccessor<String> DATA_X = SynchedEntityData.defineId(ProtectedZombieDummy.class, EntityDataSerializers.STRING);
    @SuppressWarnings("unused")
    private volatile long protectedNonce;
    private boolean ready, syncing, vanillaDamagePass, deathStarted;

    public ProtectedZombieDummy(EntityType<? extends Zombie> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() { return Zombie.createAttributes().add(Attributes.MAX_HEALTH, PROTECTED_MAX); }
    @Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(DATA_X, ProtectedHealthCipher.encrypt("0.0")); }
    @Override public void onAddedToWorld() { super.onAddedToWorld(); if(!level().isClientSide){ProtectedHealthManager.initialize(this,PROTECTED_MAX);ready=true;syncMirror();} }
    @Override public void tick(){super.tick();if(!level().isClientSide&&ready&&!deathStarted)syncMirror();}
    @Override public boolean hurt(@NotNull DamageSource source,float amount){
        if(level().isClientSide||!ready||deathStarted||amount<=0||isInvulnerableTo(source))return false;
        float acceptedDamage = ReverseHealthMath.limitIncomingDamage(amount, MAX_DAMAGE_PER_HIT);
        if (acceptedDamage <= 0) return false;
        syncMirror();
        float mirrorBefore = super.getHealth();
        boolean accepted;
        vanillaDamagePass = true;
        syncing = true;
        try {
            accepted = super.hurt(source, acceptedDamage);
        } finally {
            syncing = false;
            vanillaDamagePass = false;
        }
        float actualHealthDamage = Math.max(0, mirrorBefore - super.getHealth());
        if (actualHealthDamage > 0) ProtectedHealthManager.damage(this, actualHealthDamage);
        syncMirror();
        if (!ReverseHealthMath.isAlive(ProtectedHealthManager.read(this))) die(source);
        return accepted;
    }
    @Override public void die(@NotNull DamageSource source) {
        if (!ready) { super.die(source); return; }
        if (level().isClientSide) return;
        if (vanillaDamagePass) return;
        if (ReverseHealthMath.isAlive(ProtectedHealthManager.read(this))) {
            syncMirror();
            return;
        }
        if (!deathStarted) deathStarted = true;
        syncing = true; super.setHealth(0); syncing = false;
        super.die(source);
    }
    @Override public void heal(float amount){if(!level().isClientSide&&ready&&!deathStarted){ProtectedHealthManager.heal(this,amount,PROTECTED_MAX);syncMirror();}}
    @Override public void setHealth(float value){if(!ready||syncing||deathStarted)super.setHealth(value);else if(!level().isClientSide)syncMirror();}
    @Override public void kill(){if(deathStarted)super.kill();else if(!level().isClientSide)syncMirror();}
    @Override public boolean isAlive(){return !ready?super.isAlive():ReverseHealthMath.isAlive(ProtectedHealthManager.read(this))&&!isRemoved();}
    @Override public boolean isDeadOrDying(){return ready?!ReverseHealthMath.isAlive(ProtectedHealthManager.read(this)):super.isDeadOrDying();}
    private void syncMirror(){float value=ReverseHealthMath.mirror(ProtectedHealthManager.read(this),PROTECTED_MAX,getMaxHealth());syncing=true;super.setHealth(value);syncing=false;}
    @Override public String acq(){return entityData.get(DATA_X);}
    @Override public void upd(String value){entityData.set(DATA_X,value);}
    @Override public float cap(){return PROTECTED_MAX;}
}
