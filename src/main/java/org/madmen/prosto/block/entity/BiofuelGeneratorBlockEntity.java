package org.madmen.prosto.block.entity;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.madmen.prosto.block.BiofuelGeneratorBlock;
import org.madmen.prosto.fluid.ModFluids;
import org.madmen.prosto.item.ModItems;
import org.madmen.prosto.screen.BiofuelGeneratorMenu;

public class BiofuelGeneratorBlockEntity extends BlockEntity implements MenuProvider {

    private final FluidTank outputTank = new FluidTank(20000,
            fluidStack -> fluidStack.getFluid() == ModFluids.BIOFUEL.get());
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> outputTank);

    private int poopAmount = 0;
    private static final int MAX_POOP_CAPACITY = 8;

    // Инвентарь для слотов GUI
    private final SimpleContainer inventory = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            BiofuelGeneratorBlockEntity.this.setChanged();
        }
    };

    public BiofuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BIOFUEL_GENERATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BiofuelGeneratorBlockEntity generator) {
        if (level.isClientSide) return;

        boolean wasWorking = state.getValue(BiofuelGeneratorBlock.WORKING);
        boolean isWorking = false;
        boolean changed = false;

        // === Забираем предметы из слотов ===
        ItemStack slot0 = generator.inventory.getItem(0);
        if (!slot0.isEmpty() && slot0.getItem() == ModItems.POOP.get()) {
            if (generator.poopAmount < MAX_POOP_CAPACITY) {
                generator.poopAmount++;
                slot0.shrink(1);
                changed = true;
            }
        }

        ItemStack slot1 = generator.inventory.getItem(1);
        if (!slot1.isEmpty() && slot1.getItem() == ModItems.UNIQUE_POOP.get()) {
            int space = MAX_POOP_CAPACITY - generator.poopAmount;
            if (space > 0) {
                int take = Math.min(2, space);
                generator.poopAmount += take;
                slot1.shrink(1);
                changed = true;
            }
        }

        // === Переработка в биотопливо ===
        if (generator.poopAmount > 0 && generator.outputTank.getSpace() >= 250) {
            generator.poopAmount--;
            generator.outputTank.fill(new FluidStack(ModFluids.BIOFUEL.get(), 250), IFluidHandler.FluidAction.EXECUTE);
            isWorking = true;
            changed = true;
        }

        // === Обновляем blockstate ТОЛЬКО если изменилось WORKING ===
        if (wasWorking != isWorking) {
            System.out.println("WORKING changed to: " + isWorking);
            BlockState newState = state.setValue(BiofuelGeneratorBlock.WORKING, isWorking);
            level.setBlockAndUpdate(pos, newState); // ← Это гарантирует обновление клиента
            changed = true;
        }

        if (changed) {
            generator.setChanged(); // ← обязательно для NBT и GUI
        }
    }

    // === ВЗАИМОДЕЙСТВИЕ С БЛОКОМ ===
    public void onRightClick(Player player, InteractionHand hand, boolean isShiftDown) {
        if (isShiftDown && player.getItemInHand(hand).isEmpty()) {
            int amount = this.outputTank.getFluidAmount();
            player.sendSystemMessage(Component.literal("Биотопливо: " + amount + " mB"));
            return;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() == Items.BUCKET && heldItem.getCount() >= 1) {
            if (this.outputTank.getFluidAmount() >= 1000) {
                // Убираем 1 пустое ведро
                heldItem.shrink(1);

                // Создаём ведро с биотопливом
                ItemStack biofuelBucket = new ItemStack(ModItems.BIOFUEL_BUCKET.get(), 1);

                // Пытаемся положить в руку или в инвентарь
                if (!player.getInventory().add(biofuelBucket)) {
                    // Если инвентарь полон — выбрасываем на землю
                    player.drop(biofuelBucket, false);
                }

                // Вычитаем жидкость
                this.outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                this.setChanged();
            }
        }
    }

    // === ГЕТТЕРЫ ===
    public SimpleContainer getInventory() {
        return inventory;
    }

    public FluidTank getOutputTank() {
        return outputTank;
    }

    public int getPoopAmount() {
        return poopAmount;
    }

    public int getMaxPoopCapacity() {
        return MAX_POOP_CAPACITY;
    }

    // === NBT ===
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        outputTank.readFromNBT(tag.getCompound("outputTank"));
        poopAmount = tag.getInt("poopAmount");
        inventory.fromTag(tag.getList("Inventory", 10));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("outputTank", outputTank.writeToNBT(new CompoundTag()));
        tag.putInt("poopAmount", poopAmount);
        tag.put("Inventory", inventory.createTag());
    }

    // === CAPABILITIES ===
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandler.invalidate();
    }

    // === MenuProvider ===
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.biofuel_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new BiofuelGeneratorMenu(id, playerInventory,
                new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(getBlockPos()));
    }

    // === stillValid ===
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(Vec3.atCenterOf(this.worldPosition)) <= 64.0D;
    }
}