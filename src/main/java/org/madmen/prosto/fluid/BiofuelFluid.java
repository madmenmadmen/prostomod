package org.madmen.prosto.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.item.ModItems;

public abstract class BiofuelFluid extends ForgeFlowingFluid {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.BIOFUEL_TYPE,
            ModFluids.BIOFUEL,
            ModFluids.FLOWING_BIOFUEL
    )
            .bucket(ModItems.BIOFUEL_BUCKET)
            .block(ModBlocks.BIOFUEL_BLOCK)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .explosionResistance(100f);

    protected BiofuelFluid() {
        super(PROPERTIES);
    }

    @Override
    public FluidType getFluidType() {
        return ModFluidTypes.BIOFUEL_TYPE.get();
    }

    public static class Source extends BiofuelFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends BiofuelFluid {
        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, FluidState> builder) {
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