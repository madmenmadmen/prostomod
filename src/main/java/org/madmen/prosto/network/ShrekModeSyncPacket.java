package org.madmen.prosto.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.madmen.prosto.Prosto;

import java.util.function.Supplier;

public class ShrekModeSyncPacket {
    private final boolean shrekModeActive;

    public ShrekModeSyncPacket(boolean shrekModeActive) {
        this.shrekModeActive = shrekModeActive;
    }

    public ShrekModeSyncPacket(FriendlyByteBuf buf) {
        this.shrekModeActive = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(shrekModeActive);
    }

    public boolean isShrekModeActive() {
        return shrekModeActive;
    }

    public static void handle(ShrekModeSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                Prosto.LOGGER.info("★ ShrekMode синхронизирован: {}", packet.isShrekModeActive());

                if (packet.isShrekModeActive()) {
                    awardShrekAdvancement(player);
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void awardShrekAdvancement(ServerPlayer player) {
        var advancement = player.getServer().getAdvancements()
                .getAdvancement(new ResourceLocation("prosto", "shrek_mode"));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "activated");
            Prosto.LOGGER.info("★ Достижение Shrek Mode выдано игроку {}", player.getName().getString());
        } else {
            Prosto.LOGGER.error("★ Достижение Shrek Mode не найдено!");
        }
    }
}