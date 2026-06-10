package miku.united_as_one.genesis.mixin.minecraft.client.gui;

import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextColor.class)
public interface TextColorAccessor {

    @Accessor("name")
    String genesis$name();
}
