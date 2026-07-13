package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.network.casting.SyncCooldownPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.network.GenesisNetwork;
import miku.united_as_one.genesis.network.packet.PlayerShadowPacket;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AutoSpellConfig
@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShatterFistSpell extends ChaosBaseSpell {
    private static final ResourceLocation ANIMATION = Genesis.id("shatter_fist_attack");
    private static final AnimationHolder SHATTER_FIST_ANIMATION = new AnimationHolder(ANIMATION, true, true);
    private static final int CHARGE_TICKS = 28;
    private static final int SHADOW_DURATION_TICKS = 18;
    private static final float DASH_DISTANCE = 10.0F;
    private static final double DASH_SPEED = 4.2D;
    private static final int ACTIVE_DASH_TICKS = 6;
    private static final double COLLISION_INFLATE = 0.45D;
    private static final double IMPACT_RADIUS = 3.0D;
    private static final List<ActiveDash> ACTIVE_DASHES = new ArrayList<>();

    private final ResourceLocation spellId = Genesis.id("shatter_fist");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(15.0D)
            .build();

    public ShatterFistSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = CHARGE_TICKS;
        this.baseManaCost = 30;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)));
    }

    @Override
    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(io.redspace.ironsspellbooks.registries.SoundRegistry.SHADOW_SLASH.get());
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SHATTER_FIST_ANIMATION;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        if (level instanceof ServerLevel serverLevel) {
            beginDash(serverLevel, caster, spellLevel);
        }
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE_DASHES.isEmpty()) {
            return;
        }

        Iterator<ActiveDash> activeIterator = ACTIVE_DASHES.iterator();
        while (activeIterator.hasNext()) {
            ActiveDash dash = activeIterator.next();
            ServerLevel level = event.getServer().getLevel(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dash.level));
            if (level == null || !(level.getEntity(dash.caster) instanceof LivingEntity caster) || !caster.isAlive()
                    || dash.tick(level, caster)) {
                activeIterator.remove();
            }
        }
    }

    private static void beginDash(ServerLevel level, LivingEntity caster, int spellLevel) {
        Vec3 forward = horizontalForward(caster);
        caster.setDeltaMovement(forward.scale(DASH_SPEED).add(0.0D, 0.12D, 0.0D));
        caster.hurtMarked = true;
        GenesisNetwork.sendToTrackingAndSelf(caster, new PlayerShadowPacket(caster.getId(), SHADOW_DURATION_TICKS));
        ACTIVE_DASHES.add(new ActiveDash(level.dimension().location(), caster.getUUID(), spellLevel,
                caster.position(), caster.position(), forward, ACTIVE_DASH_TICKS));
    }

    private static boolean impact(ServerLevel level, LivingEntity caster, int spellLevel, Vec3 impactCenter) {
        ShatterFistSpell spell = (ShatterFistSpell) miku.united_as_one.genesis.registries.SpellRegistry.SHATTER_FIST.get();
        caster.setDeltaMovement(Vec3.ZERO);
        caster.hurtMarked = true;

        boolean killed = false;
        float damage = spell.getDamage(spellLevel, caster);
        AABB impactBox = AABB.ofSize(impactCenter, IMPACT_RADIUS * 2.0D, IMPACT_RADIUS * 2.0D, IMPACT_RADIUS * 2.0D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, impactBox,
                target -> target != caster && target.isAlive() && !target.isSpectator()
                        && target.distanceToSqr(impactCenter) <= IMPACT_RADIUS * IMPACT_RADIUS);
        targets.sort((a, b) -> Double.compare(a.distanceToSqr(impactCenter), b.distanceToSqr(impactCenter)));

        for (LivingEntity target : targets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            if (!Utils.hasLineOfSight(level, impactCenter, targetCenter, true)) {
                continue;
            }

            boolean hurt = DamageSources.applyDamage(target, damage, spell.getDamageSource(caster));
            if (!hurt) {
                continue;
            }

            spawnBloodParticles(level, target);
            EnchantmentHelper.doPostDamageEffects(caster, target);
            Vec3 knock = target.position().subtract(caster.position()).normalize().add(0.0D, 0.25D, 0.0D).normalize().scale(0.7D);
            target.setDeltaMovement(target.getDeltaMovement().add(knock));
            target.hurtMarked = true;
            if (!target.isAlive() || target.isDeadOrDying()) {
                killed = true;
            }
        }

        if (killed && caster instanceof ServerPlayer player) {
            refreshCooldown(player, spell);
        }
        return true;
    }

    private static Vec3 horizontalForward(LivingEntity caster) {
        Vec3 look = caster.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0D, look.z);
        if (forward.lengthSqr() < 1.0E-4D) {
            forward = Vec3.directionFromRotation(0.0F, caster.getYRot());
            forward = new Vec3(forward.x, 0.0D, forward.z);
        }
        return forward.normalize();
    }

    private static void spawnBloodParticles(ServerLevel level, LivingEntity target) {
        MagicManager.spawnParticles(level, ParticleHelper.BLOOD,
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5D,
                target.getZ(),
                25,
                target.getBbWidth() * 0.45D,
                target.getBbHeight() * 0.35D,
                target.getBbWidth() * 0.45D,
                0.18D,
                true);
    }

    private static void refreshCooldown(ServerPlayer player, ShatterFistSpell spell) {
        MagicData.getPlayerMagicData(player).getPlayerCooldowns().removeCooldown(spell.getSpellId());
        PacketDistributor.sendToPlayer(player, new SyncCooldownPacket(spell.getSpellId(), 0));
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) + Utils.getWeaponDamage(caster);
    }

    private String getDamageText(int spellLevel, LivingEntity caster) {
        if (caster == null) {
            return String.valueOf(getSpellPower(spellLevel, null));
        }
        float weaponDamage = Utils.getWeaponDamage(caster);
        String weaponDamageText = weaponDamage > 0.0F
                ? String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1))
                : "";
            return Utils.stringTruncation(getDamage(spellLevel, caster), 1) + weaponDamageText;
    }

    private static final class ActiveDash {
        private final ResourceLocation level;
        private final UUID caster;
        private final int spellLevel;
        private final Vec3 origin;
        private final Vec3 forward;
        private Vec3 lastPos;
        private int ticks;

        private ActiveDash(ResourceLocation level, UUID caster, int spellLevel, Vec3 origin, Vec3 lastPos,
                           Vec3 forward, int ticks) {
            this.level = level;
            this.caster = caster;
            this.spellLevel = spellLevel;
            this.origin = origin;
            this.lastPos = lastPos;
            this.forward = forward;
            this.ticks = ticks;
        }

        private boolean tick(ServerLevel level, LivingEntity caster) {
            Vec3 currentPos = caster.position();
            Vec3 previousEye = this.lastPos.add(0.0D, caster.getEyeHeight(), 0.0D);
            Vec3 currentEye = caster.getEyePosition();
            BlockHitResult blockHit = level.clip(new ClipContext(previousEye, currentEye, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
            if (blockHit.getType() != net.minecraft.world.phys.HitResult.Type.MISS) {
                caster.setDeltaMovement(Vec3.ZERO);
                caster.hurtMarked = true;
                return true;
            }

            AABB sweptBox = caster.getBoundingBox()
                    .move(this.lastPos.subtract(currentPos))
                    .minmax(caster.getBoundingBox())
                    .inflate(COLLISION_INFLATE, 0.35D, COLLISION_INFLATE);
            List<LivingEntity> collisions = level.getEntitiesOfClass(LivingEntity.class, sweptBox,
                    target -> target != caster && target.isAlive() && !target.isSpectator());
            collisions.sort((a, b) -> Double.compare(a.distanceToSqr(caster), b.distanceToSqr(caster)));
            for (LivingEntity target : collisions) {
                Vec3 targetCenter = target.getBoundingBox().getCenter();
                Vec3 toTarget = targetCenter.subtract(currentEye);
                if (toTarget.dot(this.forward) < -0.25D || !Utils.hasLineOfSight(level, currentEye, targetCenter, true)) {
                    continue;
                }
                return impact(level, caster, this.spellLevel, targetCenter);
            }

            this.lastPos = currentPos;
            boolean expired = --this.ticks <= 0 || currentPos.distanceTo(this.origin) >= DASH_DISTANCE;
            if (expired) {
                caster.setDeltaMovement(caster.getDeltaMovement().scale(0.2D));
                caster.hurtMarked = true;
            }
            return expired;
        }
    }
}
