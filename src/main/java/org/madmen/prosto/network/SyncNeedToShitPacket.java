package org.madmen.prosto.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.ToiletManager;

import java.util.function.Supplier;

public class SyncNeedToShitPacket {
    private final boolean needToShit;

    public SyncNeedToShitPacket(boolean needToShit) {
        this.needToShit = needToShit;
    }

    public SyncNeedToShitPacket(FriendlyByteBuf buf) {
        this.needToShit = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(needToShit);
    }

    public boolean isNeedToShit() {
        return needToShit;
    }

    public static void handle(SyncNeedToShitPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Prosto.LOGGER.info("★ Синхронизация needToShit: {}", packet.isNeedToShit());
            ToiletManager.needToShit = packet.isNeedToShit();
        });
        context.get().setPacketHandled(true);
    }
}