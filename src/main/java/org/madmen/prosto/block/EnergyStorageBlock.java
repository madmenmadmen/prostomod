package org.madmen.prosto.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.block.entity.EnergyStorageBlockEntity;

public class EnergyStorageBlock extends BaseEntityBlock {
    public EnergyStorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // Shift + ПКМ для информации
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnergyStorageBlockEntity storage) {

            if (player.isShiftKeyDown()) {
                // Shift + ПКМ — подробная информация
                player.displayClientMessage(
                        Component.literal("§6╔══════════════════╗"),
                        false
                );
                player.displayClientMessage(
                        Component.literal("§6║  Duracell Battery ║"),
                        false
                );
                player.displayClientMessage(
                        Component.literal("§6╠══════════════════╣"),
                        false
                );
                player.displayClientMessage(
                        Component.literal("§f⚡ Заряд: §e" + storage.getEnergyStored() + "§f/§e" +
                                storage.getMaxEnergyStored() + " FE"),
                        false
                );

                int percent = (int) ((storage.getEnergyStored() / (float) storage.getMaxEnergyStored()) * 100);
                String bar = createChargeBar(percent);
                player.displayClientMessage(
                        Component.literal("§f" + bar + " §e" + percent + "%"),
                        false
                );

                player.displayClientMessage(
                        Component.literal("§f└──────────────────┘"),
                        false
                );
            } else {
                // Просто ПКМ — краткая информация
                player.displayClientMessage(
                        Component.literal("⚡ " + storage.getEnergyStored() + "/" +
                                storage.getMaxEnergyStored() + " FE"),
                        false
                );
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    private String createChargeBar(int percent) {
        StringBuilder bar = new StringBuilder("[");
        int bars = percent / 10;
        for (int i = 0; i < 10; i++) {
            if (i < bars) {
                bar.append("§a|");
            } else {
                bar.append("§7|");
            }
        }
        bar.append("§f]");
        return bar.toString();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyStorageBlockEntity(pos, state);
    }
}