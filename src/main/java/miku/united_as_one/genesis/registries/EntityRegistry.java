package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import miku.united_as_one.genesis.entity.effect.MeleeDamageTextEntity;
import miku.united_as_one.genesis.entity.effect.MithrilImpactRingEntity;
import miku.united_as_one.genesis.entity.effect.MithrilMeleeSlashEntity;
import miku.united_as_one.genesis.entity.spell.chaos.ChaosSwordAoeEntity;
import miku.united_as_one.genesis.entity.spell.chaos.ChaosSwordEntity;
import miku.united_as_one.genesis.entity.spell.MeteorProjectileEntity;
import miku.united_as_one.genesis.entity.spell.MeteorShockwaveEntity;
import miku.united_as_one.genesis.entity.spell.MeteorStarEntity;
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

    public static final RegistryObject<EntityType<MeteorProjectileEntity>> METEOR_PROJECTILE =
            fastMisc("meteor_projectile", MeteorProjectileEntity::new, 0.45F, 0.45F, 64);

    public static final RegistryObject<EntityType<MeteorStarEntity>> METEOR_STAR =
            fastMisc("meteor_star", MeteorStarEntity::new, 0.6F, 0.6F, 64);

    public static final RegistryObject<EntityType<MeteorShockwaveEntity>> METEOR_SHOCKWAVE =
            fastMisc("meteor_shockwave", MeteorShockwaveEntity::new, 0.25F, 0.25F, 64);

    public static final RegistryObject<EntityType<ChaosSwordEntity>> CHAOS_SWORD =
            fastMisc("chaos_sword", ChaosSwordEntity::new, 0.5F, 0.5F, 64);

    public static final RegistryObject<EntityType<ChaosSwordAoeEntity>> CHAOS_SWORD_AOE =
            fastMisc("chaos_sword_aoe", ChaosSwordAoeEntity::new, 4.0F, 0.8F, 64);

    public static final RegistryObject<EntityType<MithrilImpactRingEntity>> MITHRIL_IMPACT_RING =
            fastMisc("mithril_impact_ring", MithrilImpactRingEntity::new, 0.25F, 0.25F, 64);

    public static final RegistryObject<EntityType<MithrilMeleeSlashEntity>> MITHRIL_MELEE_SLASH =
            fastMisc("mithril_melee_slash", MithrilMeleeSlashEntity::new, 0.5F, 0.5F, 64);

    public static final RegistryObject<EntityType<MeleeProjBase>> MELEE_PROJ_BASE =
            fastMisc("melee_proj_base", MeleeProjBase::new, 0.5F, 0.5F, 64);

    public static final RegistryObject<EntityType<MeleeDamageTextEntity>> MELEE_DAMAGE_TEXT =
            fastMisc("melee_damage_text", MeleeDamageTextEntity::new, 0.1F, 0.1F, 64);

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
