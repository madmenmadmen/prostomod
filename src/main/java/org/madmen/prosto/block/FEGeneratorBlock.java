package org.madmen.prosto.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.madmen.prosto.block.entity.FEGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.block.entity.ModBlockEntities;
import org.madmen.prosto.fluid.ModFluids;
import org.madmen.prosto.item.ModItems;

public class FEGeneratorBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public FEGeneratorBlock(Properties properties) {
        super(properties);
        // Устанавливаем начальное состояние с FACING
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Правильно устанавливаем направление при установке блока
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(ACTIVE, false);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FEGeneratorBlockEntity generator)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        // === 1. Если в руке ведро с биотопливом — заливаем ===
        if (heldItem.getItem() == ModItems.BIOFUEL_BUCKET.get()) {
            FluidTank tank = generator.getInputTank();
            if (tank.getSpace() >= 1000) { // 1 ведро = 1000 mB
                // Заливаем жидкость
                tank.fill(new FluidStack(ModFluids.BIOFUEL.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                // Заменяем ведро на пустое
                heldItem.shrink(1);
                if (!player.getInventory().add(new ItemStack(net.minecraft.world.item.Items.BUCKET))) {
                    player.drop(new ItemStack(net.minecraft.world.item.Items.BUCKET), false);
                }
                return InteractionResult.CONSUME;
            }
        }

        // === 2. Иначе — показываем энергию (как сейчас) ===
        player.displayClientMessage(
                Component.literal("Energy: " + generator.getEnergyStored() + "/" + generator.getMaxEnergyStored()),
                false
        );

        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FEGeneratorBlockEntity(pos, state);
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
        return createTickerHelper(type, ModBlockEntities.FE_GENERATOR.get(),
                FEGeneratorBlockEntity::tick);
    }
}