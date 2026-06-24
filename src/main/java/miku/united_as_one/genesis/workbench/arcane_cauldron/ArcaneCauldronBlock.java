package miku.united_as_one.genesis.workbench.arcane_cauldron;

import miku.united_as_one.genesis.workbench.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArcaneCauldronBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.or(
                    box(0, 0, 4, 16, 2, 6),
                    box(0, 0, 10, 16, 2, 12),
                    box(4, 0, 0, 6, 2, 16),
                    box(10, 0, 0, 12, 2, 16)
            ),
            Shapes.join(
                    Shapes.or(
                            Shapes.join(box(0, 2, 0, 16, 16, 16), box(0, 12, 0, 16, 14, 16), BooleanOp.ONLY_FIRST),
                            box(1, 12, 1, 15, 14, 15)
                    ),
                    box(2, 4, 2, 14, 16, 14),
                    BooleanOp.ONLY_FIRST
            )
    );

    public ArcaneCauldronBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneCauldronBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, BlockEntityRegistry.ARCANE_CAULDRON.get(), ArcaneCauldronBlockEntity::serverTick);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ArcaneCauldronBlockEntity cauldron) {
            return cauldron.handleUse(level, player, hand);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof ArcaneCauldronBlockEntity cauldron) {
            cauldron.dropContents();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
