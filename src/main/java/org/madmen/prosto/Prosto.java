package org.madmen.prosto;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.block.entity.ModBlockEntities;
import org.madmen.prosto.client.gui.AdvancedPoopFurnaceScreen;
import org.madmen.prosto.client.gui.BiofuelGeneratorScreen;
import org.madmen.prosto.entity.ModEntities;
import org.madmen.prosto.fluid.ModFluidTypes;
import org.madmen.prosto.fluid.ModFluids;
import org.madmen.prosto.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.madmen.prosto.network.NetworkHandler;
import org.madmen.prosto.screen.ModMenuTypes;

@Mod("prosto")
public class Prosto {
    public static final String MOD_ID = "prosto";
    public static Logger LOGGER = LogManager.getLogger();

    public Prosto() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(bus);
        ModItems.register(bus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenuTypes.MENUS.register(bus);
        ModFluids.FLUIDS.register(bus);
        ModFluidTypes.FLUID_TYPES.register(bus);
        ModSounds.SOUND_EVENTS.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::onClientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Prosto.LOGGER.info("★ Регистрируем сетевые пакеты...");
            NetworkHandler.register();
        });
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ADVANCED_POOP_FURNACE_MENU.get(), AdvancedPoopFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.BIOFUEL_GENERATOR_MENU.get(), BiofuelGeneratorScreen::new);
        });
    }
}