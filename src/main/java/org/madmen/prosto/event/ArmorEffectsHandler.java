package org.madmen.prosto.event;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.madmen.prosto.ModSounds;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.item.ModItems;

@EventBusSubscriber(modid = Prosto.MOD_ID, value = Dist.CLIENT)
public class ArmorEffectsHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        // Проверяем только на сервере и в конце тика
        if (!player.level().isClientSide() && event.phase == Phase.END) {

            // Проверяем полный набор какашечной брони
            boolean fullPoopArmor =
                    player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.POOP_HELMET.get() &&
                            player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.POOP_CHESTPLATE.get() &&
                            player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.POOP_LEGGINGS.get() &&
                            player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.POOP_BOOTS.get();

            // Эффекты для полного набора какашечной брони
            if (fullPoopArmor) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, false));

                // Случайный звук пукания (1% шанс каждый тик)
                if (player.level().random.nextInt(100) == 0) {
                    player.level().playSound(null, player.blockPosition(),
                            ModSounds.FART.get(), SoundSource.PLAYERS, 0.2F, 1.0F);
                }
            }
        }
    }
}