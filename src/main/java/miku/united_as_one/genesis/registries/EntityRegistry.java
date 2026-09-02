package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.combat.protectedhealth.ProtectedZombieDummy;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import miku.united_as_one.genesis.content.entity.spell.blood_boss.BloodBossFireEruptionAoe;
import miku.united_as_one.genesis.content.entity.spell.blood_boss.blood_dagger.BloodDaggerEntity;
import miku.united_as_one.genesis.content.entity.spell.blood_boss.blood_dagger.BloodField;
import miku.united_as_one.genesis.content.entity.effect.MeleeDamageTextEntity;
import miku.united_as_one.genesis.content.entity.effect.MithrilImpactRingEntity;
import miku.united_as_one.genesis.content.entity.effect.MithrilMeleeSlashEntity;
import miku.united_as_one.genesis.content.entity.spell.celestial_source.MeteorProjectileEntity;
import miku.united_as_one.genesis.content.entity.spell.celestial_source.MeteorStarEntity;
import miku.united_as_one.genesis.content.entity.spell.thunder.DeathLaserEntity;
import miku.united_as_one.genesis.content.entity.spell.celestial_source.MeteorShockwaveEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Genesis.MOD_ID);

    // 受血者的匕首
    public static final RegistryObject<EntityType<BloodDaggerEntity>> BLOOD_DAGGER_PROJECTILE =
            fastMisc("blood_dagger", BloodDaggerEntity::new, 0.5F, 0.5F, 64);

    public static final RegistryObject<EntityType<BloodField>> BLOOD_FIELD =
            fastMisc("blood_field", BloodField::new, 4.0F, 1.2F, 64);

    public static final RegistryObject<EntityType<BloodBossFireEruptionAoe>> BLOOD_BOSS_FIRE_ERUPTION_AOE =
            fastMisc("blood_boss_fire_eruption", BloodBossFireEruptionAoe::new, 4.0F, 0.8F, 64);

    public static final RegistryObject<EntityType<MeteorProjectileEntity>> METEOR_PROJECTILE =
            fastMisc("meteor_projectile", MeteorProjectileEntity::new, 0.45F, 0.45F, 64);

    public static final RegistryObject<EntityType<MeteorStarEntity>> METEOR_STAR =
            fastMisc("meteor_star", MeteorStarEntity::new, 0.6F, 0.6F, 64);

    public static final RegistryObject<EntityType<MeteorShockwaveEntity>> METEOR_SHOCKWAVE =
            fastMisc("meteor_shockwave", MeteorShockwaveEntity::new, 0.25F, 0.25F, 64);

    public static final RegistryObject<EntityType<MithrilImpactRingEntity>> MITHRIL_IMPACT_RING =
            fastMisc("mithril_impact_ring", MithrilImpactRingEntity::new, 0.25F, 0.25F, 64);

    public static final RegistryObject<EntityType<MithrilMeleeSlashEntity>> MITHRIL_MELEE_SLASH =
            fastMisc("mithril_melee_slash", MithrilMeleeSlashEntity::new, 0.5F, 0.5F, 64);

    public static final RegistryObject<EntityType<MeleeProjBase>> MELEE_PROJ_BASE =
            fastMisc("melee_proj_base", MeleeProjBase::new, 0.5F, 0.5F, 64);

    public static final RegistryObject<EntityType<MeleeDamageTextEntity>> MELEE_DAMAGE_TEXT =
            fastMisc("melee_damage_text", MeleeDamageTextEntity::new, 0.1F, 0.1F, 64);

    // 雷霆
    public static final RegistryObject<EntityType<DeathLaserEntity>> DEATH_LASER =
            fastMisc("death_laser", DeathLaserEntity::new, 0.5f, 0.5f, 64);

    public static final RegistryObject<EntityType<ProtectedZombieDummy>> PROTECTED_ZOMBIE_DUMMY =
            ENTITY_TYPES.register("protected_zombie_dummy", () -> EntityType.Builder
                    .of(ProtectedZombieDummy::new, MobCategory.MONSTER).sized(0.6F, 1.95F)
                    .clientTrackingRange(10).updateInterval(3).build("protected_zombie_dummy"));

    private EntityRegistry() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> fastMisc(
            String name,
            EntityType.EntityFactory<T> factory,
            float width,
            float height,
            int trackingRange
    ) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(width, height)
                .clientTrackingRange(trackingRange)
                .updateInterval(1)
                .build(name));
    }
}
