package org.madmen.prosto.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.fluid.ModFluids;
import org.madmen.prosto.item.ModItems;

import java.util.Collections;
import java.util.List;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Prosto.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Prosto.MOD_ID);

    public static final RegistryObject<Block> POOP_BLOCK = BLOCKS.register("poop_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .strength(0.5F)));

    public static final RegistryObject<Item> POOP_BLOCK_ITEM = ITEMS.register("poop_block",
            () -> new BlockItem(POOP_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_FURNACE = BLOCKS.register("poop_furnace",
            () -> new PoopFurnaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .strength(3.5F)
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0)
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Item> POOP_FURNACE_ITEM = ITEMS.register("poop_furnace",
            () -> new BlockItem(POOP_FURNACE.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_DYNAMITE_BLOCK = BLOCKS.register("poop_dynamite_block",
            () -> new PoopDynamiteBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .noCollission()
                    .sound(SoundType.GRASS)));

    public static final RegistryObject<Item> POOP_DYNAMITE_BLOCK_ITEM = ITEMS.register("poop_dynamite_block",
            () -> new BlockItem(POOP_DYNAMITE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> DIAMOND_POOP_PORTAL_CORE = BLOCKS.register("diamond_poop_portal_core",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIAMOND)
                    .strength(5.0F)
                    .lightLevel(state -> 15)
                    .sound(SoundType.GLASS)));

    public static final RegistryObject<Item> DIAMOND_POOP_PORTAL_CORE_ITEM = ITEMS.register("diamond_poop_portal_core",
            () -> new BlockItem(DIAMOND_POOP_PORTAL_CORE.get(), new Item.Properties()));

    public static final RegistryObject<LiquidBlock> LIQUID_POOP_BLOCK = BLOCKS.register("liquid_poop_block",
            () -> new LiquidBlock(ModFluids.LIQUID_POOP, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()));

    public static final RegistryObject<Block> ADVANCED_POOP_FURNACE = BLOCKS.register("advanced_poop_furnace",
            () -> new AdvancedPoopFurnaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(3.5F)
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Item> ADVANCED_POOP_FURNACE_ITEM = ITEMS.register("advanced_poop_furnace",
            () -> new BlockItem(ADVANCED_POOP_FURNACE.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_GRASS_BLOCK = BLOCKS.register("poop_grass_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GRASS)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(0.6F)
                    .sound(SoundType.GRASS)
                    .lightLevel(state -> 0)));

    public static final RegistryObject<Item> POOP_GRASS_BLOCK_ITEM = ITEMS.register("poop_grass_block",
            () -> new BlockItem(POOP_GRASS_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_DIRT = BLOCKS.register("poop_dirt",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(0.5F)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Item> POOP_DIRT_ITEM = ITEMS.register("poop_dirt",
            () -> new BlockItem(POOP_DIRT.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_LOG = BLOCKS.register("poop_log",
            () -> new PoopLogBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.WOOD)));

    public static final RegistryObject<Item> POOP_LOG_ITEM = ITEMS.register("poop_log",
            () -> new BlockItem(POOP_LOG.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_LEAVES = BLOCKS.register("poop_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isSuffocating((state, level, pos) -> false)));

    public static final RegistryObject<Item> POOP_LEAVES_ITEM = ITEMS.register("poop_leaves",
            () -> new BlockItem(POOP_LEAVES.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_STONE = BLOCKS.register("poop_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Item> POOP_STONE_ITEM = ITEMS.register("poop_stone",
            () -> new BlockItem(POOP_STONE.get(), new Item.Properties()));

    public static final RegistryObject<Block> POOP_ORE = BLOCKS.register("poop_ore",
            () -> new PoopOreBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(0.4F, 0.4F)));

    public static final RegistryObject<Item> POOP_ORE_ITEM = ITEMS.register("poop_ore",
            () -> new BlockItem(POOP_ORE.get(), new Item.Properties()));

    // Генератор биотоплива
    public static final RegistryObject<Block> BIOFUEL_GENERATOR = BLOCKS.register("biofuel_generator",
            () -> new BiofuelGeneratorBlock(Block.Properties.of()
                    .strength(3.0f)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(BiofuelGeneratorBlock.WORKING) ? 8 : 0)));

    public static final RegistryObject<Item> BIOFUEL_GENERATOR_ITEM = ITEMS.register("biofuel_generator",
            () -> new BlockItem(BIOFUEL_GENERATOR.get(), new Item.Properties()));

    // FE генератор
    public static final RegistryObject<Block> FE_GENERATOR = BLOCKS.register("fe_generator",
            () -> new FEGeneratorBlock(Block.Properties.of()
                    .strength(3.5f)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(FEGeneratorBlock.ACTIVE) ? 10 : 0)));

    public static final RegistryObject<Item> FE_GENERATOR_ITEM = ITEMS.register("fe_generator",
            () -> new BlockItem(FE_GENERATOR.get(), new Item.Properties()));

    // Блок жидкости биотоплива
    public static final RegistryObject<LiquidBlock> BIOFUEL_BLOCK = BLOCKS.register("biofuel",
            () -> new LiquidBlock(ModFluids.BIOFUEL, Block.Properties.of()
                    .noCollission()
                    .strength(100f)
                    .noLootTable()));

    public static final RegistryObject<Block> ENERGY_CABLE = BLOCKS.register("energy_cable",
            () -> new EnergyCableBlock(Block.Properties.of()
                    .strength(1.5f)));

    public static final RegistryObject<Item> ENERGY_CABLE_ITEM = ITEMS.register("energy_cable",
            () -> new BlockItem(ENERGY_CABLE.get(), new Item.Properties()));

    public static final RegistryObject<Block> ENERGY_STORAGE = BLOCKS.register("energy_storage",
            () -> new EnergyStorageBlock(Block.Properties.of()
                    .strength(1.5f)));

    public static final RegistryObject<Item> ENERGY_STORAGE_ITEM = ITEMS.register("energy_storage",
            () -> new BlockItem(ENERGY_STORAGE.get(), new Item.Properties()));

    public static final RegistryObject<Block> ENERGY_PUMP = BLOCKS.register("energy_pump",
            () -> new EnergyPumpBlock(Block.Properties.of()
                    .strength(1.5f)));

    public static final RegistryObject<Item> ENERGY_PUMP_ITEM = ITEMS.register("energy_pump",
            () -> new BlockItem(ENERGY_PUMP.get(), new Item.Properties()));

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}