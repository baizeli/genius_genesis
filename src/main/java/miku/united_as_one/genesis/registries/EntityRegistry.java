package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.entity.spell.chaos.ChaosSwordAoeEntity;
import miku.united_as_one.genesis.entity.spell.chaos.ChaosSwordEntity;
import miku.united_as_one.genesis.entity.spell.MeteorProjectileEntity;
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

    public static final RegistryObject<EntityType<ChaosSwordEntity>> CHAOS_SWORD =
            fastMisc("chaos_sword", ChaosSwordEntity::new, 0.5F, 0.5F, 64);

    public static final RegistryObject<EntityType<ChaosSwordAoeEntity>> CHAOS_SWORD_AOE =
            fastMisc("chaos_sword_aoe", ChaosSwordAoeEntity::new, 4.0F, 0.8F, 64);

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
