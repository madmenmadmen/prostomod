package org.madmen.prosto.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.registries.ForgeRegistries;
import org.madmen.prosto.item.ModItems;
import java.util.List;

public class PoopOreBlock extends Block {
    public PoopOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        System.out.println("=== GET DROPS CALLED ===");

        // 1. Проверяем, зарегистрирован ли предмет
        System.out.println("UNIQUE_POOP exists: " + ModItems.UNIQUE_POOP.isPresent());

        if (ModItems.UNIQUE_POOP.isPresent()) {
            Item uniquePoop = ModItems.UNIQUE_POOP.get();
            System.out.println("UNIQUE_POOP item: " + uniquePoop);
            System.out.println("UNIQUE_POOP registry name: " + ForgeRegistries.ITEMS.getKey(uniquePoop));
        } else {
            System.out.println("ERROR: UNIQUE_POOP not registered!");
        }

        // 2. Проверяем стандартный дроп
        List<ItemStack> drops = super.getDrops(state, builder);
        System.out.println("Super drops count: " + drops.size());

        // 3. Пробуем создать ItemStack вручную
        if (ModItems.UNIQUE_POOP.isPresent()) {
            ItemStack manualStack = new ItemStack(ModItems.UNIQUE_POOP.get());
            System.out.println("Manual stack created: " + manualStack);

            // Добавляем ручной дроп К стандартному
            drops.add(manualStack);
            System.out.println("Added manual drop!");
        }

        return drops;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);

        if (!level.isClientSide && !player.isCreative()) {
            // Спавним предмет независимо от лутаблицы
            ItemEntity item = new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    new ItemStack(ModItems.UNIQUE_POOP.get())
            );
            level.addFreshEntity(item);
        }
    }
}