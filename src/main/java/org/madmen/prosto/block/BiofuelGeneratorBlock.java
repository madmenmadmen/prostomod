package org.madmen.prosto.block;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.madmen.prosto.block.entity.BiofuelGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.block.entity.ModBlockEntities;

public class BiofuelGeneratorBlock extends BaseEntityBlock {
    public static final BooleanProperty WORKING = BooleanProperty.create("working");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BiofuelGeneratorBlock(Properties properties) {
        super(properties);
        // Устанавливаем начальное состояние
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(WORKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WORKING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Правильно устанавливаем направление при установке блока
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WORKING, false);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof BiofuelGeneratorBlockEntity generator) {
            // === 1. Отладка: Shift + ПКМ голой рукой ===
            if (!level.isClientSide() && heldItem.isEmpty() && player.isShiftKeyDown()) {
                generator.onRightClick(player, hand, true);
                return InteractionResult.sidedSuccess(true);
            }

            // === 2. Забор биотоплива ведром ===
            if (!level.isClientSide() && heldItem.getItem() == Items.BUCKET) {
                int fluidAmount = generator.getOutputTank().getFluidAmount();
                if (fluidAmount >= 1000) {
                    generator.onRightClick(player, hand, false);
                    return InteractionResult.sidedSuccess(true);
                }
            }

            // === 3. Иначе — открываем GUI ===
            if (!level.isClientSide()) {
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) generator, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BiofuelGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        // Создаем тикер только на серверной стороне
        if (level.isClientSide()) {
            return null;
        }

        // Проверяем, что это правильный тип BlockEntity
        return createTickerHelper(type, ModBlockEntities.BIOFUEL_GENERATOR.get(),
                BiofuelGeneratorBlockEntity::tick);
    }
}