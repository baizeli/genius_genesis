package miku.united_as_one.genesis.combat.autoswing;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class AutoSwingManager {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Map<UUID, State> STATES = new HashMap<>();

    private AutoSwingManager() {
    }

    public static void remove(UUID id) {
        STATES.remove(id);
    }

    public static void onAttackInput(ServerPlayer player, boolean down) {
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (!(item instanceof IAutoSwingItem autoSwingItem)) {
            State state = STATES.get(player.getUUID());
            if (state != null) {
                state.holding = false;
            }
            return;
        }

        SwingPipeline pipeline = autoSwingItem.getSwingPipeline(stack);
        State state = stateOf(player);
        if (pipeline.swingMode == SwingPipeline.SwingMode.AUTO_HOLD) {
            if (down) {
                state.holding = true;
                state.idle = 0;
                if (player.isUsingItem()) {
                    player.stopUsingItem();
                }
                if (state.cooldown <= 0) {
                    fire(player, pipeline, stack, state);
                }
            } else {
                state.holding = false;
                if (pipeline.releaseMode == SwingPipeline.ReleaseMode.RESET) {
                    state.index = 0;
                    state.lastFired = -1;
                }
            }
        } else if (down) {
            state.idle = 0;
            if (state.cooldown <= 0) {
                fire(player, pipeline, stack, state);
            }
        }
    }

    public static boolean isAutoAttacking(UUID playerId) {
        State state = STATES.get(playerId);
        return state != null && state.holding;
    }

    public static void serverTick(ServerPlayer player) {
        State state = STATES.get(player.getUUID());
        if (state == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        if (!(item instanceof IAutoSwingItem autoSwingItem)) {
            state.holding = false;
            return;
        }

        SwingPipeline pipeline = autoSwingItem.getSwingPipeline(stack);
        if (pipeline.swingMode == SwingPipeline.SwingMode.AUTO_HOLD) {
            if (state.holding) {
                if (state.cooldown <= 0) {
                    fire(player, pipeline, stack, state);
                } else {
                    state.cooldown--;
                }
            } else if (state.cooldown > 0) {
                state.cooldown--;
            }
        } else {
            if (state.cooldown > 0) {
                state.cooldown--;
            }
            if (pipeline.releaseMode == SwingPipeline.ReleaseMode.RESET
                    && pipeline.advanceMode == SwingPipeline.AdvanceMode.SEQUENTIAL
                    && pipeline.comboResetTicks > 0
                    && ++state.idle > pipeline.comboResetTicks) {
                state.index = 0;
                state.lastFired = -1;
            }
        }
    }

    private static State stateOf(ServerPlayer player) {
        return STATES.computeIfAbsent(player.getUUID(), key -> new State());
    }

    private static void fire(ServerPlayer player, SwingPipeline pipeline, ItemStack stack, State state) {
        if (state.waitingAttackCooldown) {
            if (player.getAttackStrengthScale(0.0F) < 1.0F) {
                return;
            }
            state.waitingAttackCooldown = false;
        }

        int index;
        if (pipeline.advanceMode == SwingPipeline.AdvanceMode.RANDOM) {
            index = pickRandom(pipeline, state);
        } else {
            if (state.index >= pipeline.stages.size()) {
                return;
            }
            index = state.index;
        }

        SwingPipeline.SwingStage stage = pipeline.stages.get(index);
        if (player.level() instanceof ServerLevel serverLevel) {
            stage.action().perform(player, serverLevel, stack, index);
            player.swing(InteractionHand.MAIN_HAND, true);
        }
        state.lastFired = index;
        if (pipeline.advanceMode == SwingPipeline.AdvanceMode.RANDOM) {
            state.cooldown = randomDelay(pipeline);
        } else {
            state.cooldown = stage.cooldownTicks();
            state.waitingAttackCooldown = shouldWaitForFullAttackCooldown(pipeline, index);
            state.index = nextIndex(pipeline, index);
        }
    }

    private static boolean shouldWaitForFullAttackCooldown(SwingPipeline pipeline, int firedIndex) {
        return pipeline.waitFullAttackCooldownAfterCombo
                && pipeline.endMode == SwingPipeline.EndMode.LOOP
                && firedIndex == pipeline.stages.size() - 1;
    }

    private static int nextIndex(SwingPipeline pipeline, int index) {
        int next = index + 1;
        if (next < pipeline.stages.size()) {
            return next;
        }
        return switch (pipeline.endMode) {
            case LOOP -> 0;
            case HOLD_LAST -> pipeline.stages.size() - 1;
            case STOP -> pipeline.stages.size();
        };
    }

    private static int pickRandom(SwingPipeline pipeline, State state) {
        int size = pipeline.stages.size();
        if (size == 1) {
            return 0;
        }
        int index = RANDOM.nextInt(size);
        if (pipeline.avoidRepeat && index == state.lastFired) {
            index = (index + 1 + RANDOM.nextInt(size - 1)) % size;
        }
        return index;
    }

    private static int randomDelay(SwingPipeline pipeline) {
        if (pipeline.randomDelayMax > pipeline.randomDelayMin) {
            return pipeline.randomDelayMin + RANDOM.nextInt(pipeline.randomDelayMax - pipeline.randomDelayMin + 1);
        }
        return pipeline.randomDelayMin;
    }

    private static final class State {
        private boolean holding;
        private int index;
        private int cooldown;
        private int idle;
        private int lastFired = -1;
        private boolean waitingAttackCooldown;
    }
}
