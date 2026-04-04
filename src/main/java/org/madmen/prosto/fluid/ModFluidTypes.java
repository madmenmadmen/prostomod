package org.madmen.prosto.fluid;

import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madmen.prosto.Prosto;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Prosto.MOD_ID);

    public static final RegistryObject<FluidType> LIQUID_POOP_FLUID_TYPE = FLUID_TYPES.register("liquid_poop",
            LiquidPoopFluidType::new);

    public static final RegistryObject<FluidType> BIOFUEL_TYPE = FLUID_TYPES
            .register("biofuel", () -> new BioFuelFluidType(
                    FluidType.Properties.create()
                            .density(900) // Менее плотный чем вода (1000)
                            .viscosity(1500) // Более вязкий чем вода (1000)
                            .temperature(300) // Комнатная температура
                            .lightLevel(0)   // Не светится
                            .canExtinguish(true) // Может тушить огонь
                            .canConvertToSource(false) // Нельзя создать источник
                            .supportsBoating(true) // Можно плавать на лодке
                            .canHydrate(false) // Не гидратирует (как вода)
            ));
}