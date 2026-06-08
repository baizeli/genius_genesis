package miku.united_as_one.genesis.client.gui.manuscript;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.render.RenderHelper;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.network.GenesisNetwork;
import miku.united_as_one.genesis.network.packet.LearnSpellPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public abstract class SpellLearningScreen extends Screen {
    protected static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/eldritch_research_screen/window.png");
    protected static final ResourceLocation FRAME_LOCATION = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/eldritch_research_screen/spell_frame.png");
    protected static final int WINDOW_WIDTH = 252;
    protected static final int WINDOW_HEIGHT = 256;
    protected static final int WINDOW_INSIDE_X = 9;
    protected static final int WINDOW_INSIDE_Y = 18;
    protected static final int WINDOW_INSIDE_WIDTH = 234;
    protected static final int WINDOW_INSIDE_HEIGHT = 229;
    private static final Component ALREADY_LEARNED = Component.translatable("ui.irons_spellbooks.research_already_learned").withStyle(ChatFormatting.DARK_AQUA);
    private static final int TIME_TO_HOLD = 15;

    protected final InteractionHand activeHand;
    protected int leftPos;
    protected int topPos;
    protected List<SpellNode> nodes = List.of();
    protected SyncedSpellData playerData;
    protected Vec2 viewportOffset = Vec2.ZERO;
    protected boolean isMouseHoldingSpell;
    protected boolean isMouseDragging;
    protected int heldSpellIndex = -1;
    protected int heldSpellTime = -1;
    protected int lastPlayerTick;

    protected SpellLearningScreen(Component title, InteractionHand activeHand) {
        super(title);
        this.activeHand = activeHand;
    }

    protected abstract AbstractSpell getSchoolFilter();

    protected abstract boolean isCorrectManuscript();

    protected abstract Component getRequiredItemName();

    @Override
    protected void init() {
        this.playerData = this.minecraft == null ? null : ClientMagicData.getSyncedSpellData(this.minecraft.player);
        this.leftPos = (this.width - WINDOW_WIDTH) / 2;
        this.topPos = (this.height - WINDOW_HEIGHT) / 2;
        this.viewportOffset = Vec2.ZERO;

        List<AbstractSpell> learnableSpells = SpellRegistry.getEnabledSpells().stream()
                .filter(spell -> spell.getSchoolType().equals(getSchoolFilter().getSchoolType()))
                .toList();

        List<SpellNode> arranged = new ArrayList<>();
        float step = Mth.TWO_PI / 6.0F;
        float radius = 35.0F;
        float circumference = 0.0F;
        float angle = 0.5F;
        for (AbstractSpell spell : learnableSpells) {
            if (circumference > radius * Mth.TWO_PI) {
                radius += 40.0F;
                step = 35.0F / radius;
                angle -= step;
                circumference = 0.0F;
            }
            angle += step;
            int x = this.leftPos + WINDOW_WIDTH / 2 - 8 + (int) (radius * Mth.cos(angle));
            int y = this.topPos + WINDOW_HEIGHT / 2 - 8 + (int) (radius * Mth.sin(angle));
            arranged.add(new SpellNode(spell, x, y));
            circumference += radius * step * 1.1F;
        }
        this.nodes = arranged;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        drawBackdrop(graphics, this.leftPos + WINDOW_INSIDE_X, this.topPos + WINDOW_INSIDE_Y);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        tickHeldSpell(player);
        handleConnections(graphics, partialTick);

        List<FormattedCharSequence> tooltip = null;
        for (int i = 0; i < this.nodes.size(); i++) {
            SpellNode node = this.nodes.get(i);
            drawNode(graphics, node, player, i == this.heldSpellIndex && this.heldSpellTime > 0);
            if (isHoveringNode(node, mouseX, mouseY)) {
                tooltip = buildTooltip(node.spell(), this.font);
            }
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(WINDOW_LOCATION, this.leftPos, this.topPos, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        if (tooltip != null) {
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private void tickHeldSpell(LocalPlayer player) {
        if (player.tickCount == this.lastPlayerTick) {
            return;
        }
        this.lastPlayerTick = player.tickCount;

        if (this.isMouseHoldingSpell && this.heldSpellIndex >= 0 && this.heldSpellIndex < this.nodes.size()
                && !this.nodes.get(this.heldSpellIndex).spell().isLearned(player)) {
            if (this.heldSpellTime > TIME_TO_HOLD) {
                this.heldSpellTime = -1;
                GenesisNetwork.CHANNEL.sendToServer(new LearnSpellPacket(this.activeHand, this.nodes.get(this.heldSpellIndex).spell().getSpellId()));
                player.playNotifySound(SoundRegistry.LEARN_ELDRITCH_SPELL.get(), SoundSource.MASTER, 1.0F, Utils.random.nextIntBetweenInclusive(9, 11) * 0.1F);
            }
            this.heldSpellTime++;
            if (this.lastPlayerTick % 2 == 0) {
                float pitch = Mth.lerp(this.heldSpellTime / (float) TIME_TO_HOLD, 0.5F, 1.5F);
                player.playNotifySound(SoundEvents.SOUL_ESCAPE, SoundSource.MASTER, 1.0F, pitch);
                player.playNotifySound(SoundRegistry.UI_TICK.get(), SoundSource.MASTER, 1.0F, pitch);
            }
        } else if (this.heldSpellTime >= 0) {
            this.heldSpellTime = Math.max(this.heldSpellTime - 3, -1);
        }
    }

    protected void drawNode(GuiGraphics graphics, SpellNode node, LocalPlayer player, boolean drawProgress) {
        drawWithClipping(node.spell().getSpellIconResource(), graphics, node.x(), node.y(), 0, 0, 16, 16, 16, 16,
                this.leftPos + WINDOW_INSIDE_X, this.topPos + WINDOW_INSIDE_Y, WINDOW_INSIDE_WIDTH, WINDOW_INSIDE_HEIGHT);
        if (drawProgress) {
            int x = node.x() + (int) this.viewportOffset.x;
            int y = node.y() + (int) this.viewportOffset.y;
            graphics.fill(x, y, x + Mth.ceil(16.0F * this.heldSpellTime / TIME_TO_HOLD), y + 16, FastColor.ARGB32.color(127, 244, 65, 255));
        }
        drawWithClipping(FRAME_LOCATION, graphics, node.x() - 8, node.y() - 8, node.spell().isLearned(player) ? 32 : 0, 0, 32, 32, 64, 32,
                this.leftPos + WINDOW_INSIDE_X, this.topPos + WINDOW_INSIDE_Y, WINDOW_INSIDE_WIDTH, WINDOW_INSIDE_HEIGHT);
    }

    protected void drawWithClipping(ResourceLocation texture, GuiGraphics graphics, int x, int y, int uvx, int uvy, int width, int height,
                                    int imageWidth, int imageHeight, int bbx, int bby, int bbw, int bbh) {
        x += (int) this.viewportOffset.x;
        y += (int) this.viewportOffset.y;
        if (x < bbx) {
            int diff = bbx - x;
            width -= diff;
            uvx += diff;
            x += diff;
        } else if (x > bbx + bbw - width) {
            width -= x - (bbx + bbw - width);
        }
        if (y < bby) {
            int diff = bby - y;
            height -= diff;
            uvy += diff;
            y += diff;
        } else if (y > bby + bbh - height) {
            height -= y - (bby + bbh - height);
        }
        if (width > 0 && height > 0) {
            graphics.blit(texture, x, y, width, height, uvx, uvy, width, height, imageWidth, imageHeight);
        }
    }

    protected List<FormattedCharSequence> buildTooltip(AbstractSpell spell, Font font) {
        boolean learned = spell.isLearned(Minecraft.getInstance().player);
        Component name = spell.getDisplayName(null).withStyle(learned ? ChatFormatting.DARK_AQUA : ChatFormatting.RED);
        List<FormattedCharSequence> tooltip = new ArrayList<>();
        tooltip.add(FormattedCharSequence.forward(name.getString(), name.getStyle().withUnderlined(true)));
        tooltip.addAll(font.split(Component.translatable(spell.getComponentId() + ".guide").withStyle(ChatFormatting.GRAY), 180));
        tooltip.add(FormattedCharSequence.EMPTY);
        tooltip.add((learned ? ALREADY_LEARNED : Component.translatable("ui." + Genesis.MOD_ID + ".research_warning", getRequiredItemName()).withStyle(ChatFormatting.RED)).getVisualOrderText());
        return tooltip;
    }

    protected void handleConnections(GuiGraphics graphics, float partialTick) {
        RenderSystem.enableDepthTest();
        float pulse = Mth.sin((Minecraft.getInstance().player.tickCount + partialTick) * 0.1F);
        float glow = pulse * pulse * 0.8F + 0.2F;
        Vector4f base = new Vector4f(135 / 255.0F, 154 / 255.0F, 174 / 255.0F, 0.5F);
        Vector4f lit = new Vector4f(244 / 255.0F, 65 / 255.0F, 255 / 255.0F, 0.5F);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < this.nodes.size() - 1; i++) {
            SpellNode a = this.nodes.get(i);
            SpellNode b = this.nodes.get(i + 1);
            Vec2 from = new Vec2(a.x(), a.y());
            Vec2 to = new Vec2(b.x(), b.y());
            Vec2 orth = new Vec2(-(to.y - from.y), to.x - from.x).normalized().scale(1.5F);
            Vector4f color1 = lerpColor(base, lit, glow * (a.spell().isLearned(Minecraft.getInstance().player) ? 1.0F : 0.0F));
            Vector4f color2 = lerpColor(base, lit, glow * (b.spell().isLearned(Minecraft.getInstance().player) ? 1.0F : 0.0F));
            float x1a = from.x + orth.x + 8 + (int) this.viewportOffset.x;
            float x2a = to.x + orth.x + 8 + (int) this.viewportOffset.x;
            float y1a = from.y + orth.y + 8 + (int) this.viewportOffset.y;
            float y2a = to.y + orth.y + 8 + (int) this.viewportOffset.y;
            float x1b = from.x - orth.x + 8 + (int) this.viewportOffset.x;
            float x2b = to.x - orth.x + 8 + (int) this.viewportOffset.x;
            float y1b = from.y - orth.y + 8 + (int) this.viewportOffset.y;
            float y2b = to.y - orth.y + 8 + (int) this.viewportOffset.y;
            RenderHelper.quadBuilder()
                    .vertex(x1a, y1a).color(fadeOutTowardEdges(x1a, y1a, color1))
                    .vertex(x2a, y2a).color(fadeOutTowardEdges(x2a, y2a, color2))
                    .vertex(x2b, y2b).color(fadeOutTowardEdges(x2b, y2b, color2))
                    .vertex(x1b, y1b).color(fadeOutTowardEdges(x1b, y1b, color1))
                    .build(buffer);
        }
        tesselator.end();
    }

    protected Vector4f fadeOutTowardEdges(double x, double y, Vector4f color) {
        float margin = 40.0F;
        int minX = (int) Mth.clamp(x + this.viewportOffset.x - this.leftPos, 0, WINDOW_WIDTH);
        int maxX = WINDOW_WIDTH - (int) Mth.clamp(x + this.viewportOffset.x - this.leftPos, 0, WINDOW_WIDTH);
        int minY = (int) Mth.clamp(y + this.viewportOffset.y - this.topPos, 0, WINDOW_HEIGHT);
        int maxY = WINDOW_HEIGHT - (int) Mth.clamp(y + this.viewportOffset.y - this.topPos, 0, WINDOW_HEIGHT);
        float alpha = Mth.sqrt(Mth.clamp(Math.min(minX, maxX) / margin, 0, 1) * Mth.clamp(Math.min(minY, maxY) / margin, 0, 1));
        return new Vector4f(color.x, color.y, color.z, color.w * alpha);
    }

    protected void drawBackdrop(GuiGraphics graphics, int left, int top) {
        float time = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount * 0.02F : 0.0F;
        float color = (Mth.sin(time) + 1.0F) * 0.25F + 0.15F;
        var quad = RenderHelper.quadBuilder()
                .vertex(left, top + WINDOW_INSIDE_HEIGHT)
                .vertex(left + WINDOW_INSIDE_WIDTH, top + WINDOW_INSIDE_HEIGHT)
                .vertex(left + WINDOW_INSIDE_WIDTH, top)
                .vertex(left, top)
                .color(0, 0, 0, color);
        quad.build(graphics, RenderType.endPortal());
        quad.build(graphics, RenderType.guiOverlay());
    }

    protected static Vector4f lerpColor(Vector4f a, Vector4f b, float delta) {
        float inverse = 1.0F - delta;
        return new Vector4f(a.x() * inverse + b.x() * delta, a.y() * inverse + b.y() * delta, a.z() * inverse + b.z() * delta, a.w() * inverse + b.w() * delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (int) mouseX;
        int y = (int) mouseY;
        if (Minecraft.getInstance().player != null && isCorrectManuscript()) {
            for (int i = 0; i < this.nodes.size(); i++) {
                if (isHoveringNode(this.nodes.get(i), x, y)) {
                    this.heldSpellIndex = i;
                    this.isMouseHoldingSpell = true;
                    break;
                }
            }
        }
        if (!this.isMouseHoldingSpell && isHovering(this.leftPos + WINDOW_INSIDE_X, this.topPos + WINDOW_INSIDE_Y, WINDOW_INSIDE_WIDTH, WINDOW_INSIDE_HEIGHT, x, y)) {
            this.isMouseDragging = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isMouseHoldingSpell = false;
        this.isMouseDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (this.minecraft != null && this.minecraft.options.keyInventory.isActiveAndMatches(key)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected boolean isHoveringNode(SpellNode node, int mouseX, int mouseY) {
        return isHovering(node.x() - 2 + (int) this.viewportOffset.x, node.y() - 2 + (int) this.viewportOffset.y, 20, 20, mouseX, mouseY);
    }

    protected boolean isHovering(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    protected record SpellNode(AbstractSpell spell, int x, int y) {
    }
}
