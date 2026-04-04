package org.madmen.prosto.screen;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madmen.prosto.Prosto;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Prosto.MOD_ID);

    public static final RegistryObject<MenuType<AdvancedPoopFurnaceMenu>> ADVANCED_POOP_FURNACE_MENU =
            MENUS.register("advanced_poop_furnace_menu",
                    () -> IForgeMenuType.create(AdvancedPoopFurnaceMenu::new));

    public static final RegistryObject<MenuType<BiofuelGeneratorMenu>> BIOFUEL_GENERATOR_MENU =
            MENUS.register("biofuel_generator_menu",
                    () -> IForgeMenuType.create(BiofuelGeneratorMenu::new) // ✅ НОВОЕ — РАБОТАЕТ
            );

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}