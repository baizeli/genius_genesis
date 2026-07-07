package miku.united_as_one.genesis.item.weapon;

import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.Map;
import miku.united_as_one.genesis.combat.autoswing.IAutoSwingItem;
import miku.united_as_one.genesis.combat.autoswing.SwingPipeline;
import miku.united_as_one.genesis.combat.meleeproj.IMeleeProjListener;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjResources;
import miku.united_as_one.genesis.entity.effect.MithrilImpactRingEntity;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.GenesisParticles;
import miku.united_as_one.genesis.util.SlashColors;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("removal")
public class MithrilSword extends MagicSwordItem implements IAutoSwingItem, IMeleeProjListener {
    private static final int SLASH_COLOR = SlashColors.MITHRIL_LIGHT_BLUE;
    private static final int IMPACT_RING_COLOR = 0xB8AEEBFF;
    private static final float AOE_RADIUS = 1.75F;
    private static final float MANA_RETURN_MULTIPLIER = 1.0F;
    private static final int NORMAL_DURABILITY_COST = 1;
    private static final int FINAL_DURABILITY_COST = 3;
    private static final float FINAL_PRIMARY_DAMAGE_MULTIPLIER = 1.5F;
    private static final float FINAL_SECONDARY_DAMAGE_MULTIPLIER = 1.2F;
    private static final SwingPipeline SWING_PIPELINE = SwingPipeline.builder()
            .mode(SwingPipeline.SwingMode.AUTO_HOLD)
            .advance(SwingPipeline.AdvanceMode.SEQUENTIAL)
            .release(SwingPipeline.ReleaseMode.RESET)
            .end(SwingPipeline.EndMode.LOOP)
            .waitFullAttackCooldownAfterCombo(true)
            .stage(2, MithrilSword::swingA)
            .stage(5, MithrilSword::swingB)
            .stage(2, MithrilSword::swingA)
            .stage(13, MithrilSword::swingB)
            .stage(8, MithrilSword::swingC)
            .build();

    public MithrilSword(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, (int) -tier.getAttackDamageBonus(), 0.0F,
                SpellDataRegistryHolder.of(new SpellDataRegistryHolder(SpellRegistry.RAY_OF_FROST_SPELL, 5)),
                Map.of(), properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return true;
    }

