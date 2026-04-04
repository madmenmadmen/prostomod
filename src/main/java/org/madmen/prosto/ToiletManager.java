package org.madmen.prosto;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.madmen.prosto.item.ModItems;
import org.madmen.prosto.network.NetworkHandler;
import org.madmen.prosto.network.SyncNeedToShitPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID)
public class ToiletManager {
    public static final int NEED_TO_SHIT_TIME = 900;

    // Старые поля для одиночной игры
    public static int shitTimer = 0;
    public static boolean needToShit = false;

    // Новые поля для сервера
    private static Map<UUID, Integer> serverShitTimers = new HashMap<>();
    private static Map<UUID, Boolean> serverNeedToShit = new HashMap<>();
    private static Map<UUID, Boolean> serverFartRequests = new HashMap<>();
    private static Map<UUID, Boolean> serverSuperFartRequests = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        UUID playerId = player.getUUID();

        // Инициализация
        serverShitTimers.putIfAbsent(playerId, 0);
        serverNeedToShit.putIfAbsent(playerId, false);

        int timer = serverShitTimers.get(playerId);
        timer++;

        // Отправляем пакет при изменении состояния
        boolean oldNeedToShit = serverNeedToShit.get(playerId);
        if (timer >= NEED_TO_SHIT_TIME && !oldNeedToShit) {
            serverNeedToShit.put(playerId, true);

            // Отправляем пакет синхронизации клиенту
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncNeedToShitPacket(true));
            Prosto.LOGGER.info("★ Отправлен SyncNeedToShitPacket(true) игроку {}", player.getName().getString());

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.PORAKAKATE.get(), SoundSource.PLAYERS, 1.0F, 0.8F
            );

            // Уведомление игрока
            player.sendSystemMessage(Component.literal("💩 Тебе пора срать! Зажми Shift чтобы покакать"));
        }

        serverShitTimers.put(playerId, timer);

        // Обработка какания
        handleFart(player);
        handleSuperFart(player);
    }

    private static void handleFart(ServerPlayer player) {
        boolean shouldFart = serverFartRequests.getOrDefault(player.getUUID(), false);
        boolean currentNeedToShit = serverNeedToShit.getOrDefault(player.getUUID(), false);

        if (currentNeedToShit && shouldFart) {
            // Сброс флагов
            serverFartRequests.put(player.getUUID(), false);
            serverNeedToShit.put(player.getUUID(), false);
            serverShitTimers.put(player.getUUID(), 0);

            // Звук и спавн какашки
            player.level().playSound(
                    null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.FART.get(), SoundSource.PLAYERS, 1.0F, 0.8F
            );

            ItemStack poop = new ItemStack(ModItems.POOP.get());
            ItemEntity item = new ItemEntity(
                    player.level(), player.getX(), player.getY() + 0.5, player.getZ(), poop
            );
            item.setDeltaMovement(0, 0.2, 0);
            player.level().addFreshEntity(item);

            // Отправляем пакет сброса клиенту
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncNeedToShitPacket(false));

            player.sendSystemMessage(Component.literal("💩 Вы успешно сходили по-большому!"));
        }
    }

    private static void handleSuperFart(ServerPlayer player) {
        boolean shouldSuperFart = serverSuperFartRequests.getOrDefault(player.getUUID(), false);

        if (shouldSuperFart) {
            serverSuperFartRequests.put(player.getUUID(), false);

            // Взлёт вверх
            player.push(0, 1.2, 0);

            // 21 какашка
            for (int i = 0; i < 21; i++) {
                double angle = Math.random() * Math.PI * 2;
                double speed = 0.2 + Math.random() * 0.5;
                double dx = Math.cos(angle) * speed;
                double dz = Math.sin(angle) * speed;
                double dy = 0.1 + Math.random() * 0.4;

                ItemStack poop = new ItemStack(ModItems.POOP.get());
                ItemEntity item = new ItemEntity(
                        player.level(),
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        poop
                );
                item.setDeltaMovement(dx, dy, dz);
                player.level().addFreshEntity(item);
            }

            // Звук
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.FART.get(), SoundSource.PLAYERS, 1.0F, 0.7F + (float) Math.random() * 0.3F);

            player.sendSystemMessage(Component.literal("💩💩💩 ШРЕК СРЁТ! 💩💩💩"));
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 0));
        }
    }

    // Методы для установки флагов
    public static void setFartRequest(ServerPlayer player, boolean value) {
        serverFartRequests.put(player.getUUID(), value);
    }

    public static void setSuperFartRequest(ServerPlayer player, boolean value) {
        serverSuperFartRequests.put(player.getUUID(), value);
    }

    public static void setNeedToShit(ServerPlayer player, boolean value) {
        UUID playerId = player.getUUID();
        serverNeedToShit.put(playerId, value);
        serverShitTimers.put(playerId, value ? NEED_TO_SHIT_TIME : 0);
    }
}