package org.madmen.prosto.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Prosto.MOD_ID);

    public static final RegistryObject<BlockEntityType<PoopFurnaceBlockEntity>> POOP_FURNACE =
            BLOCK_ENTITIES.register("poop_furnace",
                    () -> BlockEntityType.Builder.of(PoopFurnaceBlockEntity::new,
                            ModBlocks.POOP_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AdvancedPoopFurnaceBlockEntity>> ADVANCED_POOP_FURNACE =
            BLOCK_ENTITIES.register("advanced_poop_furnace",
                    () -> BlockEntityType.Builder.of(AdvancedPoopFurnaceBlockEntity::new,
                            ModBlocks.ADVANCED_POOP_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<BiofuelGeneratorBlockEntity>> BIOFUEL_GENERATOR =
            BLOCK_ENTITIES.register("biofuel_generator",
                    () -> BlockEntityType.Builder.of(
                            BiofuelGeneratorBlockEntity::new,
                            ModBlocks.BIOFUEL_GENERATOR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<FEGeneratorBlockEntity>> FE_GENERATOR =
            BLOCK_ENTITIES.register("fe_generator",
                    () -> BlockEntityType.Builder.of(
                            FEGeneratorBlockEntity::new,
                            ModBlocks.FE_GENERATOR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<EnergyCableBlockEntity>> ENERGY_CABLE =
            BLOCK_ENTITIES.register("energy_cable",
                    () -> BlockEntityType.Builder.of(
                            EnergyCableBlockEntity::new,
                            ModBlocks.ENERGY_CABLE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<EnergyCableBlockEntity>> ENERGY_STORAGE =
            BLOCK_ENTITIES.register("energy_storage",
                    () -> BlockEntityType.Builder.of(
                            EnergyCableBlockEntity::new,
                            ModBlocks.ENERGY_STORAGE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<EnergyPumpBlockEntity>> ENERGY_PUMP =
            BLOCK_ENTITIES.register("energy_pump",
                    () -> BlockEntityType.Builder.of(
                            EnergyPumpBlockEntity::new,
                            ModBlocks.ENERGY_PUMP.get()
                    ).build(null));
}