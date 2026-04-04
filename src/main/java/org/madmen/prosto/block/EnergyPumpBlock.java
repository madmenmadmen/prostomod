package org.madmen.prosto.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.block.entity.EnergyPumpBlockEntity;
import org.madmen.prosto.block.entity.ModBlockEntities;

public class EnergyPumpBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
    public static final EnumProperty<PumpMode> MODE = EnumProperty.create("mode", PumpMode.class);

    private static final VoxelShape SHAPE_WEST = Shapes.box(
            0.0/16.0,   // x min
            0.0/16.0,   // y min
            2.0/16.0,   // z min
            2.0/16.0,   // x max
            11.0/16.0,  // y max
            14.0/16.0   // z max
    );

    // Правильные преобразования для каждой стороны
    private static final VoxelShape[] SHAPES = new VoxelShape[6];

    static {
        // WEST (как в рендерере)
        // EAST (отзеркалить по X)
        SHAPES[Direction.WEST.get3DDataValue()] = Shapes.box(
                14.0/16.0,  // 16 - 2 = 14
                0.0/16.0,
                2.0/16.0,
                16.0/16.0,  // 16
                11.0/16.0,
                14.0/16.0
        );

        // EAST (отзеркалить по X)
        SHAPES[Direction.EAST.get3DDataValue()] = SHAPE_WEST;

        // NORTH (поворот на 90° по часовой)
        // x становится z, z становится обратный x
        SHAPES[Direction.SOUTH.get3DDataValue()] = Shapes.box(
                14.0/16.0 - 2.0/16.0,  // z: 14-2=12 становится x
                0.0/16.0,
                0.0/16.0,              // x: 0 становится z
                14.0/16.0,             // z: 14 становится x
                11.0/16.0,
                2.0/16.0               // x: 2 становится z
        );

        // SOUTH (поворот на -90° или 270°)
        SHAPES[Direction.NORTH.get3DDataValue()] = Shapes.box(
                2.0/16.0,              // x: 2 (но это z)
                0.0/16.0,
                14.0/16.0 - 2.0/16.0,  // z: 12 (но это x)
                14.0/16.0,             // x: 14 (но это z)
                11.0/16.0,
                16.0/16.0              // z: 16 (но это x)
        );

        // UP (поворот на -90° по X)
        // y становится z, z становится y
        SHAPES[Direction.UP.get3DDataValue()] = Shapes.box(
                0.0/16.0,
                14.0/16.0 - 2.0/16.0,  // z: 12 становится y
                0.0/16.0,              // y: 0 становится z
                2.0/16.0,
                14.0/16.0,             // z: 14 становится y
                11.0/16.0              // y: 11 становится z
        );

        // DOWN (поворот на 90° по X)
        SHAPES[Direction.DOWN.get3DDataValue()] = Shapes.box(
                0.0/16.0,
                2.0/16.0,              // z: 2 становится y
                0.0/16.0,              // y: 0 становится z
                2.0/16.0,
                16.0/16.0 - 14.0/16.0 + 2.0/16.0,  // сложная формула, упростим
                11.0/16.0              // y: 11 становится z
        );
    }

    public enum PumpMode implements StringRepresentable {
        PUSH("push", 0xFF00FF00),      // Зелёный
        PULL("pull", 0xFFFF0000),      // Красный
        NONE("none", 0xFF808080);      // Серый

        private final String name;
        private final int color;

        PumpMode(String name, int color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }

        public PumpMode next() {
            return switch (this) {
                case PUSH -> PULL;
                case PULL -> NONE;
                case NONE -> PUSH;
            };
        }

        public String getDisplayName() {
            return switch (this) {
                case PUSH -> "Разгрузка";
                case PULL -> "Загрузка";
                case NONE -> "Выключено";
            };
        }

        @Override
        public String getSerializedName() {
            return this.name; // ВАЖНО: возвращаем уникальное имя для каждого значения!
            // Не возвращайте константу "pump_mode_prosto_fe_energy_tralala" для всех!
        }
    }

    public EnergyPumpBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(MODE, PumpMode.PUSH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Определяем, на какую грань блока ставим помпу
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos placedOnPos = pos.relative(facing.getOpposite());

        // Проверяем, можно ли поставить помпу на этот блок
        if (!level.getBlockState(placedOnPos).isFaceSturdy(level, placedOnPos, facing)) {
            return null; // Нельзя поставить
        }

        // Также проверяем, не занято ли место для помпы
        if (!level.getBlockState(pos).canBeReplaced(context)) {
            return null;
        }

        // ВАЖНО: Модель сделана для WEST, поэтому нужно адаптировать
        // facing - это куда смотрит помпа (в сторону от блока)
        return this.defaultBlockState()
                .setValue(FACING, facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        VoxelShape shape = SHAPES[facing.get3DDataValue()];
        return shape != null ? shape : SHAPE_WEST;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachedPos = pos.relative(facing.getOpposite()); // Блок, на который крепится

        // Помпа прикреплена к блоку в противоположном направлении
        return level.getBlockState(attachedPos).isFaceSturdy(level, attachedPos, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);

        // Если блок, на который крепимся, был удален - ломаем помпу
        if (direction == facing.getOpposite()) {
            if (!neighborState.isFaceSturdy(level, neighborPos, facing)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            PumpMode currentMode = state.getValue(MODE);
            BlockState newState = state.setValue(MODE, currentMode.next());
            level.setBlock(pos, newState, 3);

            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "Режим помпы: " + newState.getValue(MODE).getDisplayName()
            ));

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE; // Используем JSON модель
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyPumpBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ENERGY_PUMP.get(),
                EnergyPumpBlockEntity::tick);
    }
}