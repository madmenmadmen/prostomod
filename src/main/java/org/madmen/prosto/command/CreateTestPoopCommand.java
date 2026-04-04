package org.madmen.prosto.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.madmen.prosto.ToiletManager;
import org.madmen.prosto.network.NetworkHandler;
import org.madmen.prosto.network.SyncNeedToShitPacket;

import java.util.Collection;

public class CreateTestPoopCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("testpoop")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return triggerNeedToShit(player);
                })
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                            return triggerNeedToShit(players);
                        })
                )
                .then(Commands.literal("instant")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            return instantPoop(player);
                        })
                )
        );
    }

    private static int triggerNeedToShit(ServerPlayer player) {
        ToiletManager.setNeedToShit(player, true);

        NetworkHandler.INSTANCE.send(NetworkHandler.toPlayer(player),
                new SyncNeedToShitPacket(true));

        player.sendSystemMessage(Component.literal("💩 Тестовый режим активирован! Зажми Shift чтобы покакать"));
        return Command.SINGLE_SUCCESS;
    }

    private static int triggerNeedToShit(Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            triggerNeedToShit(player);
        }
        return players.size();
    }

    private static int instantPoop(ServerPlayer player) {
        ToiletManager.setFartRequest(player, true);
        player.sendSystemMessage(Component.literal("💩 Мгновенное какание!"));
        return Command.SINGLE_SUCCESS;
    }
}