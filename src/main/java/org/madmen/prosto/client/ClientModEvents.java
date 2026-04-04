package org.madmen.prosto.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.block.entity.ModBlockEntities;
import org.madmen.prosto.client.renderer.EnergyPumpRenderer;
import org.madmen.prosto.client.renderer.PoopPetRenderer;
import org.madmen.prosto.entity.ModEntities;
import org.madmen.prosto.client.renderer.EnergyCableRenderer;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.POOP_PET.get(), PoopPetRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Регистрируем рендерер для кабеля
            BlockEntityRenderers.register(ModBlockEntities.ENERGY_CABLE.get(),
                    EnergyCableRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.ENERGY_PUMP.get(), EnergyPumpRenderer::new);
        });
    }
}