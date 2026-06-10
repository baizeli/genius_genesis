package miku.united_as_one.genesis.mixin.minecraft.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Font.class)
public interface FontAccessor {

    @Accessor("SHADOW_OFFSET")
    static Vector3f genesis$getShadowOffset() {
        throw new AssertionError();
    }

    @Accessor("filterFishyGlyphs")
    boolean genesis$filterFishyGlyphs();

    @Invoker("adjustColor")
    static int genesis$adjustColor(int color) {
        throw new AssertionError();
    }

    @Invoker("getFontSet")
    FontSet genesis$getFontSet(ResourceLocation location);
}
