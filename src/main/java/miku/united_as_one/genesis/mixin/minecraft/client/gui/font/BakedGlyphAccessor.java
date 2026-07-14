package miku.united_as_one.genesis.mixin.minecraft.client.gui.font;

import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BakedGlyph.class)
public interface BakedGlyphAccessor {

    @Accessor("left")
    float genesis$left();

    @Accessor("right")
    float genesis$right();

    @Accessor("up")
    float genesis$up();

    @Accessor("down")
    float genesis$down();

    @Accessor("u0")
    float genesis$u0();

    @Accessor("u1")
    float genesis$u1();

    @Accessor("v0")
    float genesis$v0();

    @Accessor("v1")
    float genesis$v1();
}
