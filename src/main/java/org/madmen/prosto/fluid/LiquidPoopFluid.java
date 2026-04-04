package org.madmen.prosto.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.item.ModItems;

public abstract class LiquidPoopFluid extends ForgeFlowingFluid {
    public static final Properties PROPERTIES = new Properties(
            () -> ModFluidTypes.LIQUID_POOP_FLUID_TYPE.get(),
            () -> ModFluids.LIQUID_POOP.get(),
            () -> ModFluids.FLOWING_LIQUID_POOP.get()
    ).bucket(() -> ModItems.LIQUID_POOP_BUCKET.get())
            .block(() -> ModBlocks.LIQUID_POOP_BLOCK.get())
            .slopeFindDistance(4)
            .levelDecreasePerBlock(1);

    protected LiquidPoopFluid(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSource(FluidState state) {
        return false;
    }

    @Override
    public int getAmount(FluidState state) {
        return 0;
    }

    public static class Source extends LiquidPoopFluid {
        public Source() {
            super(PROPERTIES);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends LiquidPoopFluid {
        public Flowing() {
            super(PROPERTIES);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}