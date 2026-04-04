package org.madmen.prosto;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madmen.prosto.command.CreateTestPoopCommand;
import org.madmen.prosto.command.FindPoopOreCommand;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CreateTestPoopCommand.register(event.getDispatcher());
        FindPoopOreCommand.register(event.getDispatcher());
        Prosto.LOGGER.info("★ Команда /testpoop зарегистрирована!");
    }
}