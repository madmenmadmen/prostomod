package org.madmen.prosto.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.madmen.prosto.item.ModItems;

public class PoopDynamiteBlock extends Block {
    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    public PoopDynamiteBlock(Properties p) {
        super(p);
        this.registerDefaultState(this.stateDefinition.any().setValue(UNSTABLE, false));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !state.getValue(UNSTABLE)) {
            level.setBlock(pos, state.setValue(UNSTABLE, true), 3);
            level.scheduleTick(pos, this, 40);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide && state.getValue(UNSTABLE)) {
            level.scheduleTick(pos, this, 40);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(UNSTABLE)) {
            explode(level, pos);
        }
    }

    private void explode(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2.0F, Level.ExplosionInteraction.NONE);

        for (int i = 0; i < 8; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double dx = Math.cos(angle) * 1.5;
            double dz = Math.sin(angle) * 1.5;
            net.minecraft.world.entity.item.ItemEntity item = new net.minecraft.world.entity.item.ItemEntity(
                    level,
                    pos.getX() + 0.5 + dx,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5 + dz,
                    new net.minecraft.world.item.ItemStack(ModItems.DIAMOND_POOP.get())
            );
            item.setDeltaMovement(dx * 0.2, 0.3, dz * 0.2);
            level.addFreshEntity(item);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UNSTABLE);
    }
}