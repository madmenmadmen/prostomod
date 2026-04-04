package org.madmen.prosto.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class FindPoopOreCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("findpoopore")
                .requires(source -> source.hasPermission(2)) // Требует читов/оп-права
                .executes(context -> findPoopOre(context, 50)) // По умолчанию радиус 50
                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 500))
                        .executes(context -> findPoopOre(context,
                                IntegerArgumentType.getInteger(context, "radius")))
                )
        );
    }

    private static int findPoopOre(CommandContext<CommandSourceStack> context, int radius) {
        CommandSourceStack source = context.getSource();
        BlockPos playerPos = BlockPos.containing(source.getPosition());
        Level level = source.getLevel();

        Block poopOre = ModBlocks.POOP_ORE.get();
        List<BlockPos> foundOres = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Рассчитываем границы поиска
        int minX = playerPos.getX() - radius;
        int maxX = playerPos.getX() + radius;
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        int minZ = playerPos.getZ() - radius;
        int maxZ = playerPos.getZ() + radius;

        // Счетчик проверенных блоков для отладки
        int checkedBlocks = 0;

        // Оптимизированный поиск (можно сделать чанками)
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    checkedBlocks++;
                    BlockPos checkPos = new BlockPos(x, y, z);
                    if (level.getBlockState(checkPos).getBlock() == poopOre) {
                        foundOres.add(checkPos);
                    }
                }
            }
        }

        long searchTime = System.currentTimeMillis() - startTime;

        // Формируем сообщение
        Component message = buildResultMessage(playerPos, foundOres, radius, searchTime, checkedBlocks);
        source.sendSuccess(() -> message, false);

        return foundOres.size();
    }

    private static Component buildResultMessage(BlockPos playerPos, List<BlockPos> foundOres,
                                                int radius, long searchTime, int checkedBlocks) {
        Component message = Component.literal("§6===== Поиск какашечной руды =====\n");

        if (foundOres.isEmpty()) {
            message = message.copy()
                    .append(Component.literal("§cВ радиусе §e" + radius + " §cблоков какашечная руда не найдена!\n"))
                    .append(Component.literal("§7Проверено блоков: §f" + checkedBlocks + "\n"))
                    .append(Component.literal("§7Время поиска: §f" + searchTime + " мс\n"))
                    .append(Component.literal("§7Ваши координаты: §fX=" + playerPos.getX() +
                            " Y=" + playerPos.getY() + " Z=" + playerPos.getZ()));
            return message;
        }

        // Находим ближайшую руду
        BlockPos nearest = foundOres.get(0);
        double minDistance = playerPos.distSqr(nearest);

        for (BlockPos pos : foundOres) {
            double distance = playerPos.distSqr(pos);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = pos;
            }
        }

        double distance = Math.sqrt(minDistance);

        message = message.copy()
                .append(Component.literal("§a✓ Найдено: §e" + foundOres.size() + " §aкакашечных руд\n"))
                .append(Component.literal("§6Ближайшая руда:\n"))
                .append(Component.literal("  §7Координаты: §fX=" + nearest.getX() +
                        " Y=" + nearest.getY() + " Z=" + nearest.getZ() + "\n"))
                .append(Component.literal("  §7Расстояние: §f" + String.format("%.1f", distance) + " блоков\n"))
                .append(Component.literal("  §7Направление: §f" + getDirection(playerPos, nearest) + "\n"))
                .append(Component.literal("§6Статистика:\n"))
                .append(Component.literal("  §7Радиус поиска: §f" + radius + " блоков\n"))
                .append(Component.literal("  §7Проверено блоков: §f" + checkedBlocks + "\n"))
                .append(Component.literal("  §7Время поиска: §f" + searchTime + " мс\n"))
                .append(Component.literal("§7Используйте: §f/findpoopore [радиус] §7для изменения радиуса"));

        // Если нашли много руд, показываем еще несколько ближайших
        if (foundOres.size() > 1) {
            message = message.copy()
                    .append(Component.literal("\n§6Еще 3 ближайшие руды:"));

            foundOres.sort((a, b) ->
                    Double.compare(playerPos.distSqr(a), playerPos.distSqr(b)));

            for (int i = 1; i < Math.min(4, foundOres.size()); i++) {
                BlockPos pos = foundOres.get(i);
                double dist = Math.sqrt(playerPos.distSqr(pos));
                message = message.copy()
                        .append(Component.literal("\n  §8" + i + ". §7X=" + pos.getX() +
                                " Y=" + pos.getY() + " Z=" + pos.getZ() +
                                " (§f" + String.format("%.1f", dist) + "§7)"));
            }
        }

        return message;
    }

    private static String getDirection(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dz = to.getZ() - from.getZ();

        if (Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? "→ Восток" : "← Запад";
        } else {
            return dz > 0 ? "↓ Юг" : "↑ Север";
        }
    }
}