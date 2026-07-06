package miku.united_as_one.genesis.combat.autoswing;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SwingPipeline {
    public final List<SwingStage> stages;
    public final SwingMode swingMode;
    public final AdvanceMode advanceMode;
    public final ReleaseMode releaseMode;
    public final EndMode endMode;
    public final int randomDelayMin;
    public final int randomDelayMax;
    public final int comboResetTicks;
    public final boolean avoidRepeat;
    public final boolean waitFullAttackCooldownAfterCombo;

    private SwingPipeline(Builder builder) {
        this.stages = List.copyOf(builder.stages);
        this.swingMode = builder.swingMode;
        this.advanceMode = builder.advanceMode;
        this.releaseMode = builder.releaseMode;
        this.endMode = builder.endMode;
        this.randomDelayMin = builder.randomDelayMin;
        this.randomDelayMax = builder.randomDelayMax;
        this.comboResetTicks = builder.comboResetTicks;
        this.avoidRepeat = builder.avoidRepeat;
        this.waitFullAttackCooldownAfterCombo = builder.waitFullAttackCooldownAfterCombo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public enum SwingMode {
        AUTO_HOLD,
        MANUAL_CLICK
    }

    public enum AdvanceMode {
        SEQUENTIAL,
        RANDOM
    }

    public enum ReleaseMode {
        RESET,
        SAVE
    }

    public enum EndMode {
        LOOP,
        HOLD_LAST,
        STOP
    }

    public record SwingStage(int cooldownTicks, SwingAction action) {
    }

    @FunctionalInterface
    public interface SwingAction {
        void perform(ServerPlayer player, ServerLevel level, ItemStack stack, int stageIndex);
    }

    public static class Builder {
        private final List<SwingStage> stages = new ArrayList<>();
        private SwingMode swingMode = SwingMode.AUTO_HOLD;
        private AdvanceMode advanceMode = AdvanceMode.SEQUENTIAL;
        private ReleaseMode releaseMode = ReleaseMode.RESET;
        private EndMode endMode = EndMode.LOOP;
        private int randomDelayMin = 10;
        private int randomDelayMax = 10;
        private int comboResetTicks;
        private boolean avoidRepeat;
        private boolean waitFullAttackCooldownAfterCombo;

        public Builder mode(SwingMode mode) {
            this.swingMode = mode;
            return this;
        }

        public Builder advance(AdvanceMode mode) {
            this.advanceMode = mode;
            return this;
        }

        public Builder release(ReleaseMode mode) {
            this.releaseMode = mode;
            return this;
        }

        public Builder end(EndMode mode) {
            this.endMode = mode;
            return this;
        }

        public Builder randomDelay(int min, int max) {
            this.randomDelayMin = min;
            this.randomDelayMax = max;
            return this;
        }

        public Builder comboReset(int ticks) {
            this.comboResetTicks = ticks;
            return this;
        }

        public Builder avoidRepeat(boolean avoidRepeat) {
            this.avoidRepeat = avoidRepeat;
            return this;
        }

        public Builder waitFullAttackCooldownAfterCombo(boolean wait) {
            this.waitFullAttackCooldownAfterCombo = wait;
            return this;
        }

        public Builder stage(int cooldownTicks, SwingAction action) {
            this.stages.add(new SwingStage(cooldownTicks, action));
            return this;
        }

        public SwingPipeline build() {
            if (this.stages.isEmpty()) {
                throw new IllegalStateException("SwingPipeline needs at least one stage");
            }
            return new SwingPipeline(this);
        }
    }
}
