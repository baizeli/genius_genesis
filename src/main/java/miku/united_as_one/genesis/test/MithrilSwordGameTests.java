package miku.united_as_one.genesis.test;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.weapon.MithrilSword;
import miku.united_as_one.genesis.util.SlashColors;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class MithrilSwordGameTests {
    @GameTest(template = "empty", batch = Genesis.MOD_ID)
    public static void mithrilSwordFifthHitSettings(GameTestHelper helper) {
        helper.assertTrue(MithrilSword.slashColor() == SlashColors.MITHRIL_LIGHT_BLUE,
                "Mithril sword effect color must stay light blue.");
        helper.assertTrue(MithrilSword.hitsPerSpecial() == 5,
                "Mithril sword special effect must trigger on the fifth hit.");
        helper.succeed();
    }
}
