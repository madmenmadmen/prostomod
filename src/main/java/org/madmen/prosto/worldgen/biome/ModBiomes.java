package org.madmen.prosto.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.madmen.prosto.worldgen.ModPlacedFeatures;

public class ModBiomes {
    public static final ResourceKey<Biome> POOP_BIOME;

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(POOP_BIOME, createPoopBiome(placed, carvers));
    }

    public static Biome createPoopBiome(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeSpecialEffects effects = (new BiomeSpecialEffects.Builder())
                .waterColor(10516570)
                .waterFogColor(8019514)
                .fogColor(5980192)
                .skyColor(9136974)
                .grassColorOverride(9141078)
                .foliageColorOverride(8416069)
                .build();

        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(placed, carvers);
        generation.addFeature(Decoration.VEGETAL_DECORATION, ModPlacedFeatures.POOP_TREE_PLACED);
        generation.addFeature(Decoration.LAKES, ModPlacedFeatures.POOP_LAKE_PLACED);
        generation.addFeature(Decoration.UNDERGROUND_ORES, ModPlacedFeatures.POOP_ORE_PLACED);

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        spawns.addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(EntityType.COW, 8, 2, 4));

        return (new Biome.BiomeBuilder())
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects(effects)
                .mobSpawnSettings(spawns.build())
                .generationSettings(generation.build())
                .build();
    }

    static {
        POOP_BIOME = ResourceKey.create(Registries.BIOME,
                new ResourceLocation("prosto", "poop_biome"));
    }
}