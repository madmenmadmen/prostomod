package org.madmen.prosto.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.ToiletManager;
import org.madmen.prosto.network.FartRequestPacket;
import org.madmen.prosto.network.NetworkHandler;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {
    private static boolean wasShifting = false;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Простая проверка
        if (!ToiletManager.needToShit) return;

        int x = event.getGuiGraphics().guiWidth() / 2 + 91;
        int y = event.getGuiGraphics().guiHeight() - 40;
        event.getGuiGraphics().drawString(mc.font, "Пора срать!", x, y, 0xFFFF00, true);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        boolean isShifting = player.isShiftKeyDown();

        if (isShifting && !wasShifting) {
            if (ShrekModeHandler.shrekModeActive) {
                NetworkHandler.sendToServer(new FartRequestPacket(true));
            } else if (ToiletManager.needToShit) {
                NetworkHandler.sendToServer(new FartRequestPacket(false));
            }
        }

        wasShifting = isShifting;
    }
}