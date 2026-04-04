package org.madmen.prosto.worldgen;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import org.madmen.prosto.block.ModBlocks;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_TREE;
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_LAKE;
    public static final ResourceKey<ConfiguredFeature<?, ?>> POOP_ORE;

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        BlockState log = ModBlocks.POOP_LOG.get().defaultBlockState();
        BlockState leaves = ModBlocks.POOP_LEAVES.get().defaultBlockState();
        BlockState dirt = ModBlocks.POOP_DIRT.get().defaultBlockState();

        TreeConfiguration treeConfig = (new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(log),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(leaves),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)))
                .build();

        context.register(POOP_TREE, new ConfiguredFeature<>(Feature.TREE, treeConfig));

        BlockState poopFluid = ModBlocks.LIQUID_POOP_BLOCK.get().defaultBlockState();
        BlockState poopDirt = ModBlocks.POOP_DIRT.get().defaultBlockState();

        LakeFeature.Configuration lakeConfig = new LakeFeature.Configuration(
                BlockStateProvider.simple(poopFluid),
                BlockStateProvider.simple(poopDirt));

        OreConfiguration poopOreConfig = new OreConfiguration(
                List.of(
                        OreConfiguration.target(
                                new BlockMatchTest(Blocks.STONE),
                                ModBlocks.POOP_ORE.get().defaultBlockState()
                        ),
                        OreConfiguration.target(
                                new BlockMatchTest(ModBlocks.POOP_STONE.get()),
                                ModBlocks.POOP_ORE.get().defaultBlockState()
                        )
                ),
                9
        );

        context.register(POOP_ORE, new ConfiguredFeature<>(Feature.ORE, poopOreConfig));
        context.register(POOP_LAKE, new ConfiguredFeature<>(Feature.LAKE, lakeConfig));
    }

    static {
        POOP_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
                new ResourceLocation("prosto", "poop_tree"));
        POOP_LAKE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
                new ResourceLocation("prosto", "poop_lake"));
        POOP_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
                new ResourceLocation("prosto", "poop_ore"));
    }
}