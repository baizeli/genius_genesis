package miku.united_as_one.genesis.client.spellhud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.CooldownInstance;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.gui.overlays.SpellBarOverlay;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SpellCardHudOverlay {
    static final int CARD_SIZE = 22;
    private static final int ICON_SIZE = 16;
    private static final int EDGE_MARGIN = 13;
    private static final int COLLAPSED_CARD_COUNT = 2;
    private static final float COLLAPSED_STEP = 6.0F;
    private static final float EXPANDED_STEP = 24.0F;
    private static final long SHIFT_MILLIS = 180L;
    private static final long WHEEL_INSERT_MILLIS = 300L;
    private static final long SPREAD_HOLD_MILLIS = 620L;
    private static final long SPREAD_CLOSE_MILLIS = 180L;

    private static int selectedIndex = Integer.MIN_VALUE;
    private static int previousIndex = Integer.MIN_VALUE;
    private static int spellCount;
    private static long transitionStartedAt = Long.MIN_VALUE;
    private static SpellCardAnimationState.SelectionSource transitionSource =
            SpellCardAnimationState.SelectionSource.UNKNOWN;
    private static int transitionDirection = 1;

    public static final IGuiOverlay OVERLAY = (forgeGui, graphics, partialTick,
                                               screenWidth, screenHeight) ->
            render(graphics, screenWidth, screenHeight);

    private SpellCardHudOverlay() {
    }

    public static void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
        if (!SpellCardHudClientEvents.shouldRenderReplacement()) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (minecraft.options.hideGui || player == null || player.isSpectator()) {
            resetTransientState();
            return;
        }
        float referenceScale = SpellCardHudState.referenceScale(
                minecraft.getWindow().getGuiScale());
        int referenceWidth = SpellCardHudState.referenceViewport(screenWidth, referenceScale);
        int referenceHeight = SpellCardHudState.referenceViewport(screenHeight, referenceScale);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(referenceScale, referenceScale, 1.0F);
        try {
            renderAtReferenceScale(graphics, referenceWidth, referenceHeight,
                    screenWidth, screenHeight, player);
        } finally {
            pose.popPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void renderAtReferenceScale(GuiGraphics graphics,
                                               int screenWidth, int screenHeight,
                                               int scissorWidth, int scissorHeight,
                                               LocalPlayer player) {
        SpellSelectionManager manager = ClientMagicData.getSpellSelectionManager();
        List<SpellSelectionManager.SelectionOption> spells = manager.getAllSpells();
        if (spells.isEmpty()) {
            resetTransientState();
            return;
        }
        int current = Mth.clamp(manager.getGlobalSelectionIndex(), 0, spells.size() - 1);
        long now = Util.getMillis();
        updateSelection(current, spells.size(), now);
        List<SpellCardEffectEngine.CardInput> inputs = new ArrayList<>(spells.size());
        for (var option : spells) {
            AbstractSpell spell = option.spellData.getSpell();
            inputs.add(new SpellCardEffectEngine.CardInput(spell.getSpellId(),
                    SpellSchoolColors.colorFor(spell.getSchoolType().getId().toString()),
                    ClientMagicData.getCooldownPercent(spell)));
        }
        SpellCardEffectSnapshot effects = SpellCardHudClientEvents.effects().update(
                new SpellCardEffectEngine.FrameInput(now,
                        SpellCardHudClientEvents.isEnabled(), current,
                        ClientMagicData.isCasting(), ClientMagicData.getCastCompletionPercent(), inputs));
        if (effects.hudVisibility() <= 0.0001F) return;

        int x = SpellCardHudState.cardX(screenWidth, CARD_SIZE, EDGE_MARGIN);
        float centerY = screenHeight * 0.5F - CARD_SIZE * 0.5F;
        float spread = spreadProgress(now);
        float offset = transitionOffset(now);
        var casting = SpellCardHudClientEvents.animation().castingTransform(
                ClientMagicData.isCasting(), now);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(effects.horizontalOffset(), 0.0F, 0.0F);
        graphics.enableScissor(0, 0, scissorWidth, scissorHeight);
        try {
            drawAfterimages(graphics, spells, effects, x, centerY);
            int maximumDistance = spells.size() / 2;
            for (int distance = maximumDistance; distance >= 1; distance--) {
                int lower = Math.floorMod(current + distance, spells.size());
                int upper = Math.floorMod(current - distance, spells.size());
                drawPositionedCard(graphics, spells.get(lower), effects.cards().get(lower),
                        lower, current, spells.size(), x, centerY, spread, offset,
                        screenHeight, false, casting, effects, now);
                if (upper != lower) {
                    drawPositionedCard(graphics, spells.get(upper), effects.cards().get(upper),
                            upper, current, spells.size(), x, centerY, spread, offset,
                            screenHeight, false, casting, effects, now);
                }
            }
            drawPositionedCard(graphics, spells.get(current), effects.cards().get(current),
                    current, current, spells.size(), x, centerY, spread, offset,
                    screenHeight, true, casting, effects, now);
        } finally {
            graphics.disableScissor();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            pose.popPose();
        }
        pose.pushPose();
        pose.translate(effects.horizontalOffset(), 0.0F, 0.0F);
        try {
            drawPersistentInfo(graphics, x, centerY, spells.get(current).spellData,
                    player, current, spells.size(), effects.hudVisibility());
        } finally {
            pose.popPose();
        }
    }

    private static void updateSelection(int current, int count, long now) {
        if (selectedIndex == Integer.MIN_VALUE) {
            selectedIndex = current;
            spellCount = count;
            return;
        }
        if (current != selectedIndex) {
            previousIndex = Math.floorMod(selectedIndex, count);
            var transition = SpellCardHudClientEvents.animation().consumeSelection(
                    previousIndex, current, count, now);
            transitionSource = transition.source();
            transitionDirection = transition.direction();
            selectedIndex = current;
            transitionStartedAt = transition.startedAtMillis();
        }
        spellCount = count;
    }

    private static void drawPositionedCard(GuiGraphics graphics,
                                           SpellSelectionManager.SelectionOption option,
                                           SpellCardEffectSnapshot.CardEffect effect,
                                           int index, int current, int count,
                                           int x, float centerY, float spread, float offset,
                                           int screenHeight, boolean selected,
                                           SpellCardAnimationState.CastingTransform casting,
                                           SpellCardEffectSnapshot effects, long now) {
        float relative = signedDistance(index, current, count) + offset;
        float collapsed = Mth.clamp(relative, -COLLAPSED_CARD_COUNT, COLLAPSED_CARD_COUNT);
        float y = Mth.lerp(spread, centerY + collapsed * COLLAPSED_STEP,
                centerY + relative * EXPANDED_STEP);
        if (y + CARD_SIZE < 0.0F || y > screenHeight) return;
        float opacity = effects.hudVisibility() * Mth.lerp(spread,
                Math.abs(relative) <= COLLAPSED_CARD_COUNT + 0.001F ? 1.0F : 0.0F, 1.0F);
        float drawX = x;
        float scale = selected ? 1.10F : 1.0F;
        if (selected && transitionSource == SpellCardAnimationState.SelectionSource.WHEEL) {
            float progress = smootherStep((now - transitionStartedAt) / (float) WHEEL_INSERT_MILLIS);
            drawX -= (float) Math.sin(progress * Math.PI) * 16.0F;
            scale *= 0.96F + progress * 0.04F;
        }
        if (selected && spread > 0.001F) {
            drawSelectionBackdrop(graphics, drawX, y, spread, opacity, effect.color());
        }
        drawCard(graphics, option, effect, drawX, y, scale, opacity, selected, casting);
        if (selected) {
            drawCastProgress(graphics, drawX, y, scale, opacity, effects);
            drawCastBurst(graphics, drawX, y, scale, effects);
        }
    }

    private static void drawCard(GuiGraphics graphics,
                                 SpellSelectionManager.SelectionOption option,
                                 SpellCardEffectSnapshot.CardEffect effect,
                                 float x, float y, float scale, float opacity,
                                 boolean selected,
                                 SpellCardAnimationState.CastingTransform casting) {
        SpellData spellData = option.spellData;
        if (spellData == null || spellData == SpellData.EMPTY || opacity <= 0.001F) return;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + CARD_SIZE * 0.5F, y + CARD_SIZE * 0.5F, selected ? 60.0F : 20.0F);
        pose.scale(scale, scale, 1.0F);
        pose.translate(-CARD_SIZE * 0.5F, -CARD_SIZE * 0.5F, 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        graphics.blit(SpellBarOverlay.TEXTURE, 0, 0, 66, 84, CARD_SIZE, CARD_SIZE);
        if (!selected) {
            int frameU = Curios.SPELLBOOK_SLOT.equals(option.slot) ? 22 : 132;
            graphics.blit(SpellBarOverlay.TEXTURE, 0, 0, frameU, 84, CARD_SIZE, CARD_SIZE);
        }
        graphics.blit(spellData.getSpell().getSpellIconResource(),
                3, 3, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        if (effect.cooldownProgress() > 0.0F) {
            int height = Mth.ceil(ICON_SIZE * effect.cooldownProgress());
            graphics.fill(3, 3 + ICON_SIZE - height, 3 + ICON_SIZE,
                    3 + ICON_SIZE, withAlpha(0xFF070512, 217.0F * opacity));
        }
        drawCooldownShine(graphics, effect, opacity);
        pose.popPose();
        if (selected) {
            drawSelectedFrame(graphics, x, y, scale, opacity, casting, effect.color());
            drawDirectionArrow(graphics, x, y, opacity, effect.color());
        }
    }

    private static void drawCooldownShine(GuiGraphics graphics,
                                          SpellCardEffectSnapshot.CardEffect effect,
                                          float opacity) {
        if (effect.shineAlpha() <= 0.001F) return;
        int center = Math.round(-5.0F + effect.shineProgress() * 26.0F);
        int color = withAlpha(effect.color(), 210.0F * effect.shineAlpha() * opacity);
        for (int column = 0; column < ICON_SIZE; column++) {
            int diagonal = center - column;
            int top = Mth.clamp(diagonal, 0, ICON_SIZE);
            int bottom = Mth.clamp(diagonal + 3, 0, ICON_SIZE);
            if (bottom > top) graphics.fill(3 + column, 3 + top, 4 + column, 3 + bottom, color);
        }
    }

    private static void drawSelectedFrame(GuiGraphics graphics, float x, float y,
                                          float scale, float opacity,
                                          SpellCardAnimationState.CastingTransform casting,
                                          int color) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + CARD_SIZE * 0.5F, y + CARD_SIZE * 0.5F, 62.0F);
        pose.scale(scale * casting.scale(), scale * casting.scale(), 1.0F);
        pose.mulPose(Axis.ZP.rotationDegrees(casting.rotationDegrees()));
        pose.translate(-CARD_SIZE * 0.5F, -CARD_SIZE * 0.5F, 0.0F);
        RenderSystem.setShaderColor(red(color), green(color), blue(color), opacity);
        graphics.blit(SpellBarOverlay.TEXTURE, 0, 0, 0, 84, CARD_SIZE, CARD_SIZE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
    }

    private static void drawSelectionBackdrop(GuiGraphics graphics, float x, float y,
                                              float emphasis, float opacity, int color) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + CARD_SIZE * 0.5F, y + CARD_SIZE * 0.5F, 18.0F);
        graphics.fill(-14, -14, 14, 14, withAlpha(color, 118.0F * emphasis * opacity));
        graphics.fill(-12, -12, 12, 12, withAlpha(0xFF09060F, 220.0F * emphasis * opacity));
        pose.popPose();
    }

    private static void drawAfterimages(GuiGraphics graphics,
                                        List<SpellSelectionManager.SelectionOption> spells,
                                        SpellCardEffectSnapshot effects, float x, float centerY) {
        for (var image : effects.afterimages()) {
            var option = findSpell(spells, image.spellId());
            if (option == null) continue;
            float drift = image.progress() * 15.0F;
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(x - drift, centerY + drift * 0.35F, 12.0F);
            pose.scale(1.10F + image.progress() * 0.16F,
                    1.10F + image.progress() * 0.16F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(red(image.color()), green(image.color()),
                    blue(image.color()), image.alpha() * effects.hudVisibility() * 0.62F);
            graphics.blit(SpellBarOverlay.TEXTURE, 0, 0, 66, 84, CARD_SIZE, CARD_SIZE);
            graphics.blit(option.spellData.getSpell().getSpellIconResource(),
                    3, 3, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            pose.popPose();
        }
    }

    private static SpellSelectionManager.SelectionOption findSpell(
            List<SpellSelectionManager.SelectionOption> spells, String spellId) {
        for (var option : spells) {
            if (option.spellData.getSpell().getSpellId().equals(spellId)) return option;
        }
        return null;
    }

    private static void drawCastProgress(GuiGraphics graphics, float x, float y,
                                         float scale, float opacity,
                                         SpellCardEffectSnapshot effects) {
        if (effects.castProgress() <= 0.001F) return;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + CARD_SIZE * 0.5F, y + CARD_SIZE * 0.5F, 66.0F);
        pose.scale(scale * 1.13F, scale * 1.13F, 1.0F);
        pose.mulPose(Axis.ZP.rotationDegrees(effects.castRotationDegrees()));
        pose.translate(-CARD_SIZE * 0.5F, -CARD_SIZE * 0.5F, 0.0F);
        drawSquareProgress(graphics, effects.castProgress(),
                withAlpha(effects.selectedColor(), 235.0F * opacity));
        pose.popPose();
    }

    private static void drawSquareProgress(GuiGraphics graphics, float progress, int color) {
        int pixels = Mth.clamp(Math.round(4 * CARD_SIZE * progress), 0, 4 * CARD_SIZE);
        int top = Math.min(pixels, CARD_SIZE);
        if (top > 0) graphics.fill(0, 0, top, 1, color);
        int right = Math.min(Math.max(0, pixels - CARD_SIZE), CARD_SIZE);
        if (right > 0) graphics.fill(CARD_SIZE - 1, 0, CARD_SIZE, right, color);
        int bottom = Math.min(Math.max(0, pixels - CARD_SIZE * 2), CARD_SIZE);
        if (bottom > 0) graphics.fill(CARD_SIZE - bottom, CARD_SIZE - 1, CARD_SIZE, CARD_SIZE, color);
        int left = Math.min(Math.max(0, pixels - CARD_SIZE * 3), CARD_SIZE);
        if (left > 0) graphics.fill(0, CARD_SIZE - left, 1, CARD_SIZE, color);
    }

    private static void drawCastBurst(GuiGraphics graphics, float x, float y,
                                      float scale, SpellCardEffectSnapshot effects) {
        if (effects.castBurstAlpha() <= 0.001F) return;
        int radius = Math.round(CARD_SIZE * 0.5F + 2.0F + 7.0F * effects.castBurstProgress());
        int color = withAlpha(effects.selectedColor(), 220.0F * effects.castBurstAlpha());
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + CARD_SIZE * 0.5F, y + CARD_SIZE * 0.5F, 68.0F);
        pose.scale(scale, scale, 1.0F);
        graphics.fill(-radius, -radius, radius, -radius + 1, color);
        graphics.fill(-radius, radius - 1, radius, radius, color);
        graphics.fill(-radius, -radius, -radius + 1, radius, color);
        graphics.fill(radius - 1, -radius, radius, radius, color);
        pose.popPose();
    }

    private static void drawPersistentInfo(GuiGraphics graphics, int cardX, float centerY,
                                           SpellData spellData, LocalPlayer player,
                                           int selectionIndex, int count, float opacity) {
        if (spellData == null || spellData == SpellData.EMPTY) return;
        AbstractSpell spell = spellData.getSpell();
        int level = spell.getLevelFor(spellData.getLevel(), player);
        CooldownInstance cooldown = ClientMagicData.getCooldowns()
                .getSpellCooldowns().get(spell.getSpellId());
        int totalCooldown = cooldown == null ? spell.getSpellCooldown() : cooldown.getSpellCooldown();
        int remainingCooldown = cooldown == null ? 0 : Math.max(0, cooldown.getCooldownRemaining());
        List<Component> lines = new ArrayList<>();
        lines.add(spell.getDisplayName(player));
        lines.add(Component.literal((selectionIndex + 1) + " / " + count));
        lines.add(Component.translatable("gui.genius_genesis.spell_stack.level", level, spell.getMaxLevel()));
        lines.add(Component.translatable("gui.genius_genesis.spell_stack.rarity", spell.getRarity(level).getDisplayName()));
        lines.add(Component.translatable("gui.genius_genesis.spell_stack.mana", spell.getManaCost(level)));
        lines.add(Component.translatable("gui.genius_genesis.spell_stack.cooldown", seconds(remainingCooldown), seconds(totalCooldown)));
        lines.add(Component.translatable("gui.genius_genesis.spell_stack.cast_time", seconds(spell.getEffectiveCastTime(level, player))));
        lines.add(Component.translatable("gui.genius_genesis.spell_stack.spell_power", String.format(Locale.ROOT, "%.2f", spell.getSpellPower(level, player))));
        lines.add(Component.translatable("gui.genius_genesis.spell_stack.recast", spell.getRecastCount(level, player)));
        lines.addAll(spell.getUniqueInfo(level, player));
        Font font = Minecraft.getInstance().font;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(cardX - 13.0F, centerY - 4.0F, 55.0F);
        pose.scale(0.72F, 0.72F, 1.0F);
        for (int index = 0; index < lines.size(); index++) {
            Component line = lines.get(index);
            int rgb = index == 0 ? 0xFFF4F4 : 0xFFC8C8;
            graphics.drawString(font, line, -font.width(line), index * 10,
                    withAlpha(0xFF000000 | rgb, 255.0F * opacity), true);
        }
        pose.popPose();
    }

    private static String seconds(int ticks) {
        return String.format(Locale.ROOT, "%.2f", Math.max(0, ticks) / 20.0F);
    }

    private static void drawDirectionArrow(GuiGraphics graphics, float x, float y,
                                           float opacity, int color) {
        if (previousIndex == Integer.MIN_VALUE) return;
        String arrow = transitionDirection < 0 ? "▲" : "▼";
        int width = Minecraft.getInstance().font.width(arrow);
        graphics.drawString(Minecraft.getInstance().font, arrow,
                Math.round(x - width - 3.0F), Math.round(y + 7.0F),
                withAlpha(color, opacity * 230.0F), true);
    }

    private static int signedDistance(int index, int center, int count) {
        int distance = index - center;
        int half = count / 2;
        if (distance > half) distance -= count;
        else if (distance < -half) distance += count;
        return distance;
    }

    private static float transitionOffset(long now) {
        if (previousIndex == Integer.MIN_VALUE || transitionStartedAt == Long.MIN_VALUE) return 0.0F;
        float start = signedDistance(previousIndex, selectedIndex, spellCount);
        long duration = transitionSource == SpellCardAnimationState.SelectionSource.WHEEL ? WHEEL_INSERT_MILLIS : SHIFT_MILLIS;
        float progress = smootherStep((now - transitionStartedAt) / (float) duration);
        if (progress >= 1.0F) previousIndex = Integer.MIN_VALUE;
        return start * (1.0F - progress);
    }

    private static float spreadProgress(long now) {
        if (transitionStartedAt == Long.MIN_VALUE) return 0.0F;
        long elapsed = now - transitionStartedAt;
        if (elapsed <= SPREAD_HOLD_MILLIS) return 1.0F;
        return 1.0F - smootherStep((elapsed - SPREAD_HOLD_MILLIS) / (float) SPREAD_CLOSE_MILLIS);
    }

    private static float smootherStep(float value) {
        float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return clamped * clamped * clamped * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
    }

    private static int withAlpha(int color, float alpha) {
        return Mth.clamp(Math.round(alpha), 0, 255) << 24 | color & 0x00FFFFFF;
    }

    private static float red(int color) { return (color >> 16 & 255) / 255.0F; }
    private static float green(int color) { return (color >> 8 & 255) / 255.0F; }
    private static float blue(int color) { return (color & 255) / 255.0F; }

    public static void resetTransientState() {
        selectedIndex = Integer.MIN_VALUE;
        previousIndex = Integer.MIN_VALUE;
        spellCount = 0;
        transitionStartedAt = Long.MIN_VALUE;
        transitionSource = SpellCardAnimationState.SelectionSource.UNKNOWN;
        SpellCardHudClientEvents.animation().reset();
        SpellCardHudClientEvents.effects().resetTransientState();
    }

    public static void reset() { resetTransientState(); }

    public static void playSelectionSound(int previous, int selected, int count) {
        if (!SpellCardHudClientEvents.isEnabled() || count <= 1 || previous == selected) return;
        int direction = SpellCardAnimationState.signedDistance(selected, previous, count) < 0 ? -1 : 1;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        float variation = (minecraft.player.getRandom().nextFloat() - 0.5F) * 0.10F;
        float pitch = 1.32F + (direction < 0 ? 0.04F : -0.02F) + variation;
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.STONE_BUTTON_CLICK_ON, pitch, 0.42F));
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.TRIPWIRE_CLICK_ON, pitch * 1.08F, 0.14F));
    }
}
