package org.madmen.prosto;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.item.ModItems;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID)
public class PoopDynamiteHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(ModItems.POOP_DYNAMITE.get())) {
            var level = event.getLevel();
            var player = event.getEntity();
            var pos = event.getPos().relative(event.getFace());

            if (!level.isClientSide && level.getBlockState(pos).isAir()) {
                level.setBlock(pos, ModBlocks.POOP_DYNAMITE_BLOCK.get().defaultBlockState(), 3);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                event.setCanceled(true);
            }
        }
    }
}