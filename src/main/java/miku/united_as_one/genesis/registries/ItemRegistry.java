package miku.united_as_one.genesis.registries;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import miku.bai_ze_li.genesis.api.item.GenesisGoldTooltipParticleItem;
import miku.bai_ze_li.genesis.api.item.GenesisPurpleTooltipParticleItem;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.FireBossDagger;
import miku.united_as_one.genesis.item.FlyingSwallowThroughWillow;
import miku.united_as_one.genesis.item.GenesisArmorMaterials;
import miku.united_as_one.genesis.item.GenesisTiers;
import miku.united_as_one.genesis.item.InfiniteShrivingStoneItem;
import miku.united_as_one.genesis.item.armor.GenesisGeoArmorItem;
import miku.united_as_one.genesis.item.tool.DivineMetalAxe;
import miku.united_as_one.genesis.item.tool.DivineMetalHoe;
import miku.united_as_one.genesis.item.tool.DivineMetalPickaxe;
import miku.united_as_one.genesis.item.tool.DivineMetalShovel;
import miku.united_as_one.genesis.item.tool.VioletAxe;
import miku.united_as_one.genesis.item.tool.VioletHoe;
import miku.united_as_one.genesis.item.tool.VioletPickaxe;
import miku.united_as_one.genesis.item.tool.VioletShovel;
import miku.united_as_one.genesis.item.weapon.MithrilSword;
import miku.united_as_one.genesis.item.spell.CelestialSourceSpellBook;
import miku.united_as_one.genesis.item.spell.CelestialSourceStaff;
import miku.united_as_one.genesis.item.spell.ChaosSpellBook;
import miku.united_as_one.genesis.item.spell.ChaosStaff;
import miku.united_as_one.genesis.item.spell.DiskSpellBook;
import miku.united_as_one.genesis.item.spell.LightningSpellBook;
import miku.united_as_one.genesis.spell.UpgradeOrbTypes;
import miku.united_as_one.genesis.item.weapon.bow.FlameBow;
import miku.united_as_one.genesis.item.weapon.bow.FrostLongBow;
import miku.united_as_one.genesis.item.weapon.bow.ThunderLongBow;
import miku.united_as_one.genesis.item.weapon.bow.WitchcraftBow;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public final class ItemRegistry {

    public static final ItemEntry<GenesisPurpleTooltipParticleItem> VIOLET_GALAXY_INGOT = item("violet_galaxy_ingot", GenesisPurpleTooltipParticleItem::new, epicProps(), CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 绱瀬纰庣墖
    public static final ItemEntry<Item> VIOLET_FRAGMENTS = epic("violet_fragments", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 鐮寸伃绁炴牸
    public static final ItemEntry<Item> DESTROY_GODHEAD = epic("destroy_godhead", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<GenesisGoldTooltipParticleItem> DIVINE_METAL_INGOT = item("divine_metal_ingot", GenesisGoldTooltipParticleItem::new, epicProps(), CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 绁炲湥閲戝睘纰庣墖
    public static final ItemEntry<Item> DIVINE_METAL_FRAGMENT = epic("divine_metal_fragment", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> TWISTED_CHAOS_INGOT = epic("twisted_chaos_ingot", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> TWISTED_CHAOS = epic("twisted_chaos", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 鏄熸簮鐝嶇彔
    public static final ItemEntry<Item> CELESTIAL_SOURCE_PEARL = epic("celestial_source_pearl", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CELESTIAL_SOURCE_INGOT = epic("celestial_source_ingot", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CREATE_STAR = simple("create_star", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // Good Cake
    public static final ItemEntry<Item> GOOD_CAKE = simple("good_cake", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CRYSTAL_FRUIT = simple("crystal_fruit", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> PHANTOM_PLUM = simple("phantom_plum", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 褰╄壊鏋滃疄
    public static final ItemEntry<Item> COLORFUL_FRUITS = simple("colorful_fruits", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 楂樼骇娉曠悆
    public static final ItemEntry<Item> UPGRADE_ORB_PRO = epic16("upgrade_orb_pro", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);

    // 榄旀硶姘存櫠
    public static final ItemEntry<Item> ARCANE_CRYSTAL = epic16("arcane_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> BLOOD_CRYSTAL = epic16("blood_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> ELDRITCH_CRYSTAL = epic16("eldritch_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> ENDER_CRYSTAL = epic16("ender_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> EVOCATION_CRYSTAL = epic16("evocation_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> FIRE_CRYSTAL = epic16("fire_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> HOLY_CRYSTAL = epic16("holy_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> ICE_CRYSTAL = epic16("ice_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> LIGHTNING_CRYSTAL = epic16("lightning_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> NATURE_CRYSTAL = epic16("nature_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CHAOS_CRYSTAL = epic16("chaos_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CELESTIAL_SOURCE_CRYSTAL = epic16("celestial_source_crystal", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);

    // 琛€鑲夌伒榄傞搩
    public static final ItemEntry<Item> FLESH_SOUL_BELL = epic("flesh_soul_bell", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 娣锋矊鍘熸牳
    public static final ItemEntry<Item> CHAOS_CORE = epic("chaos_core", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> FLESH_SOUL_FRAGMENT = epic("flesh_soul_fragment", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    // 鏄熷皹锛屾棫璇█鏂囦欢鏆傛棤鏉＄洰
    public static final ItemEntry<Item> STELLAR_DUST = noModel("stellar_dust", Item::new, new Item.Properties(), CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);

    public static final ItemEntry<SwordItem> LAEVATAIN = sword("laevatain", GenesisTiers.DIVINE_METAL, 3, -2.4F);
    // 鍐堟牸灏煎皵
    public static final ItemEntry<SwordItem> GUNGNIR = sword("gungnir", GenesisTiers.LEGENDARY, 2, -1.9F);
    public static final ItemEntry<MithrilSword> MITHRIL_SWORD = mithrilSword();
    // 绁炲湥閲戝睘宸ュ叿
    public static final ItemEntry<SwordItem> DIVINE_METAL_SWORD = sword("divine_metal_sword", GenesisTiers.DIVINE_METAL, 3, -2.4F);
    public static final ItemEntry<DivineMetalAxe> DIVINE_METAL_AXE = axe("divine_metal_axe", properties -> new DivineMetalAxe(GenesisTiers.DIVINE_METAL, 5, -3.0F, properties));
    public static final ItemEntry<DivineMetalPickaxe> DIVINE_METAL_PICKAXE = pickaxe("divine_metal_pickaxe", properties -> new DivineMetalPickaxe(GenesisTiers.DIVINE_METAL, 1, -2.8F, properties), epicProps());
    public static final ItemEntry<DivineMetalShovel> DIVINE_METAL_SHOVEL = shovel("divine_metal_shovel", properties -> new DivineMetalShovel(GenesisTiers.DIVINE_METAL, 1.5F, -3.0F, properties));
    public static final ItemEntry<DivineMetalHoe> DIVINE_METAL_HOE = hoe("divine_metal_hoe", properties -> new DivineMetalHoe(GenesisTiers.DIVINE_METAL, -4, 0.0F, properties));
    public static final ItemEntry<PickaxeItem> MITHRIL_PICKAXE = pickaxe("mithril_pickaxe", GenesisTiers.MITHRIL, rareProps());
    // 绱瀬宸ュ叿
    public static final ItemEntry<SwordItem> VIOLET_SWORD = sword("violet_sword", GenesisTiers.VIOLET, 3, -2.4F);
    public static final ItemEntry<VioletAxe> VIOLET_AXE = axe("violet_axe", properties -> new VioletAxe(GenesisTiers.VIOLET, 5, -3.0F, properties));
    public static final ItemEntry<VioletPickaxe> VIOLET_PICKAXE = pickaxe("violet_pickaxe", properties -> new VioletPickaxe(GenesisTiers.VIOLET, 1, -2.8F, properties), epicProps());
    public static final ItemEntry<VioletShovel> VIOLET_SHOVEL = shovel("violet_shovel", properties -> new VioletShovel(GenesisTiers.VIOLET, 1.5F, -3.0F, properties));
    public static final ItemEntry<VioletHoe> VIOLET_HOE = hoe("violet_hoe", properties -> new VioletHoe(GenesisTiers.VIOLET, -4, 0.0F, properties));
    // 鍙楃伀鑰呯殑鍖曢
    public static final ItemEntry<FireBossDagger> FIRE_BOSS_DAGGER = item("fire_boss_dagger", properties -> new FireBossDagger(GenesisTiers.DAGGER, 0, -2.4F, properties), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    // 鍙楄鑰呯殑鍖曢
    public static final ItemEntry<ThunderLongBow> THUNDER_LONGBOW = bow("thunder_longbow", ThunderLongBow::new);
    public static final ItemEntry<FrostLongBow> FROST_LONGBOW = bow("frost_longbow", FrostLongBow::new);
    public static final ItemEntry<WitchcraftBow> WITCHCRAFT_BOW = bow("witchcraft_bow", WitchcraftBow::new);
    public static final ItemEntry<FlameBow> FLAME_BOW = bow("flame_bow", FlameBow::new);
    // 椋炵嚂绌挎煶
    public static final ItemEntry<FlyingSwallowThroughWillow> FLYING_SWALLOW_THROUGH_WILLOW = noModel("flying_swallow_through_willow", properties -> new FlyingSwallowThroughWillow(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);

    // 绁炲湥閲戝睘鎶ょ敳
    public static final ItemEntry<GenesisGeoArmorItem> DIVINE_METAL_HELMET = armor("divine_metal_helmet", GenesisArmorMaterials.DIVINE_METAL, ArmorItem.Type.HELMET);
    public static final ItemEntry<GenesisGeoArmorItem> DIVINE_METAL_CHESTPLATE = armor("divine_metal_chestplate", GenesisArmorMaterials.DIVINE_METAL, ArmorItem.Type.CHESTPLATE);
    public static final ItemEntry<GenesisGeoArmorItem> DIVINE_METAL_LEGGINGS = armor("divine_metal_leggings", GenesisArmorMaterials.DIVINE_METAL, ArmorItem.Type.LEGGINGS);
    public static final ItemEntry<GenesisGeoArmorItem> DIVINE_METAL_BOOTS = armor("divine_metal_boots", GenesisArmorMaterials.DIVINE_METAL, ArmorItem.Type.BOOTS);
    // 娉曟湳鎶ょ敳
    public static final ItemEntry<GenesisGeoArmorItem> CELESTIAL_SOURCE_SPELL_HELMET = armor("celestial_source_spell_helmet", GenesisArmorMaterials.CELESTIAL_SOURCE_SPELL, ArmorItem.Type.HELMET);
    public static final ItemEntry<GenesisGeoArmorItem> CELESTIAL_SOURCE_SPELL_CHESTPLATE = armor("celestial_source_spell_chestplate", GenesisArmorMaterials.CELESTIAL_SOURCE_SPELL, ArmorItem.Type.CHESTPLATE);
    public static final ItemEntry<GenesisGeoArmorItem> CELESTIAL_SOURCE_SPELL_LEGGINGS = armor("celestial_source_spell_leggings", GenesisArmorMaterials.CELESTIAL_SOURCE_SPELL, ArmorItem.Type.LEGGINGS);
    public static final ItemEntry<GenesisGeoArmorItem> CELESTIAL_SOURCE_SPELL_BOOTS = armor("celestial_source_spell_boots", GenesisArmorMaterials.CELESTIAL_SOURCE_SPELL, ArmorItem.Type.BOOTS);
    public static final ItemEntry<GenesisGeoArmorItem> CHAOS_SPELL_HELMET = armor("chaos_spell_helmet", GenesisArmorMaterials.CHAOS_SPELL, ArmorItem.Type.HELMET);
    public static final ItemEntry<GenesisGeoArmorItem> CHAOS_SPELL_CHESTPLATE = armor("chaos_spell_chestplate", GenesisArmorMaterials.CHAOS_SPELL, ArmorItem.Type.CHESTPLATE);
    public static final ItemEntry<GenesisGeoArmorItem> CHAOS_SPELL_LEGGINGS = armor("chaos_spell_leggings", GenesisArmorMaterials.CHAOS_SPELL, ArmorItem.Type.LEGGINGS);
    public static final ItemEntry<GenesisGeoArmorItem> CHAOS_SPELL_BOOTS = armor("chaos_spell_boots", GenesisArmorMaterials.CHAOS_SPELL, ArmorItem.Type.BOOTS);
    public static final ItemEntry<GenesisGeoArmorItem> VIOLET_ZENITH_HELMET = armor("violet_zenith_helmet", GenesisArmorMaterials.VIOLET_ZENITH, ArmorItem.Type.HELMET);
    public static final ItemEntry<GenesisGeoArmorItem> VIOLET_ZENITH_CHESTPLATE = armor("violet_zenith_chestplate", GenesisArmorMaterials.VIOLET_ZENITH, ArmorItem.Type.CHESTPLATE);
    public static final ItemEntry<GenesisGeoArmorItem> VIOLET_ZENITH_LEGGINGS = armor("violet_zenith_leggings", GenesisArmorMaterials.VIOLET_ZENITH, ArmorItem.Type.LEGGINGS);
    public static final ItemEntry<GenesisGeoArmorItem> VIOLET_ZENITH_BOOTS = armor("violet_zenith_boots", GenesisArmorMaterials.VIOLET_ZENITH, ArmorItem.Type.BOOTS);
    public static final ItemEntry<GenesisGeoArmorItem> ARCANE_CRYSTAL_HELMET = armor("arcane_crystal_helmet", GenesisArmorMaterials.ARCANE_CRYSTAL, ArmorItem.Type.HELMET);
    public static final ItemEntry<GenesisGeoArmorItem> ARCANE_CRYSTAL_CHESTPLATE = armor("arcane_crystal_chestplate", GenesisArmorMaterials.ARCANE_CRYSTAL, ArmorItem.Type.CHESTPLATE);
    public static final ItemEntry<GenesisGeoArmorItem> ARCANE_CRYSTAL_LEGGINGS = armor("arcane_crystal_leggings", GenesisArmorMaterials.ARCANE_CRYSTAL, ArmorItem.Type.LEGGINGS);
    public static final ItemEntry<GenesisGeoArmorItem> ARCANE_CRYSTAL_BOOTS = armor("arcane_crystal_boots", GenesisArmorMaterials.ARCANE_CRYSTAL, ArmorItem.Type.BOOTS);

    public static final ItemEntry<Item> LAO_WANG_237 = epic("lao_wang_237", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> GENESIS_CURSE = noModel("genesis_curse", Item::new, epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<InfiniteShrivingStoneItem> INFINITE_SHRIVING_STONE = item("infinite_shriving_stone", properties -> new InfiniteShrivingStoneItem(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> ETERNAL_RING = epic("eternal_ring", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> LIGHTNING_RUNE_PLUS = epic("lightning_rune_plus", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> NATURE_RUNE_PLUS = epic("nature_rune_plus", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> ENDER_RUNE_PLUS = epic("ender_rune_plus", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> HOLY_RUNE_PLUS = epic("holy_rune_plus", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> ICE_RUNE_PLUS = epic("ice_rune_plus", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> BLOOD_RUNE_PLUS = epic("blood_rune_plus", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> FIRE_RUNE_PLUS = epic("fire_rune_plus", CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<Item> ELDRITCH_RUNE_PLUS = noModel("eldritch_rune_plus", Item::new, epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);

    // 娉曟湳涔︿笌娉曟潠
    public static final ItemEntry<ChaosSpellBook> CHAOS_SPELL_BOOK = noModel("chaos_spell_book", properties -> new ChaosSpellBook(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<CelestialSourceSpellBook> CELESTIAL_SOURCE_SPELL_BOOK = noModel("celestial_source_spell_book", properties -> new CelestialSourceSpellBook(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<LightningSpellBook> LIGHTNING_SPELL_BOOK = noModel("lightning_spell_book", properties -> new LightningSpellBook(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<DiskSpellBook> DISK_SPELL_BOOK = item("disk_spell_book", properties -> new DiskSpellBook(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<ChaosStaff> CHAOS_STAFF = noModel("chaos_staff", properties -> new ChaosStaff(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    public static final ItemEntry<CelestialSourceStaff> CELESTIAL_SOURCE_STAFF = noModel("celestial_source_staff", properties -> new CelestialSourceStaff(), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);

    // 绗︽枃銆佹硶鐞冧笌鎵嬬
    public static final ItemEntry<Item> CHAOS_RUNE = simple("chaos_rune", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CELESTIAL_SOURCE_RUNE = simple("celestial_source_rune", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> ELDRITCH_RUNE = simple("eldritch_rune", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> BLASPHEMY_KEY = simple("blasphemy_key", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<UpgradeOrbItem> CHAOS_UPGRADE_ORB = upgradeOrb("chaos_upgrade_orb", UpgradeOrbTypes.CHAOS_SPELL_POWER);
    public static final ItemEntry<UpgradeOrbItem> CELESTIAL_SOURCE_UPGRADE_ORB = upgradeOrb("celestial_source_upgrade_orb", UpgradeOrbTypes.CELESTIAL_SOURCE_SPELL_POWER);
    public static final ItemEntry<UpgradeOrbItem> ELDRITCH_UPGRADE_ORB = upgradeOrb("eldritch_upgrade_orb", UpgradeOrbTypes.ELDRITCH_SPELL_POWER);
    public static final ItemEntry<UpgradeOrbItem> FIRE_ORB_PRO = upgradeOrb("fire_orb_pro", UpgradeOrbTypes.FIRE_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> HOLY_ORB_PRO = upgradeOrb("holy_orb_pro", UpgradeOrbTypes.HOLY_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> ICE_ORB_PRO = upgradeOrb("ice_orb_pro", UpgradeOrbTypes.ICE_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> BLOOD_ORB_PRO = upgradeOrb("blood_orb_pro", UpgradeOrbTypes.BLOOD_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> ENDER_ORB_PRO = upgradeOrb("ender_orb_pro", UpgradeOrbTypes.ENDER_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> THUNDER_ORB_PRO = upgradeOrb("thunder_orb_pro", UpgradeOrbTypes.THUNDER_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> NATURE_ORB_PRO = upgradeOrb("nature_orb_pro", UpgradeOrbTypes.NATURE_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> ELDRITCH_ORB_PRO = upgradeOrb("eldritch_orb_pro", UpgradeOrbTypes.ELDRITCH_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> CHAOS_ORB_PRO = upgradeOrb("chaos_orb_pro", UpgradeOrbTypes.CHAOS_SPELL_PENETRATION);
    public static final ItemEntry<UpgradeOrbItem> CELESTIAL_SOURCE_ORB_PRO = upgradeOrb("celestial_source_orb_pro", UpgradeOrbTypes.CELESTIAL_SOURCE_SPELL_PENETRATION);
    public static final ItemEntry<Item> CHAOS_MANUSCRIPT = epic("chaos_manuscript", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CELESTIAL_SOURCE_MANUSCRIPT = epic("celestial_source_manuscript", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> CHAOS_MANUSCRIPT_FRAGMENT = epic("chaos_manuscript_fragment", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> BLANK_CELESTIAL_SOURCE_MANUSCRIPT = epic("blank_celestial_source_manuscript", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> VIOLET_UPGRADE_SMITHING_TEMPLATE = epic("violet_upgrade_smithing_template", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);
    public static final ItemEntry<Item> DIVINE_UPGRADE_SMITHING_TEMPLATE = epic("divine_upgrade_smithing_template", CreativeTabRegistry.GENIUS_GENESIS_MATERIAL);

    private ItemRegistry() {
    }

    public static void register() {
    }

    private static ItemEntry<Item> simple(String id, ResourceKey<CreativeModeTab> tab) {
        return item(id, Item::new, new Item.Properties(), tab);
    }

    private static ItemEntry<Item> epic(String id, ResourceKey<CreativeModeTab> tab) {
        return item(id, Item::new, epicProps(), tab);
    }

    private static ItemEntry<Item> epic16(String id, ResourceKey<CreativeModeTab> tab) {
        return item(id, Item::new, epicProps().stacksTo(16), tab);
    }

    private static ItemEntry<SwordItem> sword(String id, GenesisTiers tier, int damage, float speed) {
        return item(id, properties -> new SwordItem(tier, damage, speed, properties), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static ItemEntry<MithrilSword> mithrilSword() {
        return Genesis.L2_REGISTRATE
                .item("mithril_sword", properties -> new MithrilSword(GenesisTiers.MITHRIL, 3, -2.4F, properties))
                .initialProperties(ItemRegistry::epicProps)
                .setData(ProviderType.ITEM_MODEL, (ctx, prov) -> {
                })
                .tab(CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT)
                .register();
    }

    private static ItemEntry<AxeItem> axe(String id, GenesisTiers tier, float damage, float speed) {
        return item(id, properties -> new AxeItem(tier, damage, speed, properties), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static <T extends AxeItem> ItemEntry<T> axe(String id, ItemFactory<T> factory) {
        return item(id, factory, epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static ItemEntry<PickaxeItem> pickaxe(String id, GenesisTiers tier, Item.Properties properties) {
        return item(id, itemProperties -> new PickaxeItem(tier, 1, -2.8F, itemProperties), properties, CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static <T extends PickaxeItem> ItemEntry<T> pickaxe(String id, ItemFactory<T> factory, Item.Properties properties) {
        return item(id, factory, properties, CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static ItemEntry<ShovelItem> shovel(String id, GenesisTiers tier) {
        return item(id, properties -> new ShovelItem(tier, 1.5F, -3.0F, properties), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static <T extends ShovelItem> ItemEntry<T> shovel(String id, ItemFactory<T> factory) {
        return item(id, factory, epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static ItemEntry<HoeItem> hoe(String id, GenesisTiers tier) {
        return item(id, properties -> new HoeItem(tier, -4, 0.0F, properties), epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static <T extends HoeItem> ItemEntry<T> hoe(String id, ItemFactory<T> factory) {
        return item(id, factory, epicProps(), CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT);
    }

    private static <T extends BowItem> ItemEntry<T> bow(String id, ItemFactory<T> factory) {
        return Genesis.L2_REGISTRATE
                .item(id, factory::create)
                .initialProperties(() -> epicProps().stacksTo(1).durability(768))
                .model(ItemRegistry::createBowModel)
                .tab(CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT)
                .register();
    }

    private static ItemEntry<GenesisGeoArmorItem> armor(String id, GenesisArmorMaterials material, ArmorItem.Type type) {
        return Genesis.L2_REGISTRATE
                .item(id, properties -> new GenesisGeoArmorItem(material, type, properties))
                .initialProperties(ItemRegistry::epicProps)
                .setData(ProviderType.ITEM_MODEL, ItemRegistry::createArmorModel)
                .tab(CreativeTabRegistry.GENIUS_GENESIS_EQUIPMENT)
                .register();
    }

    private static ItemEntry<UpgradeOrbItem> upgradeOrb(String id, ResourceKey<UpgradeOrbType> type) {
        return Genesis.L2_REGISTRATE
                .item(id, properties -> new UpgradeOrbItem(ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON), type))
                .tab(CreativeTabRegistry.GENIUS_GENESIS_MATERIAL)
                .register();
    }

    private static <T extends Item> ItemEntry<T> item(String id, ItemFactory<T> factory, Item.Properties properties, ResourceKey<CreativeModeTab> tab) {
        return Genesis.L2_REGISTRATE
                .item(id, factory::create)
                .initialProperties(() -> properties)
                .tab(tab)
                .register();
    }

    private static <T extends Item> ItemEntry<T> noModel(String id, ItemFactory<T> factory, Item.Properties properties, ResourceKey<CreativeModeTab> tab) {
        return Genesis.L2_REGISTRATE
                .item(id, factory::create)
                .initialProperties(() -> properties)
                .setData(ProviderType.ITEM_MODEL, (ctx, prov) -> {
                })
                .tab(tab)
                .register();
    }

    public static void createArmorModel(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelProvider pvd) {
        pvd.withExistingParent(ctx.getName(), "item/generated")
                .texture("layer0", Genesis.MOD_ID + ":item/armor/" + ctx.getName());
    }

    public static <T extends Item> void createBowModel(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd) {
        ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "item/handheld")
                .texture("layer0", Genesis.MOD_ID + ":item/bow/" + ctx.getName() + "/bow");

        for (int i = 0; i < 3; i++) {
            String name = "item/bow/" + ctx.getName() + "/bow_pulling_" + i;
            pvd.getBuilder(name)
                    .parent(new ModelFile.UncheckedModelFile("minecraft:item/bow_pulling_" + i))
                    .texture("layer0", Genesis.MOD_ID + ":item/bow/" + ctx.getName() + "/bow_pulling_" + i);

            ItemModelBuilder.OverrideBuilder override = builder.override()
                    .predicate(new ResourceLocation("pulling"), 1);
            if (i == 1) {
                override.predicate(new ResourceLocation("pull"), 0.7F);
            } else if (i == 2) {
                override.predicate(new ResourceLocation("pull"), 0.9F);
            }
            override.model(new ModelFile.UncheckedModelFile(Genesis.MOD_ID + ":" + name));
        }
    }

    private static Item.Properties rareProps() {
        return new Item.Properties().rarity(Rarity.RARE).fireResistant();
    }

    private static Item.Properties epicProps() {
        return new Item.Properties().rarity(Rarity.EPIC).fireResistant();
    }

    @FunctionalInterface
    private interface ItemFactory<T extends Item> {
        T create(Item.Properties properties);
    }
}
