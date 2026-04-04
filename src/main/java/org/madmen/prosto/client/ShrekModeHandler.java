package org.madmen.prosto.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.network.NetworkHandler;
import org.madmen.prosto.network.ShrekModeAchievementPacket;
import org.madmen.prosto.network.ShrekModeSyncPacket;

import java.util.LinkedList;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID, value = Dist.CLIENT)
public class ShrekModeHandler {
    private static final char[] ACTIVATE_CODE = {'Z', 'H'};
    private static final char[] DEACTIVATE_CODE = {'_'};
    private static final Queue<Character> inputBuffer = new LinkedList<>();
    private static final long TIMEOUT_NS = 2_000_000_000L;
    public static boolean shrekModeActive = false;
    private static long lastKeyPress = 0;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;

        long now = System.nanoTime();
        if (now - lastKeyPress > TIMEOUT_NS) {
            inputBuffer.clear();
        }
        lastKeyPress = now;

        char c = getCharForKey(event.getKey());
        if (c == 0) return;

        inputBuffer.offer(c);

        int maxLength = Math.max(ACTIVATE_CODE.length, DEACTIVATE_CODE.length);
        while (inputBuffer.size() > maxLength) {
            inputBuffer.poll();
        }

        if (matches(DEACTIVATE_CODE)) {
            shrekModeActive = false;
            inputBuffer.clear();
            Minecraft.getInstance().player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Шрек-режим отключён."),
                    true
            );
            NetworkHandler.sendToServer(new ShrekModeSyncPacket(false));
        }

        if (matches(ACTIVATE_CODE)) {
            shrekModeActive = true;
            inputBuffer.clear();

            Minecraft.getInstance().player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Шрек-режим активирован! Зажми Shift, чтобы срать как бог 💩🚀"),
                    true
            );

            triggerShrekModeAchievement();
            NetworkHandler.sendToServer(new ShrekModeSyncPacket(true));
        }
    }

    private static void triggerShrekModeAchievement() {
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        // Для мультиплеера отправляем пакет
        if (!mc.isSingleplayer()) {
            NetworkHandler.sendToServer(new ShrekModeAchievementPacket());
            return;
        }

        // Для одиночной игры
        if (mc.isSingleplayer()) {
            var server = mc.getSingleplayerServer();
            if (server != null) {
                var serverPlayer = server.getPlayerList().getPlayer(player.getUUID());
                if (serverPlayer != null) {
                    // Используем метод из пакета
                    ShrekModeSyncPacket.awardShrekAdvancement(serverPlayer);
                }
            }
        }
    }

    private static boolean matches(char[] pattern) {
        if (inputBuffer.size() < pattern.length) return false;
        var list = new java.util.ArrayList<>(inputBuffer);
        int start = list.size() - pattern.length;
        for (int i = 0; i < pattern.length; i++) {
            if (list.get(start + i) != pattern[i]) {
                return false;
            }
        }
        return true;
    }

    private static char getCharForKey(int key) {
        if (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) {
            return (char) ('A' + (key - GLFW.GLFW_KEY_A));
        }
        if (key == GLFW.GLFW_KEY_MINUS) {
            return '_';
        }
        return 0;
    }
}