    @Override
    public boolean onHitTarget(MeleeProjBase proj, Level level, LivingEntity attacker, LivingEntity target, int hitType) {
        if (!level.isClientSide && attacker != null && target != null) {
            float damage = proj.getDamage();
            float before = damageState(target);
            DamageSource source = attacker instanceof Player player
                    ? attacker.damageSources().playerAttack(player)
                    : attacker.damageSources().mobAttack(attacker);
            target.invulnerableTime = 0;
            if (target.hurt(source, damage)) {
                level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_STRONG,
                        attacker.getSoundSource(), 2.0F, 1.0F);
                float actualDamage = Math.max(0.0F, before - damageState(target));
                hurtWeapon(attacker, isFinalComboHit(hitType) ? FINAL_DURABILITY_COST : NORMAL_DURABILITY_COST);
                if (hitType == 1) {
                    triggerFinalComboHit(target, attacker, actualDamage);
                }
            }
        }
        return true;
    }

    @Override
    public SwingPipeline getSwingPipeline(ItemStack stack) {
        return SWING_PIPELINE;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    public static int slashColor() {
        return SLASH_COLOR;
    }

    public static float aoeRadius() {
        return AOE_RADIUS;
    }

    public static float finalPrimaryDamageMultiplier() {
        return FINAL_PRIMARY_DAMAGE_MULTIPLIER;
    }

    public static float finalSecondaryDamageMultiplier() {
        return FINAL_SECONDARY_DAMAGE_MULTIPLIER;
    }

    public static int finalDurabilityCost() {
        return FINAL_DURABILITY_COST;
    }

    private static void swingA(ServerPlayer player, ServerLevel level, ItemStack stack, int index) {
        spawn(player, level, stack, 0.0F, 215.0F, 3, 0.0F, 90.0F, 75.0F, 1.0F, 1, 0, 0);
    }

    private static void swingB(ServerPlayer player, ServerLevel level, ItemStack stack, int index) {
        spawn(player, level, stack, 90.0F, 300.0F, 3, 0.0F, 90.0F, -40.0F, 1.0F, 1, 0, 0);
    }

    private static void swingC(ServerPlayer player, ServerLevel level, ItemStack stack, int index) {
        spawn(player, level, stack, 250.0F, 0.0F, 2, -45.0F, 90.0F, 75.0F, FINAL_PRIMARY_DAMAGE_MULTIPLIER, 1, 1, 1);
        spawn(player, level, stack, 0.0F, 230.0F, 4, -45.0F, 90.0F, 90.0F, FINAL_SECONDARY_DAMAGE_MULTIPLIER, 1, 0, 2);
    }

    private static void spawn(ServerPlayer player, ServerLevel level, ItemStack stack, float startDeg, float endDeg,
                              int duration, float rx, float ry, float rz, float damagePower, int removeTime,
                              int useType, int hitType) {
        MeleeProjBase proj = new MeleeProjBase(EntityRegistry.MELEE_PROJ_BASE.get(), level);
        double cx = player.getX();
        double cy = player.getY() + (double) player.getBbHeight() * 0.65D;
        double cz = player.getZ();
        float actualBaseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        proj.moveTo(cx, cy, cz, player.getYRot(), player.getXRot());
        proj.setOwner(player);
        proj.initFacingFromOwner(player);
        proj.setFaceOwnerDirection(true);
        proj.setHeldByOwner(true);
        proj.setUser(player);
        proj.setDisplayStack(stack.copy());
        proj.setTrailColorTexture(MeleeProjResources.COLOR_7);
        proj.setTrailTexture(MeleeProjResources.TRAIL_9);
        proj.setTrailLength(6);
        proj.setRemoveTimer(removeTime);
        proj.setFirstPersonNoDepth(true);
        proj.setUseType(useType);
        proj.setHitType(hitType);
        proj.setHitParticle(GenesisParticles.OVERLORD_PARTICLE.get());
        proj.setParticleCount(20);
        proj.setParticleSpeed(0.5F);
        proj.setParticleSpeedRandom(0.06F);
        if (useType == 1) {
            proj.setShakeIntensity(3.0F);
            proj.setShakeTime(14);
            proj.setHitboxWidth(3.0F);
            proj.setInnerRadius(4.0F);
            proj.setHitboxExtendIn(2.75F);
            proj.setOuterRadius(11.0F);
            proj.setHitboxLength(7.0F);
            proj.setSegment(10);
            proj.setTrailWarpTexture(MeleeProjResources.TRAIL_6);
        } else {
            proj.setShakeIntensity(0.5F);
            proj.setTrailWarpTexture(MeleeProjResources.TRAIL_9);
        }
        proj.setSwingSound(SoundEvents.PLAYER_ATTACK_SWEEP);
        proj.setEndSound(SoundEvents.ILLUSIONER_CAST_SPELL);
        proj.setDebugHitbox(false);
        proj.setStartAngleDeg(startDeg);
        proj.setEndAngleDeg(endDeg);
        proj.setRotationDuration(duration);
        proj.setDamage(actualBaseDamage * damagePower);
        proj.setPlaneRotXDeg(rx);
        proj.setPlaneRotYDeg(ry);
        proj.setPlaneRotZDeg(rz);
        level.addFreshEntity(proj);
    }

    private static boolean isFinalComboHit(int hitType) {
        return hitType == 1 || hitType == 2;
    }

    private static void triggerFinalComboHit(LivingEntity target, LivingEntity attacker, float directDamage) {
        Level level = attacker.level();
        spawnImpactRing(level, target);
        float aoeDamage = directDamage > 0.0F ? dealAoeDamage(level, attacker, target, directDamage) : 0.0F;
        if (attacker instanceof ServerPlayer player) {
            restoreMana(player, (directDamage + aoeDamage) * MANA_RETURN_MULTIPLIER);
        }
    }

    private static void hurtWeapon(LivingEntity attacker, int amount) {
        if (amount <= 0) {
            return;
        }
        ItemStack stack = attacker.getMainHandItem();
        if (stack.isEmpty() || !stack.isDamageableItem()) {
            return;
        }
        stack.hurtAndBreak(amount, attacker, entity -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
    }

    private static void spawnImpactRing(Level level, LivingEntity target) {
        Vec3 position = new Vec3(
                target.getX(),
                target.getY() + 0.05D + (double) target.getEyeHeight() * 0.15D,
                target.getZ()
        );
        level.addFreshEntity(new MithrilImpactRingEntity(level, position, AOE_RADIUS, IMPACT_RING_COLOR));
    }

    private static float dealAoeDamage(Level level, LivingEntity attacker, LivingEntity primaryTarget, float damage) {
        AABB area = primaryTarget.getBoundingBox().inflate(AOE_RADIUS);
        DamageSource source = attacker instanceof Player player
                ? attacker.damageSources().playerAttack(player)
                : attacker.damageSources().mobAttack(attacker);
        float totalDamage = 0.0F;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity.isAlive()
                        && entity != attacker
                        && entity != primaryTarget
                        && !attacker.isAlliedTo(entity))) {
            float before = damageState(target);
            if (target.hurt(source, damage)) {
                totalDamage += Math.max(0.0F, before - damageState(target));
            }
        }
        return totalDamage;
    }

    private static float damageState(LivingEntity entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    private static void restoreMana(ServerPlayer player, float amount) {
        if (amount <= 0.0F) {
            return;
        }
        MagicData data = MagicData.getPlayerMagicData(player);
        AttributeInstance maxMana = player.getAttribute(AttributeRegistry.MAX_MANA.get());
        if (maxMana == null) {
            return;
        }
        data.setMana(Math.min(data.getMana() + amount, (float) maxMana.getValue()));
        PacketDistributor.sendToPlayer(player, new SyncManaPacket(data));
    }
}
