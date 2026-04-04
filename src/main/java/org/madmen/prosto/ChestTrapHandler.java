package org.madmen.prosto;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madmen.prosto.item.ModItems;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID)
public class ChestTrapHandler {

    @SubscribeEvent
    public static void onChestOpen(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        BlockEntity be = player.level().getBlockEntity(event.getPos());
        if (be instanceof ChestBlockEntity chest) {
            boolean isTrap = true;
            for (int i = 0; i < chest.getContainerSize(); i++) {
                if (!chest.getItem(i).is(ModItems.POOP_TRAP.get())) {
                    isTrap = false;
                    break;
                }
            }

            if (isTrap) {
                player.level().explode(
                        player,
                        event.getPos().getX(),
                        event.getPos().getY(),
                        event.getPos().getZ(),
                        2.0F,
                        Level.ExplosionInteraction.NONE
                );

                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));

                for (int i = 0; i < chest.getContainerSize(); i++) {
                    chest.setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
                }
            }
        }
    }
}