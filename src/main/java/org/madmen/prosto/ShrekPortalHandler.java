package org.madmen.prosto;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madmen.prosto.block.ModBlocks;
import org.madmen.prosto.dimension.ShrekDimension;

@Mod.EventBusSubscriber(modid = Prosto.MOD_ID)
public class ShrekPortalHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(ModBlocks.DIAMOND_POOP_PORTAL_CORE.get())) {
            if (!level.isClientSide) {
                if (isValidSimplePortal(level, pos)) {
                    activatePortal((ServerPlayer) player);
                    player.sendSystemMessage(Component.literal("Телепортация в Болото Шрека! 💩"));
                } else {
                    player.sendSystemMessage(Component.literal("Поставьте по одному блоку какашек с двух сторон от ядра!"));
                }
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
            event.setCanceled(true);
        }
    }

    private static boolean isValidSimplePortal(Level level, BlockPos corePos) {
        BlockPos northPos = corePos.north();
        BlockPos southPos = corePos.south();
        BlockPos eastPos = corePos.east();
        BlockPos westPos = corePos.west();

        boolean hasNorthPoop = false;
        boolean hasSouthPoop = false;
        boolean hasEastPoop = false;
        boolean hasWestPoop = false;

        if (level.getBlockState(northPos).is(ModBlocks.POOP_BLOCK.get())) hasNorthPoop = true;
        if (level.getBlockState(southPos).is(ModBlocks.POOP_BLOCK.get())) hasSouthPoop = true;
        if (level.getBlockState(eastPos).is(ModBlocks.POOP_BLOCK.get())) hasEastPoop = true;
        if (level.getBlockState(westPos).is(ModBlocks.POOP_BLOCK.get())) hasWestPoop = true;

        int poopCount = 0;
        if (hasNorthPoop) poopCount++;
        if (hasSouthPoop) poopCount++;
        if (hasEastPoop) poopCount++;
        if (hasWestPoop) poopCount++;

        return poopCount >= 2;
    }

    private static void activatePortal(ServerPlayer player) {
        ServerLevel currentLevel = (ServerLevel) player.level();

        if (currentLevel.dimension() == ShrekDimension.SHREK_LEVEL) {
            ServerLevel overworld = player.server.overworld();
            player.teleportTo(overworld,
                    player.getX(), player.getY(), player.getZ(),
                    player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.literal("Возвращаемся в обычный мир!"));
        } else {
            ServerLevel shrekLevel = player.server.getLevel(ShrekDimension.SHREK_LEVEL);
            if (shrekLevel != null) {
                BlockPos safePos = findSafeTeleportPosition(shrekLevel,
                        (int)player.getX(), (int)player.getY(), (int)player.getZ());

                player.teleportTo(shrekLevel,
                        safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5,
                        player.getYRot(), player.getXRot());
                player.sendSystemMessage(Component.literal("Добро пожаловать в Болото Шрека! 💚"));
            } else {
                player.sendSystemMessage(Component.literal("Ошибка: измерение Шрека не загружено!"));
            }
        }
    }

    private static BlockPos findSafeTeleportPosition(ServerLevel level, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, 65, z);

        for (int attempt = 0; attempt < 10; attempt++) {
            if (level.getBlockState(pos).isAir() &&
                    !level.getBlockState(pos.below()).isAir()) {
                return pos;
            }
            pos = pos.above();
        }

        return new BlockPos(x, 65, z);
    }
}