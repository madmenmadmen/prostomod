package org.madmen.prosto.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.fluids.FluidType;
import org.madmen.prosto.Prosto;

import java.util.function.Consumer;

public class LiquidPoopFluidType extends FluidType {
    public LiquidPoopFluidType() {
        super(FluidType.Properties.create()
                .density(1200)
                .viscosity(1500)
                .temperature(300)
                .sound(SoundAction.get("fill"), SoundEvents.BUCKET_FILL)
                .sound(SoundAction.get("empty"), SoundEvents.BUCKET_EMPTY));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Prosto.MOD_ID, "block/liquid_poop_still");
            private static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Prosto.MOD_ID, "block/liquid_poop_flow");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }

            @Override
            public int getTintColor() {
                return 0xFF8B4513;
            }
        });
    }
}