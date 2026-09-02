package miku.united_as_one.genesis.test;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.weapon.Laevatain;
import miku.united_as_one.genesis.combat.autoswing.SwingPipeline;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class MithrilSwordGameTests {
    @GameTest(template = "empty", batch = Genesis.MOD_ID)
    public static void laevatainFinalComboSettings(GameTestHelper helper) {
        helper.assertTrue(Laevatain.finalPrimaryDamageMultiplier() == 1.5F,
                "Laevatain final primary slash must use 1.5x damage.");
        helper.assertTrue(Laevatain.finalSecondaryDamageMultiplier() == 1.2F,
                "Laevatain final secondary slash must use 1.2x damage.");
        helper.assertTrue(Laevatain.aoeRadius() == 1.75F,
                "Laevatain final combo AOE radius must be 1.75 blocks.");
        helper.assertTrue(Laevatain.swingPipeline().inputMode == SwingPipeline.InputMode.USE_HOLD,
                "Laevatain combo must be driven by holding use.");
        helper.assertTrue(Laevatain.swingPipeline().completionCooldownTicks == 60,
                "Laevatain combo must apply a 60 tick completion cooldown.");
        helper.assertTrue(Laevatain.swingPipeline().endMode == SwingPipeline.EndMode.STOP,
                "Laevatain combo must stop after one sequence until use is released.");
        helper.succeed();
    }
}
