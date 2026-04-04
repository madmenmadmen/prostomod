package org.madmen.prosto.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.madmen.prosto.block.entity.AdvancedPoopFurnaceBlockEntity;
import org.madmen.prosto.item.ModItems;

public class AdvancedPoopFurnaceMenu extends AbstractContainerMenu {
    private final AdvancedPoopFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final boolean isClientSide;

    public AdvancedPoopFurnaceMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory,
                getBlockEntity(playerInventory, extraData),
                createClientData());
    }

    public AdvancedPoopFurnaceMenu(int containerId, Inventory playerInventory,
                                   AdvancedPoopFurnaceBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.ADVANCED_POOP_FURNACE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.data = data;
        this.isClientSide = level.isClientSide();

        addSlots(playerInventory);
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        if (data != null && data.getCount() > 0) {
            addDataSlots(data);
        }
    }

    private static ContainerData createClientData() {
        return new ContainerData() {
            private final int[] data = new int[4];

            @Override
            public int get(int index) {
                return index >= 0 && index < data.length ? data[index] : 0;
            }

            @Override
            public void set(int index, int value) {
                if (index >= 0 && index < data.length) {
                    data[index] = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    private static AdvancedPoopFurnaceBlockEntity getBlockEntity(Inventory playerInventory, FriendlyByteBuf extraData) {
        if (extraData == null) {
            return null;
        }

        Level level = playerInventory.player.level();
        BlockPos pos = extraData.readBlockPos();

        if (level.getBlockEntity(pos) instanceof AdvancedPoopFurnaceBlockEntity blockEntity) {
            return blockEntity;
        }
        return null;
    }

    private void addSlots(Inventory playerInventory) {
        if (isClientSide || blockEntity == null) {
            this.addSlot(new Slot(new SimpleContainer(1), 0, 56, 17) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == ModItems.POOP.get();
                }
            });

            this.addSlot(new Slot(new SimpleContainer(1), 0, 56, 53) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return AbstractFurnaceBlockEntity.isFuel(stack);
                }
            });

            this.addSlot(new Slot(new SimpleContainer(1), 0, 116, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return true;
                }
            });

            this.addSlot(new Slot(new SimpleContainer(1), 0, 117, 64) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == Items.BUCKET;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        } else {
            this.addSlot(new Slot(blockEntity, 0, 56, 17) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == ModItems.POOP.get();
                }
            });

            this.addSlot(new Slot(blockEntity, 1, 56, 53) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return AbstractFurnaceBlockEntity.isFuel(stack);
                }
            });

            this.addSlot(new Slot(blockEntity, 2, 116, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return true;
                }
            });

            this.addSlot(new Slot(blockEntity, 3, 117, 64) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == Items.BUCKET;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() == ModItems.POOP.get()) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (AbstractFurnaceBlockEntity.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() == Items.BUCKET) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 4 && index < 31) {
                    if (!this.moveItemStackTo(itemstack1, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 31 && index < 40) {
                    if (!this.moveItemStackTo(itemstack1, 4, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (isClientSide) {
            return true;
        }
        if (blockEntity == null) {
            return false;
        }
        return this.blockEntity.stillValid(player);
    }

    public boolean isLit() {
        return data.get(0) > 0;
    }

    public int getBurnProgress() {
        int i = data.get(2);
        int j = data.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    public int getLitProgress() {
        int i = data.get(1);
        if (i == 0) {
            i = 200;
        }
        return data.get(0) * 13 / i;
    }
}