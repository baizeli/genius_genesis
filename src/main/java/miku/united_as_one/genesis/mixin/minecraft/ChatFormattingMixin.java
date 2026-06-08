package miku.united_as_one.genesis.mixin.minecraft;

import miku.united_as_one.genesis.registries.text.Formatting;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ChatFormatting.class)
public class ChatFormattingMixin {
    @Shadow @Final @Mutable
    private static ChatFormatting[] $VALUES;

    @SuppressWarnings("unused")
    private ChatFormattingMixin(String name, int ordinal, String nameIn, char code, int colorIndex, Integer colorValue) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        int base = $VALUES.length;
        $VALUES = Arrays.copyOf($VALUES, base + 2);

        ChatFormatting blood = (ChatFormatting)(Object) new ChatFormattingMixin("BLOOD_WAVE", base, "BLOOD_WAVE", 'v', 12, 0xFFFFFF);
        Formatting.BLOOD_WAVE = blood;
        $VALUES[base] = blood;

        ChatFormatting celestial = (ChatFormatting)(Object) new ChatFormattingMixin("CELESTIAL_WAVE", base + 1, "CELESTIAL_WAVE", 'w', 13, 0xFFFFFF);
        Formatting.CELESTIAL_WAVE = celestial;
        $VALUES[base + 1] = celestial;
    }
}