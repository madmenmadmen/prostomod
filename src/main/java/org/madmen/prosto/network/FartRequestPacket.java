package org.madmen.prosto.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.ToiletManager;

import java.util.function.Supplier;

public class FartRequestPacket {
    private final boolean isSuperFart;

    public FartRequestPacket(boolean isSuperFart) {
        this.isSuperFart = isSuperFart;
    }

    public FartRequestPacket(FriendlyByteBuf buf) {
        this.isSuperFart = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isSuperFart);
    }

    public boolean isSuperFart() {
        return isSuperFart;
    }

    public static void handle(FartRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                Prosto.LOGGER.info("★★★ ПАКЕТ ПОЛУЧЕН! Игрок: {}, Супер-какание: {}",
                        player.getName().getString(), packet.isSuperFart());

                if (packet.isSuperFart()) {
                    ToiletManager.setSuperFartRequest(player, true);
                } else {
                    ToiletManager.setFartRequest(player, true);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}