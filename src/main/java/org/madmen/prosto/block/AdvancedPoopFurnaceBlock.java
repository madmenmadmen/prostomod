package org.madmen.prosto.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.madmen.prosto.block.entity.AdvancedPoopFurnaceBlockEntity;
import org.madmen.prosto.block.entity.ModBlockEntities;
import javax.annotation.Nullable;

public class AdvancedPoopFurnaceBlock extends FurnaceBlock {

    public AdvancedPoopFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedPoopFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType,
                ModBlockEntities.ADVANCED_POOP_FURNACE.get(),
                AdvancedPoopFurnaceBlockEntity::serverTick);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof AdvancedPoopFurnaceBlockEntity) {
            player.openMenu((MenuProvider)blockentity);
        }
    }
}