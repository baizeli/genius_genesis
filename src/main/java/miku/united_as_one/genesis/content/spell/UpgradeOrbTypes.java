package miku.united_as_one.genesis.content.spell;

import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class UpgradeOrbTypes {
    public static final ResourceKey<Registry<UpgradeOrbType>> KEY = UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY;

    // 混沌法强+1
    public static final ResourceKey<UpgradeOrbType> CHAOS_SPELL_POWER = key("chaos_power");
    // 星源法强+1
    public static final ResourceKey<UpgradeOrbType> CELESTIAL_SOURCE_SPELL_POWER = key("celestial_source_power");
    // 远古巫术法强+1
    public static final ResourceKey<UpgradeOrbType> ELDRITCH_SPELL_POWER = key("eldritch_power");
    // 烈焰穿透+1
    public static final ResourceKey<UpgradeOrbType> FIRE_SPELL_PENETRATION = key("fire_spell_penetration");
    // 神圣穿透+1
    public static final ResourceKey<UpgradeOrbType> HOLY_SPELL_PENETRATION = key("holy_spell_penetration");
    // 冰霜穿透+1
    public static final ResourceKey<UpgradeOrbType> ICE_SPELL_PENETRATION = key("ice_spell_penetration");
    // 猩红穿透+1
    public static final ResourceKey<UpgradeOrbType> BLOOD_SPELL_PENETRATION = key("blood_spell_penetration");
    // 末影穿透+1
    public static final ResourceKey<UpgradeOrbType> ENDER_SPELL_PENETRATION = key("ender_spell_penetration");
    // 雷霆穿透+1
    public static final ResourceKey<UpgradeOrbType> THUNDER_SPELL_PENETRATION = key("thunder_spell_penetration");
    // 自然穿透+1
    public static final ResourceKey<UpgradeOrbType> NATURE_SPELL_PENETRATION = key("nature_spell_penetration");
    // 邪术穿透+1
    public static final ResourceKey<UpgradeOrbType> ELDRITCH_SPELL_PENETRATION = key("eldritch_spell_penetration");
    // 混沌穿透+1
    public static final ResourceKey<UpgradeOrbType> CHAOS_SPELL_PENETRATION = key("chaos_spell_penetration");
    // 星源穿透+1
    public static final ResourceKey<UpgradeOrbType> CELESTIAL_SOURCE_SPELL_PENETRATION = key("celestial_source_spell_penetration");

    private UpgradeOrbTypes() {
    }

    private static ResourceKey<UpgradeOrbType> key(String id) {
        return ResourceKey.create(KEY, Genesis.rl(id));
    }
}
