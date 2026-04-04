package org.madmen.prosto.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.madmen.prosto.Prosto;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Prosto.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(packetId++,
                FartRequestPacket.class,
                FartRequestPacket::toBytes,
                FartRequestPacket::new,
                FartRequestPacket::handle
        );

        INSTANCE.registerMessage(packetId++,
                ShrekModeSyncPacket.class,
                ShrekModeSyncPacket::toBytes,
                ShrekModeSyncPacket::new,
                ShrekModeSyncPacket::handle
        );

        INSTANCE.registerMessage(packetId++,
                SyncNeedToShitPacket.class,
                SyncNeedToShitPacket::toBytes,
                SyncNeedToShitPacket::new,
                SyncNeedToShitPacket::handle
        );

        INSTANCE.registerMessage(packetId++,
                ShrekModeAchievementPacket.class,
                ShrekModeAchievementPacket::toBytes,
                ShrekModeAchievementPacket::new,
                ShrekModeAchievementPacket::handle
        );

        Prosto.LOGGER.info("★ Сетевые пакеты зарегистрированы");
    }

    public static final PacketDistributor.PacketTarget toPlayer(ServerPlayer player) {
        return PacketDistributor.PLAYER.with(() -> player);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}