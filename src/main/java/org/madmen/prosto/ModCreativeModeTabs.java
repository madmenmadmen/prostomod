package org.madmen.prosto;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.item.ModItems;

@EventBusSubscriber(bus = Bus.MOD)
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS;
    public static final RegistryObject<CreativeModeTab> POOP_TAB;

    static {
        CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "prosto");

        POOP_TAB = CREATIVE_MODE_TABS.register("poop_tab", () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.prosto.poop_tab"))
                        .icon(() -> new ItemStack((ItemLike) ModItems.POOP.get()))
                        .displayItems((params, output) -> {
                            // Предметы
                            output.accept(ModItems.POOP.get());
                            output.accept(ModItems.GOLDEN_POOP.get());
                            output.accept(ModItems.DIAMOND_POOP.get());
                            output.accept(ModItems.POOP_DYNAMITE.get());
                            output.accept(ModItems.POOP_TRAP.get());
                            output.accept(ModItems.POOP_PET_ITEM.get());
                            output.accept(ModItems.RUBY_POOP.get());

                            // Броня из какашки
                            output.accept(ModItems.POOP_HELMET.get());
                            output.accept(ModItems.POOP_CHESTPLATE.get());
                            output.accept(ModItems.POOP_LEGGINGS.get());
                            output.accept(ModItems.POOP_BOOTS.get());

                            // Инструменты из какашки
                            output.accept(ModItems.POOP_SWORD.get());
                            output.accept(ModItems.POOP_AXE.get());
                            output.accept(ModItems.POOP_HOE.get());
                            output.accept(ModItems.POOP_SHOVEL.get());
                            output.accept(ModItems.POOP_PICKAXE.get());

                            output.accept(ModBlocks.POOP_BLOCK_ITEM.get());
                            output.accept(ModBlocks.POOP_FURNACE_ITEM.get());
                            output.accept(ModBlocks.POOP_DYNAMITE_BLOCK_ITEM.get());
                            output.accept(ModBlocks.DIAMOND_POOP_PORTAL_CORE_ITEM.get());
                            output.accept(ModBlocks.ADVANCED_POOP_FURNACE_ITEM.get());
                            output.accept(ModBlocks.POOP_GRASS_BLOCK_ITEM.get());
                            output.accept(ModBlocks.POOP_DIRT_ITEM.get());
                            output.accept(ModBlocks.POOP_LEAVES_ITEM.get());
                            output.accept(ModBlocks.POOP_LOG_ITEM.get());
                            output.accept(ModBlocks.POOP_ORE_ITEM.get());
                            output.accept(ModBlocks.BIOFUEL_GENERATOR_ITEM.get());
                            output.accept(ModBlocks.FE_GENERATOR_ITEM.get());
                        })
                        .build()
        );
    }
}