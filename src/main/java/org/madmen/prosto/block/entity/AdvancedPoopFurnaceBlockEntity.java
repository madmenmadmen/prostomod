package org.madmen.prosto.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import javax.annotation.Nullable;
import org.madmen.prosto.item.ModItems;
import org.madmen.prosto.screen.AdvancedPoopFurnaceMenu;

public class AdvancedPoopFurnaceBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {

    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 3};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};

    protected NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    protected int litTime;
    protected int litDuration;
    protected int cookingProgress;
    protected int cookingTotalTime = 200;

    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> AdvancedPoopFurnaceBlockEntity.this.litTime;
                case 1 -> AdvancedPoopFurnaceBlockEntity.this.litDuration;
                case 2 -> AdvancedPoopFurnaceBlockEntity.this.cookingProgress;
                case 3 -> AdvancedPoopFurnaceBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0 -> AdvancedPoopFurnaceBlockEntity.this.litTime = value;
                case 1 -> AdvancedPoopFurnaceBlockEntity.this.litDuration = value;
                case 2 -> AdvancedPoopFurnaceBlockEntity.this.cookingProgress = value;
                case 3 -> AdvancedPoopFurnaceBlockEntity.this.cookingTotalTime = value;
            }
        }

        public int getCount() {
            return 4;
        }
    };

    public AdvancedPoopFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_POOP_FURNACE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.prosto.advanced_poop_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AdvancedPoopFurnaceMenu(id, inventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemstack = this.items.get(slot);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack);
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (!flag) {
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D,
                    (double)this.worldPosition.getY() + 0.5D,
                    (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else if (side == Direction.UP) {
            return SLOTS_FOR_UP;
        } else {
            return SLOTS_FOR_SIDES;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 0) {
            return stack.getItem() == ModItems.POOP.get();
        } else if (index == 1) {
            return net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.isFuel(stack);
        } else if (index == 3) {
            return stack.getItem() == Items.BUCKET;
        } else {
            return false;
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AdvancedPoopFurnaceBlockEntity blockEntity) {
        boolean wasLit = blockEntity.litTime > 0;
        boolean changed = false;

        if (blockEntity.litTime > 0) {
            blockEntity.litTime--;
        }

        if (!level.isClientSide) {
            ItemStack fuel = blockEntity.items.get(1);
            ItemStack input = blockEntity.items.get(0);
            ItemStack bucket = blockEntity.items.get(3);
            ItemStack output = blockEntity.items.get(2);

            if (blockEntity.litTime > 0 || !fuel.isEmpty() && !input.isEmpty() && bucket.getItem() == Items.BUCKET && output.isEmpty()) {
                if (blockEntity.litTime <= 0 && blockEntity.canBurn()) {
                    blockEntity.litTime = blockEntity.getBurnDuration(fuel);
                    blockEntity.litDuration = blockEntity.litTime;
                    if (blockEntity.litTime > 0) {
                        changed = true;
                        if (fuel.hasCraftingRemainingItem()) {
                            blockEntity.items.set(1, fuel.getCraftingRemainingItem());
                        } else if (!fuel.isEmpty()) {
                            fuel.shrink(1);
                            if (fuel.isEmpty()) {
                                blockEntity.items.set(1, fuel.getCraftingRemainingItem());
                            }
                        }
                    }
                }

                if (blockEntity.litTime > 0 && blockEntity.canBurn()) {
                    blockEntity.cookingProgress++;
                    if (blockEntity.cookingProgress >= blockEntity.cookingTotalTime) {
                        blockEntity.cookingProgress = 0;
                        blockEntity.burn();
                        changed = true;
                    }
                } else {
                    blockEntity.cookingProgress = 0;
                }
            } else if (blockEntity.cookingProgress > 0) {
                blockEntity.cookingProgress = 0;
            }

            if (wasLit != blockEntity.litTime > 0) {
                changed = true;
                level.setBlock(pos, state.setValue(BlockStateProperties.LIT, blockEntity.litTime > 0), 3);
            }
        }

        if (changed) {
            blockEntity.setChanged();
        }
    }

    private boolean canBurn() {
        ItemStack input = this.items.get(0);
        ItemStack bucket = this.items.get(3);
        ItemStack output = this.items.get(2);

        return input.getItem() == ModItems.POOP.get() &&
                bucket.getItem() == Items.BUCKET &&
                output.isEmpty();
    }

    private void burn() {
        if (this.canBurn()) {
            ItemStack input = this.items.get(0);
            ItemStack bucket = this.items.get(3);

            // Здесь должна быть логика создания жидкого говна
            ItemStack result = new ItemStack(ModItems.LIQUID_POOP_BUCKET.get());
            this.items.set(2, result);

            input.shrink(1);
            bucket.shrink(1);
        }
    }

    private int getBurnDuration(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            return net.minecraftforge.common.ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.litTime = tag.getInt("BurnTime");
        this.cookingProgress = tag.getInt("CookTime");
        this.cookingTotalTime = tag.getInt("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BurnTime", this.litTime);
        tag.putInt("CookTime", this.cookingProgress);
        tag.putInt("CookTimeTotal", this.cookingTotalTime);
        ContainerHelper.saveAllItems(tag, this.items);
    }
}