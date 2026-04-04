package org.madmen.prosto.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.block.entity.EnergyCableBlockEntity;
import org.madmen.prosto.block.entity.ModBlockEntities;

public class EnergyCableBlock extends BaseEntityBlock {

    // Свойства подключений (6 сторон)
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    // Формы для оптимизации
    private static final VoxelShape CORE = Shapes.box(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);
    private static final VoxelShape ARM_NORTH = Shapes.box(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);
    private static final VoxelShape ARM_SOUTH = Shapes.box(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);
    private static final VoxelShape ARM_EAST = Shapes.box(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);
    private static final VoxelShape ARM_WEST = Shapes.box(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);
    private static final VoxelShape ARM_UP = Shapes.box(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);
    private static final VoxelShape ARM_DOWN = Shapes.box(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);

    public EnergyCableBlock(Properties properties) {
        super(properties);
        // Стандартное состояние: все подключения выключены
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    // Обновляем подключения при изменении соседних блоков
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean shouldConnect = shouldConnectTo(level, neighborPos, direction);
        BooleanProperty property = getPropertyForDirection(direction);
        return state.setValue(property, shouldConnect);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState state = this.defaultBlockState();

        // Проверяем все стороны при установке
        for (Direction dir : Direction.values()) {
            boolean shouldConnect = shouldConnectTo(level, pos.relative(dir), dir);
            state = state.setValue(getPropertyForDirection(dir), shouldConnect);
        }

        return state;
    }

    // Определяем, нужно ли подключаться к соседу
    private boolean shouldConnectTo(LevelAccessor level, BlockPos neighborPos, Direction side) {
        BlockEntity neighbor = level.getBlockEntity(neighborPos);
        if (neighbor == null) return false;

        // Проверяем, есть ли у соседа capability энергии
        return neighbor.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY,
                side.getOpposite()).isPresent();
    }

    // Получаем свойство для направления
    private BooleanProperty getPropertyForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    // Форма блока для коллизий
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;

        // Добавляем "руки" в подключённые стороны
        if (state.getValue(NORTH)) shape = Shapes.or(shape, ARM_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, ARM_SOUTH);
        if (state.getValue(EAST)) shape = Shapes.or(shape, ARM_EAST);
        if (state.getValue(WEST)) shape = Shapes.or(shape, ARM_WEST);
        if (state.getValue(UP)) shape = Shapes.or(shape, ARM_UP);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, ARM_DOWN);

        return shape;
    }

    // Для кастомного рендерера
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    // Block Entity
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyCableBlockEntity(pos, state);
    }

    // Ticker для логики
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null; // На клиенте не тикаем
        }
        return createTickerHelper(type, ModBlockEntities.ENERGY_CABLE.get(),
                EnergyCableBlockEntity::tick);
    }
}