package miku.united_as_one.genesis.test;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.weapon.MithrilSword;
import miku.united_as_one.genesis.util.SlashColors;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class MithrilSwordGameTests {
    @GameTest(template = "empty", batch = Genesis.MOD_ID)
    public static void mithrilSwordFinalComboSettings(GameTestHelper helper) {
        helper.assertTrue(MithrilSword.slashColor() == SlashColors.MITHRIL_LIGHT_BLUE,
                "Mithril sword effect color must stay light blue.");
        helper.assertTrue(MithrilSword.finalPrimaryDamageMultiplier() == 1.5F,
                "Mithril sword final primary slash must use 1.5x damage.");
        helper.assertTrue(MithrilSword.finalSecondaryDamageMultiplier() == 1.2F,
                "Mithril sword final secondary slash must use 1.2x damage.");
        helper.assertTrue(MithrilSword.aoeRadius() == 1.75F,
                "Mithril sword final combo AOE radius must be halved.");
        helper.succeed();
    }
}
