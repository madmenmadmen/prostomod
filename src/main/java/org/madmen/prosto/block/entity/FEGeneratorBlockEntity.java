package org.madmen.prosto.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.madmen.prosto.block.FEGeneratorBlock;
import org.madmen.prosto.fluid.ModFluids;

import javax.annotation.Nullable;

public class FEGeneratorBlockEntity extends BlockEntity {

    private final EnergyStorage energyStorage = new EnergyStorage(100000, 1000, 100);
    private final FluidTank inputTank = new FluidTank(6000,
            fluidStack -> fluidStack.getFluid() == ModFluids.BIOFUEL.get()) {

        @Override
        protected void onContentsChanged() {
            setChanged(); // КРИТИЧНО!
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            // Дублируем проверку здесь тоже
            return stack.getFluid() == ModFluids.BIOFUEL.get();
        }
    };

    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> inputTank);

    private int biofuelAmount = 0; // В миллибукетах (MB)
    private int generationPerMB = 20; // FE на 1 MB биотоплива
    private int currentGeneration = 0;

    public FEGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FE_GENERATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FEGeneratorBlockEntity generator) {
        if (level.isClientSide) return;

        boolean wasActive = state.getValue(FEGeneratorBlock.ACTIVE);
        boolean isActive = false;

        // Обновляем количество биотоплива из бака
        generator.biofuelAmount = generator.inputTank.getFluidAmount();

        // Генерация энергии
        if (generator.biofuelAmount > 0 && generator.energyStorage.getEnergyStored() < generator.energyStorage.getMaxEnergyStored()) {
            // Потребляем биотопливо
            int fuelToConsume = Math.min(10, generator.biofuelAmount); // 10 MB/тик
            generator.inputTank.drain(fuelToConsume, IFluidHandler.FluidAction.EXECUTE);
            generator.biofuelAmount = generator.inputTank.getFluidAmount();

            // Генерируем энергию
            generator.currentGeneration = fuelToConsume * generator.generationPerMB;
            generator.energyStorage.receiveEnergy(generator.currentGeneration, false);

            isActive = true;
        } else {
            generator.currentGeneration = 0;
        }

        // Обновление состояния
        if (wasActive != isActive) {
            level.setBlock(pos, state.setValue(FEGeneratorBlock.ACTIVE, isActive), 3);
        }

        generator.setChanged();
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract, simulate);
    }

    public FluidTank getInputTank() {
        return inputTank;
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.get("energy"));
        inputTank.readFromNBT(tag.getCompound("inputTank"));
        biofuelAmount = tag.getInt("biofuelAmount");
        currentGeneration = tag.getInt("currentGeneration");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inputTank", inputTank.writeToNBT(new CompoundTag()));
        tag.putInt("biofuelAmount", biofuelAmount);
        tag.putInt("currentGeneration", currentGeneration);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            // Энергию можно получать со всех сторон
            return energyHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            // Жидкость можно заливать со всех сторон
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
        fluidHandler.invalidate();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();  // Сохраняет все данные для клиента
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);  // Загружает данные на клиенте
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);  // Создаёт пакет для обновления
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());  // Обрабатывает входящий пакет на клиенте
    }
}