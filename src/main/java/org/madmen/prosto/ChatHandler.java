package org.madmen.prosto;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID)
public class ChatHandler {

    private static final Map<UUID, StringBuilder> inputBuffers = new HashMap<>();
    private static final String ACTIVATE_CODE = "ZH";
    private static final String DEACTIVATE_CODE = "_";

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String message = event.getRawText().toUpperCase().replace(" ", "");

        UUID playerId = player.getUUID();
        inputBuffers.putIfAbsent(playerId, new StringBuilder());

        StringBuilder buffer = inputBuffers.get(playerId);
        buffer.append(message);

        String currentBuffer = buffer.toString();
        if (currentBuffer.contains(DEACTIVATE_CODE)) {
            // ShrekModeHandler.setServerShrekMode(player, false);
            buffer.setLength(0);
        } else if (currentBuffer.contains(ACTIVATE_CODE)) {
            // ShrekModeHandler.setServerShrekMode(player, true);
            buffer.setLength(0);
        }

        if (buffer.length() > 10) {
            buffer.delete(0, buffer.length() - 10);
        }
    }
}