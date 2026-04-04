package org.madmen.prosto.worldgen;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> POOP_TREE_PLACED;
    public static final ResourceKey<PlacedFeature> POOP_LAKE_PLACED;
    public static final ResourceKey<PlacedFeature> POOP_ORE_PLACED;

    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier height) {
        return List.of(count, InSquarePlacement.spread(), height, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier height) {
        return orePlacement(CountPlacement.of(count), height);
    }

    private static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier height) {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), height);
    }

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> lookup = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> poopLake = lookup.getOrThrow(ModConfiguredFeatures.POOP_LAKE);
        Holder<ConfiguredFeature<?, ?>> poopTree = lookup.getOrThrow(ModConfiguredFeatures.POOP_TREE);
        Holder<ConfiguredFeature<?, ?>> poopOre = lookup.getOrThrow(ModConfiguredFeatures.POOP_ORE);

        PlacementUtils.register(context, POOP_ORE_PLACED, poopOre,
                commonOrePlacement(10, HeightRangePlacement.uniform(
                        VerticalAnchor.bottom(),
                        VerticalAnchor.aboveBottom(80)
                ))
        );

        PlacementUtils.register(context, POOP_LAKE_PLACED, poopLake,
                RarityFilter.onAverageOnceEvery(4)
        );

        PlacementUtils.register(context, POOP_TREE_PLACED, poopTree,
                VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(3))
        );
    }

    static {
        POOP_TREE_PLACED = ResourceKey.create(Registries.PLACED_FEATURE,
                new ResourceLocation("prosto", "poop_tree_placed"));
        POOP_LAKE_PLACED = ResourceKey.create(Registries.PLACED_FEATURE,
                new ResourceLocation("prosto", "poop_lake_placed"));
        POOP_ORE_PLACED = ResourceKey.create(Registries.PLACED_FEATURE,
                new ResourceLocation("prosto", "poop_ore_placed"));
    }
}