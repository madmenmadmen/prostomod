package org.madmen.prosto.fluid;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madmen.prosto.Prosto;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, Prosto.MOD_ID);

    public static final RegistryObject<FlowingFluid> LIQUID_POOP = FLUIDS.register("liquid_poop",
            () -> new LiquidPoopFluid.Source());

    public static final RegistryObject<FlowingFluid> FLOWING_LIQUID_POOP = FLUIDS.register("flowing_liquid_poop",
            () -> new LiquidPoopFluid.Flowing());

    public static final RegistryObject<FlowingFluid> BIOFUEL = FLUIDS.register("biofuel",
            () -> new BiofuelFluid.Source());

    public static final RegistryObject<FlowingFluid> FLOWING_BIOFUEL = FLUIDS.register("flowing_biofuel",
            () -> new BiofuelFluid.Flowing());
}