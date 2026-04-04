package org.madmen.prosto.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import org.madmen.prosto.Prosto;

import java.util.OptionalLong;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShrekDimension {
    public static final ResourceKey<Level> SHREK_LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation(Prosto.MOD_ID, "shrek_dimension")
    );

    public static final ResourceKey<DimensionType> SHREK_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            new ResourceLocation(Prosto.MOD_ID, "shrek_type")
    );

    @SubscribeEvent
    public static void registerDimensions(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.DIMENSION_TYPE) {
            event.register(Registries.DIMENSION_TYPE, SHREK_TYPE.location(), () ->
                    new DimensionType(
                            OptionalLong.of(12000),
                            true,
                            false,
                            false,
                            true,
                            1.0,
                            true,
                            false,
                            -64,
                            384,
                            384,
                            net.minecraft.tags.BlockTags.INFINIBURN_OVERWORLD,
                            new ResourceLocation("overworld"),
                            1.0f,
                            new DimensionType.MonsterSettings(false, true,
                                    net.minecraft.util.valueproviders.UniformInt.of(0, 7), 0)
                    )
            );
        }
    }
}