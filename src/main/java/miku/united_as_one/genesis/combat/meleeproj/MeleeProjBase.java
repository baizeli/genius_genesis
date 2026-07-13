/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.IdMap
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.entity.IEntityAdditionalSpawnData
 *  net.minecraftforge.network.NetworkHooks
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package miku.united_as_one.genesis.combat.meleeproj;

import miku.united_as_one.genesis.combat.meleeproj.IMeleeProjListener;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import miku.united_as_one.genesis.network.packet.ResetAttackTickerPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.IdMap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MeleeProjBase
extends Entity
implements IEntityAdditionalSpawnData {
    private int trailLength;
    private float[] rotationCache;
    private int segment;
    private float startAngleDeg;
    private float endAngleDeg;
    private int rotationDuration;
    private float planeRotXDeg;
    private float planeRotYDeg;
    private float planeRotZDeg;
    private float innerRadius;
    private float outerRadius;
    private float tiltAngle;
    private float headFadePortion;
    private float hitboxLength;
    private float hitboxExtendIn;
    private float hitboxExtendOut;
    private float hitboxWidth;
    private float hitboxHeight;
    private float damage;
    private int hitboxSamples;
    private float shakeIntensity;
    private int shakeTime;
    private int hitType;
    private int useType;
    private float brightness;
    private boolean firstPersonNoDepth = false;
    private ResourceLocation trailTexture;
    private ResourceLocation trailColorTexture;
    private ResourceLocation trailWarpTexture;
    private ResourceLocation trailTexture0;
    private ResourceLocation trailTexture1;
    private ResourceLocation trailTexture2;
    private ResourceLocation trailBloomTexture;
    private ResourceLocation spellDamageSource;
    private ResourceLocation displayTexture;
    private ItemStack displayStack = ItemStack.EMPTY;
    public boolean DEBUG_HITBOX = false;
    private boolean faceOwnerDirection = true;
    private boolean heldByOwner = false;
    private ParticleOptions hitParticle = null;
    private int particleCount = 0;
    private float particleSpeed = 0.4f;
    private float particleSpeedRandom = 0.0f;
    private static final EntityDataAccessor<Float> DATA_FACE_YAW = SynchedEntityData.defineId(MeleeProjBase.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_FACE_PITCH = SynchedEntityData.defineId(MeleeProjBase.class, (EntityDataSerializer)EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(MeleeProjBase.class, (EntityDataSerializer)EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_HELD = SynchedEntityData.defineId(MeleeProjBase.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    private int timer;
    private int waitTimer;
    private int removeTimer;
    private float currentRotation;
    private float prevRotation;
    private UUID ownerUUID;
    private LivingEntity cachedOwner;
    private final Set<Integer> alreadyHit = new HashSet<Integer>();
    private LivingEntity user = null;
    private SoundEvent onSound = null;
    private SoundEvent swingSound = null;
    private SoundEvent endSound = null;
    private SoundEvent hurtSound = null;
    private boolean swingStartCalled = false;
    private boolean swingHalfCalled = false;
    private boolean swingEndCalled = false;

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public float[] getRotationCache() {
        return this.rotationCache;
    }

    public float getInnerRadius() {
        return this.innerRadius;
    }

    public float getOuterRadius() {
        return this.outerRadius;
    }

    public float getTiltAngle() {
        return this.tiltAngle;
    }

    public int getTrailSegments() {
        return this.trailLength;
    }

    public int getSegment() {
        return this.segment;
    }

    public float getCurrentRotation() {
        return this.currentRotation;
    }

    public float getPrevRotation() {
        return this.prevRotation;
    }

    public float getHeadFadePortion() {
        return this.headFadePortion;
    }

    public int getUseType() {
        return this.useType;
    }

    public int getHitType() {
        return this.hitType;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public float getDamage() {
        return this.damage;
    }

    public boolean getDebugValue() {
        return this.DEBUG_HITBOX;
    }

    public LivingEntity getUser() {
        return this.user;
    }

    public ResourceLocation getTrailTexture() {
        return this.trailTexture;
    }

    public ResourceLocation getTrailColorTexture() {
        return this.trailColorTexture;
    }

    public ResourceLocation getTrailWarpTexture() {
        return this.trailWarpTexture;
    }

    public ResourceLocation getTrailTexture0() {
        return this.trailTexture0;
    }

    public ResourceLocation getTrailTexture1() {
        return this.trailTexture1;
    }

    public ResourceLocation getTrailTexture2() {
        return this.trailTexture2;
    }

    public ResourceLocation getTrailBloomTexture() {
        return this.trailBloomTexture;
    }

    public boolean isFirstPersonNoDepth() {
        return this.firstPersonNoDepth;
    }

    public ItemStack getDisplayStack() {
        return this.displayStack;
    }

    public ResourceLocation getDisplayTexture() {
        return this.displayTexture;
    }

    public void setDisplayStack(ItemStack stack) {
        this.displayStack = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    public void setDisplayTexture(ResourceLocation texture) {
        this.displayTexture = texture;
    }

    public boolean isFaceOwnerDirection() {
        return this.faceOwnerDirection;
    }

    public void setFaceOwnerDirection(boolean v) {
        this.faceOwnerDirection = v;
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER).orElse(null);
    }

    public boolean isHeldByOwner() {
        return (Boolean)this.entityData.get(DATA_HELD) != false && this.getOwnerUUID() != null;
    }

    public void setHeldByOwner(boolean v) {
        this.entityData.set(DATA_HELD, v && this.getOwnerUUID() != null);
    }

    public void setUser(LivingEntity v) {
        this.user = v;
    }

    public Vec3 getTipDirWorld(float partialTicks) {
        float rot = Mth.lerp((float)partialTicks, (float)this.prevRotation, (float)this.currentRotation);
        Vector3f d = this.swingRotation().transform(new Vector3f((float)Math.cos(rot), (float)Math.sin(rot), 0.0f));
        if (d.lengthSquared() < 1.0E-8f) {
            return new Vec3(0.0, -1.0, 0.0);
        }
        d.normalize();
        return new Vec3((double)d.x, (double)d.y, (double)d.z);
    }

    public void setOwner(LivingEntity owner) {
        this.cachedOwner = owner;
        if (owner == null) {
            this.entityData.set(DATA_OWNER, Optional.empty());
            this.entityData.set(DATA_HELD, false);
        } else {
            this.entityData.set(DATA_OWNER, Optional.of(owner.getUUID()));
        }
    }

    public void setTrailLength(int v) {
        this.trailLength = Math.max(1, v);
        this.resetSwingState();
    }

    public void setSegment(int v) {
        this.segment = Math.max(1, v);
    }

    public void setStartAngleDeg(float v) {
        this.startAngleDeg = v;
        this.resetSwingState();
    }

    public void setEndAngleDeg(float v) {
        this.endAngleDeg = v;
    }

    public void setRotationDuration(int v) {
        this.rotationDuration = Math.max(1, v);
    }

    public void setPlaneRotXDeg(float v) {
        this.planeRotXDeg = v;
    }

    public void setPlaneRotYDeg(float v) {
        this.planeRotYDeg = v;
    }

    public void setPlaneRotZDeg(float v) {
        this.planeRotZDeg = v;
    }

    public void setInnerRadius(float v) {
        this.innerRadius = v;
    }

    public void setOuterRadius(float v) {
        this.outerRadius = v;
    }

    public void setTiltAngle(float v) {
        this.tiltAngle = v;
    }

    public void setHeadFadePortion(float v) {
        this.headFadePortion = v;
    }

    public void setHitboxLength(float v) {
        this.hitboxLength = v;
    }

    public void setHitboxExtendIn(float v) {
        this.hitboxExtendIn = v;
    }

    public void setHitboxExtendOut(float v) {
        this.hitboxExtendOut = v;
    }

    public void setHitboxWidth(float v) {
        this.hitboxWidth = v;
    }

    public void setHitboxHeight(float v) {
        this.hitboxHeight = v;
    }

    public void setDamage(float v) {
        this.damage = v;
    }

    public void setShakeIntensity(float v) {
        this.shakeIntensity = v;
    }

    public void setShakeTime(int v) {
        this.shakeTime = v;
    }

    public void setHitType(int v) {
        this.hitType = v;
    }

    public void setHitboxSamples(int v) {
        this.hitboxSamples = Math.max(1, v);
    }

    public void setUseType(int v) {
        this.useType = v;
    }

    public void setBrightness(float v) {
        this.brightness = v;
    }

    public void setTrailTexture(ResourceLocation v) {
        this.trailTexture = v;
    }

    public void setTrailColorTexture(ResourceLocation v) {
        this.trailColorTexture = v;
    }

    public void setTrailWarpTexture(ResourceLocation v) {
        this.trailWarpTexture = v;
    }

    public void setTrailTexture0(ResourceLocation v) {
        this.trailTexture0 = v;
    }

    public void setTrailTexture1(ResourceLocation v) {
        this.trailTexture1 = v;
    }

    public void setTrailTexture2(ResourceLocation v) {
        this.trailTexture2 = v;
    }

    public void setTrailTexture3(ResourceLocation v) {
        this.trailTexture2 = v;
    }

    public void setTrailBloomTexture(ResourceLocation v) {
        this.trailBloomTexture = v;
    }

    public void setSpellDamageSource(ResourceLocation v) {
        this.spellDamageSource = v;
    }

    public void setSwingSound(SoundEvent v) {
        this.swingSound = v;
    }

    public void setOnSound(SoundEvent v) {
        this.onSound = v;
    }

    public void setEndSound(SoundEvent v) {
        this.endSound = v;
    }

    public void setHurtSound(SoundEvent v) {
        this.hurtSound = v;
    }

    public void setDebugHitbox(boolean v) {
        this.DEBUG_HITBOX = v;
    }

    public void setRemoveTimer(int v) {
        this.removeTimer = v;
    }

    public void setFirstPersonNoDepth(boolean v) {
        this.firstPersonNoDepth = v;
    }

    public void setHitParticle(ParticleOptions v) {
        this.hitParticle = v;
    }

    public void setParticleCount(int v) {
        this.particleCount = Math.max(0, v);
    }

    public void setParticleSpeed(float v) {
        this.particleSpeed = v;
    }

    public void setParticleSpeedRandom(float v) {
        this.particleSpeedRandom = Math.max(0.0f, v);
    }

    public ParticleOptions getHitParticle() {
        return this.hitParticle;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    public void initFacingFromOwner(LivingEntity owner) {
        if (owner != null) {
            this.entityData.set(DATA_FACE_YAW, owner.getYRot());
            this.entityData.set(DATA_FACE_PITCH, owner.getXRot());
        }
    }

    public MeleeProjBase(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
        this.trailLength = 7;
        this.segment = 5;
        this.startAngleDeg = 0.0f;
        this.endAngleDeg = 270.0f;
        this.rotationDuration = 6;
        this.planeRotXDeg = -45.0f;
        this.planeRotYDeg = 90.0f;
        this.planeRotZDeg = 90.0f;
        this.innerRadius = 1.3f;
        this.outerRadius = 4.0f;
        this.tiltAngle = 35.0f;
        this.headFadePortion = 0.05f;
        this.hitboxLength = 2.6f;
        this.hitboxExtendIn = 0.0f;
        this.hitboxExtendOut = 0.0f;
        this.hitboxWidth = 0.8f;
        this.hitboxHeight = 0.8f;
        this.damage = 10.0f;
        this.hitboxSamples = 6;
        this.shakeIntensity = 0.0f;
        this.shakeTime = 4;
        this.hitType = 0;
        this.hitParticle = null;
        this.particleCount = 6;
        this.particleSpeed = 0.4f;
        this.particleSpeedRandom = 0.05f;
        this.useType = 2;
        this.brightness = 0.8f;
        this.removeTimer = 0;
        this.trailTexture = MeleeProjResources.TRAIL_9;
        this.trailColorTexture = MeleeProjResources.COLOR_1;
        this.trailWarpTexture = MeleeProjResources.TRAIL_9;
        this.trailTexture0 = MeleeProjResources.WHITE;
        this.trailTexture1 = MeleeProjResources.COLOR_MAP4;
        this.trailTexture2 = MeleeProjResources.COLOR_MAP4;
        this.trailBloomTexture = MeleeProjResources.MAP_2;
        this.resetSwingState();
    }

    private void resetSwingState() {
        float start;
        this.currentRotation = start = (float)Math.toRadians(this.startAngleDeg);
        this.prevRotation = start;
        if (this.rotationCache == null || this.rotationCache.length != this.trailLength) {
            this.rotationCache = new float[Math.max(1, this.trailLength)];
        }
        Arrays.fill(this.rotationCache, start);
    }

    public void defineSynchedData() {
        this.entityData.define(DATA_FACE_YAW, 0.0f);
        this.entityData.define(DATA_FACE_PITCH, 0.0f);
        this.entityData.define(DATA_OWNER, Optional.empty());
        this.entityData.define(DATA_HELD, false);
    }

    protected Vec3 soundPos() {
        LivingEntity owner = this.resolveOwner();
        if (owner != null) {
            return owner.getEyePosition().add(owner.getLookAngle().scale(1.0));
        }
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }

    protected void playSwingSound(SoundEvent sound, float volume, float pitch) {
        if (sound == null || this.level().isClientSide) {
            return;
        }
        Vec3 p = this.soundPos();
        this.level().playSound(null, p.x, p.y, p.z, sound, SoundSource.PLAYERS, volume, pitch);
    }

    protected void onSwingStart() {
        IMeleeProjListener l;
        Item item;
        if (!this.level().isClientSide && (item = this.displayStack.getItem()) instanceof IMeleeProjListener && (l = (IMeleeProjListener)item).onMeleeSwingStart(this, this.level(), this.user)) {
            return;
        }
        if (this.onSound != null) {
            this.playSwingSound(this.onSound, 1.25f, 0.85f + this.random.nextFloat() * 0.4f);
        }
    }

    protected void onSwingHalf() {
        IMeleeProjListener l;
        Item item;
        if (!this.level().isClientSide && (item = this.displayStack.getItem()) instanceof IMeleeProjListener && (l = (IMeleeProjListener)item).onMeleeSwingHalf(this, this.level(), this.user)) {
            return;
        }
        if (this.swingSound != null) {
            this.playSwingSound(this.swingSound, 1.25f, 0.85f + this.random.nextFloat() * 0.4f);
        }
    }

    protected void onSwingEnd() {
        IMeleeProjListener l;
        Item item;
        if (!this.level().isClientSide && (item = this.displayStack.getItem()) instanceof IMeleeProjListener && (l = (IMeleeProjListener)item).onMeleeSwingEnd(this, this.level(), this.user)) {
            return;
        }
        if (this.endSound != null) {
            this.playSwingSound(this.endSound, 1.25f, 0.85f + this.random.nextFloat() * 0.4f);
        }
    }

    private void HitTarget(LivingEntity le) {
        IMeleeProjListener l;
        Player player;
        LivingEntity livingEntity;
        Item item;
        if (this.hurtSound != null) {
            this.playSwingSound(this.hurtSound, 1.0f, 0.85f + this.random.nextFloat() * 0.4f);
        }
        if (this.user != null && (livingEntity = this.user) instanceof Player) {
            player = (Player)livingEntity;
            if (this.shakeIntensity != 0.0f) {
                ArrayList nPlayers = new ArrayList();
                Level level = this.level();
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    nPlayers.addAll(serverLevel.players());
                }
                MeleeProjHooks.shakeInRadius(player.position, 2.5, this.shakeIntensity, this.shakeTime, MeleeProjHooks.ShakeType.RANDOM, nPlayers);
            }
        }
        if (!this.level().isClientSide && (item = this.displayStack.getItem()) instanceof IMeleeProjListener && ((l = (IMeleeProjListener)item).onHitTarget(this, this.level(), this.user, le) || l.onHitTarget(this, this.level(), this.user, le, this.hitType))) {
            return;
        }
        if (this.spellDamageSource == null && this.user != null && (livingEntity = this.user) instanceof Player) {
            player = (Player)livingEntity;
            le.invulnerableTime = 0;
            le.hurt(player.damageSources().playerAttack(player), this.damage);
        } else {
            le.hurt(this.damageSource(), this.damage);
        }
    }

    private void spawnDamageValue(LivingEntity le, float healthBefore, float absorbBefore) {
        float absorbAfter;
        float healthAfter = le.getHealth();
        float actualDamage = healthBefore - healthAfter + (absorbBefore - (absorbAfter = le.getAbsorptionAmount()));
        if (actualDamage < 0.0f) {
            actualDamage = 0.0f;
        }
        MeleeProjHooks.spawnDamageText(le, actualDamage);
    }

    private static String formatDamage(float dmg) {
        float rounded = (float)Math.round(dmg * 10.0f) / 10.0f;
        if (rounded == (float)((long)rounded)) {
            return String.valueOf((long)rounded);
        }
        return String.valueOf(rounded);
    }

    public Quaternionf swingRotation() {
        Quaternionf q = new Quaternionf();
        if (this.faceOwnerDirection) {
            float yawRad = (float)Math.toRadians(((Float)this.entityData.get(DATA_FACE_YAW)).floatValue());
            float pitchRad = (float)Math.toRadians(((Float)this.entityData.get(DATA_FACE_PITCH)).floatValue());
            q.rotateY(-yawRad);
            q.rotateX(pitchRad);
            float t = Math.abs(pitchRad) / 1.5707964f;
            t = Mth.clamp((float)t, (float)0.0f, (float)1.0f);
            float xDeg = Mth.lerp((float)t, (float)-6.0f, (float)(-pitchRad * 2.0f));
            q.rotateX((float)Math.toRadians(xDeg));
            q.rotateZ((float)Math.toRadians(this.planeRotZDeg));
        }
        q.rotateX((float)Math.toRadians(this.planeRotXDeg));
        q.rotateY((float)Math.toRadians(this.planeRotYDeg));
        q.rotateZ((float)Math.toRadians(this.planeRotZDeg));
        return q;
    }

    public Vector3f arcPointLocal(float angleRad, float radius) {
        return new Vector3f((float)Math.cos(angleRad) * radius, (float)Math.sin(angleRad) * radius, 0.0f);
    }

    public Vec3 localToWorld(Vector3f local) {
        Vector3f r = this.swingRotation().transform(new Vector3f((Vector3fc)local));
        return new Vec3(this.getX() + (double)r.x, this.getY() + (double)r.y, this.getZ() + (double)r.z);
    }

    public void tick() {
        float progress;
        LivingEntity livingEntity;
        LivingEntity owner;
        super.tick();
        if (this.isHeldByOwner() && (owner = this.resolveOwner()) != null) {
            double cx = owner.getX();
            double cy = owner.getY() + (double)owner.getBbHeight() * 0.65;
            double cz = owner.getZ();
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
            this.setPos(cx, cy, cz);
        }
        if (this.user != null && (livingEntity = this.user) instanceof Player) {
            Player player = (Player)livingEntity;
            if (!this.level().isClientSide && player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.resetAttackStrengthTicker();
                ResetAttackTickerPacket.send(serverPlayer);
            }
        }
        if (!this.swingStartCalled) {
            this.swingStartCalled = true;
            this.onSwingStart();
        }
        for (int i = this.trailLength - 1; i > 0; --i) {
            this.rotationCache[i] = this.rotationCache[i - 1];
        }
        this.rotationCache[0] = this.currentRotation;
        this.prevRotation = this.currentRotation;
        float start = (float)Math.toRadians(this.startAngleDeg);
        float end = (float)Math.toRadians(this.endAngleDeg);
        float f = progress = this.timer < this.rotationDuration ? (float)(this.timer + 1) / (float)this.rotationDuration : 1.0f;
        if (this.timer < this.rotationDuration) {
            this.currentRotation = start + (end - start) * progress;
        }
        if (!this.swingHalfCalled && progress >= 0.5f) {
            this.swingHalfCalled = true;
            this.onSwingHalf();
        }
        if (!this.level().isClientSide) {
            this.sweepDamage();
        } else {
            this.spawnHitboxParticles();
        }
        ++this.timer;
        if (!this.swingEndCalled && this.timer >= this.rotationDuration) {
            this.swingEndCalled = true;
            this.onSwingEnd();
        }
        if (this.timer > this.rotationDuration) {
            ++this.waitTimer;
        }
        if (this.removeTimer == -100) {
            if (this.rotationDuration == 1) {
                if (this.waitTimer >= this.rotationDuration * 2 + 3) {
                    this.discard();
                }
            } else if (this.rotationDuration == 2) {
                if (this.waitTimer >= this.rotationDuration * 2 + 2) {
                    this.discard();
                }
            } else if (this.rotationDuration == 3) {
                if (this.waitTimer >= this.rotationDuration * 2 + 1) {
                    this.discard();
                }
            } else if (this.waitTimer >= this.rotationDuration * 2) {
                this.discard();
            }
        } else if (this.waitTimer >= this.rotationDuration * 2 + this.removeTimer) {
            this.discard();
        }
    }

    private LivingEntity resolveOwner() {
        ServerLevel sl;
        Entity entity;
        if (this.cachedOwner != null && this.cachedOwner.isAlive()) {
            return this.cachedOwner;
        }
        UUID id = this.getOwnerUUID();
        if (id == null) {
            return null;
        }
        Level lvl = this.level();
        Player p = lvl.getPlayerByUUID(id);
        if (p != null) {
            this.cachedOwner = p;
            return p;
        }
        if (lvl instanceof ServerLevel && (entity = (sl = (ServerLevel)lvl).getEntity(id)) instanceof LivingEntity) {
            LivingEntity le;
            this.cachedOwner = le = (LivingEntity)entity;
            return le;
        }
        return null;
    }

    public Vec3 getHeldRenderCenter(float partialTicks) {
        if (!this.isHeldByOwner()) {
            return null;
        }
        LivingEntity owner = this.resolveOwner();
        if (owner == null) {
            return null;
        }
        double ox = Mth.lerp((double)partialTicks, (double)owner.xOld, (double)owner.getX());
        double oy = Mth.lerp((double)partialTicks, (double)owner.yOld, (double)owner.getY()) + (double)owner.getBbHeight() * 0.65;
        double oz = Mth.lerp((double)partialTicks, (double)owner.zOld, (double)owner.getZ());
        return new Vec3(ox, oy, oz);
    }

    private void sweepDamage() {
        List<HitBox> boxes = this.collectHitboxes(1.0f);
        if (boxes.isEmpty()) {
            return;
        }
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -1.7976931348623157E308;
        double maxY = -1.7976931348623157E308;
        double maxZ = -1.7976931348623157E308;
        for (HitBox b : boxes) {
            minX = Math.min(minX, b.center.x);
            minY = Math.min(minY, b.center.y);
            minZ = Math.min(minZ, b.center.z);
            maxX = Math.max(maxX, b.center.x);
            maxY = Math.max(maxY, b.center.y);
            maxZ = Math.max(maxZ, b.center.z);
        }
        double pad = (double)Math.max(this.hitboxLength, Math.max(this.hitboxWidth, this.hitboxHeight)) + 2.0;
        AABB broad = new AABB(minX - pad, minY - pad, minZ - pad, maxX + pad, maxY + pad, maxZ + pad);
        UUID ownerId = this.getOwnerUUID();
        List<Entity> candidates = this.level().getEntities((Entity)this, broad, e -> e instanceof LivingEntity && !(e instanceof Player) && e.isAlive() && (ownerId == null || !e.getUUID().equals(ownerId)));
        if (candidates.isEmpty()) {
            return;
        }
        for (Entity e2 : candidates) {
            Item item;
            if (this.alreadyHit.contains(e2.getId())) continue;
            AABB bb = e2.getBoundingBox();
            boolean hit = false;
            for (HitBox b : boxes) {
                if (!MeleeProjBase.obbIntersectsAabb(b, bb)) continue;
                hit = true;
                break;
            }
            if (!hit) continue;
            this.alreadyHit.add(e2.getId());
            if (!(e2 instanceof LivingEntity)) continue;
            LivingEntity le = (LivingEntity)e2;
            float healthBefore = le.getHealth();
            float absorbBefore = le.getAbsorptionAmount();
            this.HitTarget(le);
            if (this.level().isClientSide || !((item = this.displayStack.getItem()) instanceof IMeleeProjListener)) continue;
            IMeleeProjListener l = (IMeleeProjListener)item;
            if (l.onDisplayDamage(this, this.level(), le, healthBefore, absorbBefore)) {
                return;
            }
            this.spawnDamageValue(le, healthBefore, absorbBefore);
        }
    }

    private static boolean obbIntersectsAabb(HitBox b, AABB aabb) {
        Vec3 ac = aabb.getCenter();
        double ex = aabb.getXsize() * 0.5;
        double ey = aabb.getYsize() * 0.5;
        double ez = aabb.getZsize() * 0.5;
        Vec3 d = ac.subtract(b.center);
        return MeleeProjBase.sepOk(d, b.axisL, b.halfL, ex, ey, ez) && MeleeProjBase.sepOk(d, b.axisW, b.halfW, ex, ey, ez) && MeleeProjBase.sepOk(d, b.axisH, b.halfH, ex, ey, ez);
    }

    private static boolean sepOk(Vec3 d, Vector3f axis, float half, double ex, double ey, double ez) {
        double aabbProj;
        double dist = Math.abs(d.x * (double)axis.x + d.y * (double)axis.y + d.z * (double)axis.z);
        return dist <= (double)half + (aabbProj = ex * (double)Math.abs(axis.x) + ey * (double)Math.abs(axis.y) + ez * (double)Math.abs(axis.z));
    }

    private DamageSource damageSource() {
        LivingEntity owner = this.resolveOwner();
        if (this.spellDamageSource != null) {
            AbstractSpell spell = SpellRegistry.getSpell(this.spellDamageSource);
            Entity attacker = owner == null ? this : owner;
            return spell.getDamageSource(this, attacker);
        }
        if (owner != null) {
            return this.level().damageSources().mobAttack(owner);
        }
        return this.level().damageSources().generic();
    }

    private void spawnHitboxParticles() {
        if (this.hitParticle == null || this.particleCount <= 0) {
            return;
        }
        List<HitBox> boxes = this.collectHitboxes(1.0f, true);
        if (boxes.isEmpty()) {
            return;
        }
        float sign = Math.signum(this.currentRotation - this.prevRotation);
        if (sign == 0.0f) {
            sign = 1.0f;
        }
        for (int i = 0; i < this.particleCount; ++i) {
            HitBox b = boxes.get(this.random.nextInt(boxes.size()));
            float rl = (this.random.nextFloat() * 2.0f - 1.0f) * b.halfL;
            float rw = (this.random.nextFloat() * 2.0f - 1.0f) * b.halfW;
            float rh = (this.random.nextFloat() * 2.0f - 1.0f) * b.halfH;
            double px = b.center.x + (double)(b.axisL.x * rl) + (double)(b.axisW.x * rw) + (double)(b.axisH.x * rh);
            double py = b.center.y + (double)(b.axisL.y * rl) + (double)(b.axisW.y * rw) + (double)(b.axisH.y * rh);
            double pz = b.center.z + (double)(b.axisL.z * rl) + (double)(b.axisW.z * rw) + (double)(b.axisH.z * rh);
            double vx = b.axisW.x * sign * this.particleSpeed;
            double vy = b.axisW.y * sign * this.particleSpeed;
            double vz = b.axisW.z * sign * this.particleSpeed;
            if (this.particleSpeedRandom > 0.0f) {
                vx += (double)((this.random.nextFloat() * 2.0f - 1.0f) * this.particleSpeedRandom);
                vy += (double)((this.random.nextFloat() * 2.0f - 1.0f) * this.particleSpeedRandom);
                vz += (double)((this.random.nextFloat() * 2.0f - 1.0f) * this.particleSpeedRandom);
            }
            this.level().addParticle(this.hitParticle, px, py, pz, vx, vy, vz);
        }
    }

    public List<HitBox> collectHitboxes(float partialTicks) {
        return this.collectHitboxes(partialTicks, false);
    }

    public List<HitBox> collectHitboxes(float partialTicks, boolean isDust) {
        ArrayList<HitBox> boxes = new ArrayList<HitBox>();
        if (Math.abs(this.currentRotation - this.prevRotation) < 1.0E-5f) {
            return boxes;
        }
        Vec3 origin = this.getHeldRenderCenter(partialTicks);
        double ox = origin != null ? origin.x : this.getX();
        double oy = origin != null ? origin.y : this.getY();
        double oz = origin != null ? origin.z : this.getZ();
        float from = this.prevRotation;
        float to = this.prevRotation + (this.currentRotation - this.prevRotation) * partialTicks;
        Quaternionf rot = this.swingRotation();
        Vector3f planeX = rot.transform(new Vector3f(1.0f, 0.0f, 0.0f)).normalize();
        Vector3f planeY = rot.transform(new Vector3f(0.0f, 1.0f, 0.0f)).normalize();
        Vector3f planeN = rot.transform(new Vector3f(0.0f, 0.0f, 1.0f)).normalize();
        float fullLen = this.hitboxLength + this.hitboxExtendIn + this.hitboxExtendOut;
        float halfL = fullLen * 0.5f;
        float halfW = this.hitboxWidth * 0.5f;
        float halfH = isDust ? this.hitboxHeight * 0.05f : this.hitboxHeight * 0.5f;
        float centerR = (this.innerRadius + this.outerRadius) * 0.5f + (this.hitboxExtendOut - this.hitboxExtendIn) * 0.5f;
        for (int s = 0; s <= this.hitboxSamples; ++s) {
            float a = from + (to - from) * ((float)s / (float)this.hitboxSamples);
            float cos = (float)Math.cos(a);
            float sin = (float)Math.sin(a);
            Vector3f bladeAxis = new Vector3f((Vector3fc)planeX).mul(cos).add((Vector3fc)new Vector3f((Vector3fc)planeY).mul(sin)).normalize();
            Vector3f tangent = new Vector3f((Vector3fc)planeX).mul(-sin).add((Vector3fc)new Vector3f((Vector3fc)planeY).mul(cos)).normalize();
            Vector3f localCenter = new Vector3f((Vector3fc)bladeAxis).mul(centerR);
            Vec3 center = new Vec3(ox + (double)localCenter.x, oy + (double)localCenter.y, oz + (double)localCenter.z);
            boxes.add(new HitBox(center, bladeAxis, tangent, new Vector3f((Vector3fc)planeN), halfL, halfW, halfH));
        }
        return boxes;
    }

    public void readAdditionalSaveData(CompoundTag c) {
        this.timer = c.getInt("Timer");
        this.currentRotation = c.getFloat("Rotation");
        this.prevRotation = c.getFloat("PrevRotation");
        this.waitTimer = c.getInt("WaitTimer");
        if (c.hasUUID("Owner")) {
            this.entityData.set(DATA_OWNER, Optional.of(c.getUUID("Owner")));
        }
        this.faceOwnerDirection = c.getBoolean("FaceDir");
        this.entityData.set(DATA_HELD, c.getBoolean("Held"));
        this.entityData.set(DATA_FACE_YAW, c.getFloat("FaceYaw"));
        this.entityData.set(DATA_FACE_PITCH, c.getFloat("FacePitch"));
        this.swingStartCalled = c.getBoolean("SwStart");
        this.swingHalfCalled = c.getBoolean("SwHalf");
        this.swingEndCalled = c.getBoolean("SwEnd");
        this.removeTimer = c.getInt("RemoveTimer");
        if (c.contains("SpellDamageSource")) {
            this.spellDamageSource = new ResourceLocation(c.getString("SpellDamageSource"));
        } else {
            this.spellDamageSource = null;
        }
        if (c.contains("DisplayTexture")) {
            this.displayTexture = new ResourceLocation(c.getString("DisplayTexture"));
        } else {
            this.displayTexture = null;
        }
    }

    public void addAdditionalSaveData(CompoundTag c) {
        c.putInt("Timer", this.timer);
        c.putFloat("Rotation", this.currentRotation);
        c.putFloat("PrevRotation", this.prevRotation);
        c.putInt("WaitTimer", this.waitTimer);
        UUID ownerId = this.getOwnerUUID();
        if (ownerId != null) {
            c.putUUID("Owner", ownerId);
        }
        c.putBoolean("FaceDir", this.faceOwnerDirection);
        c.putBoolean("Held", ((Boolean)this.entityData.get(DATA_HELD)).booleanValue());
        c.putFloat("FaceYaw", ((Float)this.entityData.get(DATA_FACE_YAW)).floatValue());
        c.putFloat("FacePitch", ((Float)this.entityData.get(DATA_FACE_PITCH)).floatValue());
        c.putBoolean("SwStart", this.swingStartCalled);
        c.putBoolean("SwHalf", this.swingHalfCalled);
        c.putBoolean("SwEnd", this.swingEndCalled);
        c.putInt("RemoveTimer", this.removeTimer);
        if (this.spellDamageSource != null) {
            c.putString("SpellDamageSource", this.spellDamageSource.toString());
        }
        if (this.displayTexture != null) {
            c.putString("DisplayTexture", this.displayTexture.toString());
        }
    }

    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeVarInt(this.trailLength);
        buf.writeVarInt(this.segment);
        buf.writeFloat(this.startAngleDeg);
        buf.writeFloat(this.endAngleDeg);
        buf.writeVarInt(this.rotationDuration);
        buf.writeFloat(this.planeRotXDeg);
        buf.writeFloat(this.planeRotYDeg);
        buf.writeFloat(this.planeRotZDeg);
        buf.writeFloat(this.innerRadius);
        buf.writeFloat(this.outerRadius);
        buf.writeFloat(this.tiltAngle);
        buf.writeFloat(this.headFadePortion);
        buf.writeFloat(this.hitboxLength);
        buf.writeFloat(this.hitboxExtendIn);
        buf.writeFloat(this.hitboxExtendOut);
        buf.writeInt(this.removeTimer);
        buf.writeFloat(this.hitboxWidth);
        buf.writeFloat(this.hitboxHeight);
        buf.writeFloat(this.damage);
        buf.writeFloat(this.shakeIntensity);
        buf.writeInt(this.shakeTime);
        buf.writeInt(this.hitType);
        buf.writeVarInt(this.hitboxSamples);
        buf.writeVarInt(this.useType);
        buf.writeFloat(this.brightness);
        buf.writeBoolean(this.faceOwnerDirection);
        buf.writeResourceLocation(this.trailTexture);
        buf.writeResourceLocation(this.trailColorTexture);
        buf.writeResourceLocation(this.trailWarpTexture);
        buf.writeResourceLocation(this.trailTexture0);
        buf.writeResourceLocation(this.trailTexture1);
        buf.writeResourceLocation(this.trailTexture2);
        buf.writeResourceLocation(this.trailBloomTexture);
        buf.writeBoolean(this.spellDamageSource != null);
        if (this.spellDamageSource != null) {
            buf.writeResourceLocation(this.spellDamageSource);
        }
        buf.writeBoolean(this.displayTexture != null);
        if (this.displayTexture != null) {
            buf.writeResourceLocation(this.displayTexture);
        }
        buf.writeBoolean(this.firstPersonNoDepth);
        buf.writeItem(this.getDisplayStack());
        buf.writeInt(this.particleCount);
        buf.writeFloat(this.particleSpeed);
        buf.writeFloat(this.particleSpeedRandom);
        boolean hasParticle = this.hitParticle != null;
        buf.writeBoolean(hasParticle);
        if (hasParticle) {
            buf.writeId((IdMap)BuiltInRegistries.PARTICLE_TYPE, (Object)this.hitParticle.getType());
            this.hitParticle.writeToNetwork(buf);
        }
    }

    public void readSpawnData(FriendlyByteBuf buf) {
        ParticleType type;
        this.trailLength = buf.readVarInt();
        this.segment = buf.readVarInt();
        this.startAngleDeg = buf.readFloat();
        this.endAngleDeg = buf.readFloat();
        this.rotationDuration = buf.readVarInt();
        this.planeRotXDeg = buf.readFloat();
        this.planeRotYDeg = buf.readFloat();
        this.planeRotZDeg = buf.readFloat();
        this.innerRadius = buf.readFloat();
        this.outerRadius = buf.readFloat();
        this.tiltAngle = buf.readFloat();
        this.headFadePortion = buf.readFloat();
        this.hitboxLength = buf.readFloat();
        this.hitboxExtendIn = buf.readFloat();
        this.hitboxExtendOut = buf.readFloat();
        this.removeTimer = buf.readInt();
        this.hitboxWidth = buf.readFloat();
        this.hitboxHeight = buf.readFloat();
        this.damage = buf.readFloat();
        this.shakeIntensity = buf.readFloat();
        this.shakeTime = buf.readInt();
        this.hitType = buf.readInt();
        this.hitboxSamples = buf.readVarInt();
        this.useType = buf.readVarInt();
        this.brightness = buf.readFloat();
        this.faceOwnerDirection = buf.readBoolean();
        this.trailTexture = buf.readResourceLocation();
        this.trailColorTexture = buf.readResourceLocation();
        this.trailWarpTexture = buf.readResourceLocation();
        this.trailTexture0 = buf.readResourceLocation();
        this.trailTexture1 = buf.readResourceLocation();
        this.trailTexture2 = buf.readResourceLocation();
        this.trailBloomTexture = buf.readResourceLocation();
        this.spellDamageSource = buf.readBoolean() ? buf.readResourceLocation() : null;
        this.displayTexture = buf.readBoolean() ? buf.readResourceLocation() : null;
        this.firstPersonNoDepth = buf.readBoolean();
        this.setDisplayStack(buf.readItem());
        this.particleCount = buf.readInt();
        this.particleSpeed = buf.readFloat();
        this.particleSpeedRandom = buf.readFloat();
        this.hitParticle = buf.readBoolean() ? ((type = (ParticleType)buf.readById((IdMap)BuiltInRegistries.PARTICLE_TYPE)) != null ? MeleeProjBase.readParticleOptions(buf, type) : null) : null;
        this.resetSwingState();
    }

    private static <T extends ParticleOptions> T readParticleOptions(FriendlyByteBuf buf, ParticleType<T> type) {
        return (T)type.getDeserializer().fromNetwork(type, buf);
    }

    public static final class HitBox {
        public final Vec3 center;
        public final Vector3f axisL;
        public final Vector3f axisW;
        public final Vector3f axisH;
        public final float halfL;
        public final float halfW;
        public final float halfH;

        public HitBox(Vec3 c, Vector3f l, Vector3f w, Vector3f h, float hl, float hw, float hh) {
            this.center = c;
            this.axisL = l;
            this.axisW = w;
            this.axisH = h;
            this.halfL = hl;
            this.halfW = hw;
            this.halfH = hh;
        }
    }
}



