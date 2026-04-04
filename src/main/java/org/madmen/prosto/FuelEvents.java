package org.madmen.prosto;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.item.ModItems;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FuelEvents {

    @SubscribeEvent
    public static void onGetBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();
        if (fuel.is(ModItems.POOP.get())) {
            event.setBurnTime(200);
        } else if (fuel.is(ModItems.GOLDEN_POOP.get())) {
            event.setBurnTime(400);
        } else if (fuel.is(ModBlocks.POOP_BLOCK.get().asItem())) {
            event.setBurnTime(1600);
        }
    }
}