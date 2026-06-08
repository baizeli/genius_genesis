package miku.united_as_one.genesis.client.gui.manuscript;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class ChaosSpellLearningScreen extends SpellLearningScreen {
    public ChaosSpellLearningScreen(Component title, InteractionHand activeHand) {
        super(title, activeHand);
    }

    @Override
    protected AbstractSpell getSchoolFilter() {
        return new AbstractSpell() {
            @Override
            public SchoolType getSchoolType() {
                return SpellSchoolRegistry.CHAOS.get();
            }

            @Override
            public ResourceLocation getSpellResource() {
                return Genesis.id("chaos_filter");
            }

            @Override
            public DefaultConfig getDefaultConfig() {
                return new DefaultConfig();
            }

            @Override
            public CastType getCastType() {
                return CastType.INSTANT;
            }
        };
    }

    @Override
    protected boolean isCorrectManuscript() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.getItemInHand(activeHand).is(ItemRegistry.CHAOS_MANUSCRIPT.get());
    }

    @Override
    protected Component getRequiredItemName() {
        return Component.translatable("item." + Genesis.MOD_ID + ".chaos_manuscript");
    }
}
