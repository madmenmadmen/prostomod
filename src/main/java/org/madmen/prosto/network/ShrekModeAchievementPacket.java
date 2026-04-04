package org.madmen.prosto.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShrekModeAchievementPacket {
    public ShrekModeAchievementPacket() {}

    public ShrekModeAchievementPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public static void handle(ShrekModeAchievementPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                // Переиспользуем метод из ShrekModeSyncPacket
                ShrekModeSyncPacket.awardShrekAdvancement(player);
            }
        });
        context.get().setPacketHandled(true);
    }
}