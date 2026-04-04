package org.madmen.prosto.event;

import java.util.UUID;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.item.ModItems;

@EventBusSubscriber(modid = Prosto.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Проверяем только в конце тика
        if (event.phase == Phase.END) {
            // Проверяем только на серверной стороне
            if (!event.player.level().isClientSide()) {
                Player player = event.player;

                // Проверяем, что это серверный игрок
                if (player instanceof ServerPlayer serverPlayer) {
                    UUID playerUUID = serverPlayer.getUUID();
                    int totalDiamondPoop = 0;

                    // Считаем общее количество алмазных какашек в инвентаре
                    for (ItemStack stack : serverPlayer.getInventory().items) {
                        if (stack.getItem() == ModItems.DIAMOND_POOP.get()) {
                            totalDiamondPoop += stack.getCount();
                        }
                    }

                    // Если набрали 128+ алмазных какашек
                    if (totalDiamondPoop >= 128) {
                        ResourceLocation advId = new ResourceLocation("prosto", "diamond_poop_rich");
                        Advancement advancement = serverPlayer.server.getAdvancements()
                                .getAdvancement(advId);

                        // Если есть такое достижение и оно еще не получено
                        if (advancement != null) {
                            AdvancementProgress progress = serverPlayer.getAdvancements()
                                    .getOrStartProgress(advancement);

                            // Выдаем все критерии достижения
                            if (!progress.isDone()) {
                                for (String criterion : progress.getRemainingCriteria()) {
                                    serverPlayer.getAdvancements().award(advancement, criterion);
                                }
                            }
                        }

                        // Создаем эффект частиц
                        ServerLevel level = (ServerLevel) serverPlayer.level();
                        Vec3 pos = serverPlayer.position();

                        // Генерируем частицы
                        for (int i = 0; i < 50; ++i) {
                            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
                            double offsetY = level.random.nextDouble() * 2.0;
                            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;

                            level.sendParticles(ParticleTypes.HEART,
                                    pos.x + offsetX,
                                    pos.y + offsetY,
                                    pos.z + offsetZ,
                                    1, 0.0, 0.0, 0.0, 0.0);

                            level.sendParticles(ParticleTypes.GLOW,
                                    pos.x + offsetX,
                                    pos.y + offsetY,
                                    pos.z + offsetZ,
                                    1, 0.0, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
        }
    }
}