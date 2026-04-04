// BioFuelFluidType.java
package org.madmen.prosto.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.Prosto;

import java.util.function.Consumer;

public class BioFuelFluidType extends FluidType {

    // Пути к текстурам
    private static final ResourceLocation STILL_TEXTURE =
            new ResourceLocation(Prosto.MOD_ID, "block/biofuel_still");
    private static final ResourceLocation FLOWING_TEXTURE =
            new ResourceLocation(Prosto.MOD_ID, "block/biofuel_flow");

    // Зеленый цвет для биотоплива (можно настроить оттенок)
    private static final int TINT_COLOR = 0xFF4CAF50; // Зеленый цвет в формате ARGB

    public BioFuelFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {

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
                return TINT_COLOR;
            }
        });
    }
}