package org.madmen.prosto.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.madmen.prosto.block.entity.BiofuelGeneratorBlockEntity;
import org.madmen.prosto.item.ModItems;

public class BiofuelGeneratorMenu extends AbstractContainerMenu {
    private final BiofuelGeneratorBlockEntity blockEntity;
    private final Level level;
    private final boolean isClientSide;

    // ЕДИНСТВЕННЫЙ КОНСТРУКТОР — для Forge
    public BiofuelGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(ModMenuTypes.BIOFUEL_GENERATOR_MENU.get(), containerId);
        this.level = playerInventory.player.level();
        this.isClientSide = level.isClientSide();

        if (isClientSide) {
            // Клиент: заглушки
            this.blockEntity = null;
            addSlots(playerInventory, null);
        } else {
            // Сервер: получаем BlockEntity
            BlockPos pos = extraData.readBlockPos();
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BiofuelGeneratorBlockEntity) {
                this.blockEntity = (BiofuelGeneratorBlockEntity) be;
                addSlots(playerInventory, blockEntity);
            } else {
                this.blockEntity = null;
                addSlots(playerInventory, null); // fallback
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addSlots(Inventory playerInventory, BiofuelGeneratorBlockEntity be) {
        if (be == null) {
            // Заглушки
            this.addSlot(new Slot(new SimpleContainer(1), 0, 85, 40) {
                @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() == ModItems.POOP.get(); }
            });
        } else {
            // Настоящие слоты
            this.addSlot(new Slot(be.getInventory(), 0, 85, 40) {
                @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() == ModItems.POOP.get(); }
            });
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i)
            for (int l = 0; l < 9; ++l)
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (isClientSide) return true;
        return blockEntity != null && blockEntity.stillValid(player);
    }
}