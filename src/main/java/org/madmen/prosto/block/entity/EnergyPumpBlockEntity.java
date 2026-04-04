package org.madmen.prosto.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.block.EnergyPumpBlock;

public class EnergyPumpBlockEntity extends BlockEntity {

    private final EnergyStorage energyStorage = new EnergyStorage(5000, 1000, 1000, 0);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    private int transferTick = 0;

    public EnergyPumpBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_PUMP.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnergyPumpBlockEntity pump) {
        if (level.isClientSide) return;

        pump.transferTick++;
        if (pump.transferTick >= 2) { // Каждые 2 тика
            pump.performTransfer();
            pump.transferTick = 0;
        }
    }

    private void performTransfer() {
        if (level == null) return;

        BlockState state = getBlockState();
        EnergyPumpBlock.PumpMode mode = state.getValue(EnergyPumpBlock.MODE);

        if (mode == EnergyPumpBlock.PumpMode.NONE) return;

        Direction facing = state.getValue(EnergyPumpBlock.FACING);

        // 1. Блок, на который прикреплена помпа
        BlockPos attachedPos = getBlockPos().relative(facing.getOpposite());
        BlockEntity attachedBlock = level.getBlockEntity(attachedPos);

        // 2. Ищем кабели или другие блоки с других сторон
        for (Direction direction : Direction.values()) {
            if (direction == facing) continue; // Пропускаем сторону прикрепления

            BlockPos neighborPos = getBlockPos().relative(direction);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);

            if (neighbor == null) continue;

            if (mode == EnergyPumpBlock.PumpMode.PUSH) {
                // PUSH: из прикреплённого блока → в кабели
                if (attachedBlock != null) {
                    transferBetween(attachedBlock, neighbor, facing, direction.getOpposite());
                }
            } else if (mode == EnergyPumpBlock.PumpMode.PULL) {
                // PULL: из кабелей → в прикреплённый блок
                if (attachedBlock != null) {
                    transferBetween(neighbor, attachedBlock, direction.getOpposite(), facing);
                }
            }
        }
    }

    private void transferBetween(BlockEntity sourceEntity, BlockEntity targetEntity,
                                 Direction sourceSide, Direction targetSide) {

        sourceEntity.getCapability(ForgeCapabilities.ENERGY, sourceSide).ifPresent(source -> {
            targetEntity.getCapability(ForgeCapabilities.ENERGY, targetSide).ifPresent(target -> {
                // Пытаемся передать энергию
                int maxTransfer = 1000; // FE/тик

                // Сколько можем извлечь из источника
                int canExtract = source.extractEnergy(maxTransfer, true);
                if (canExtract <= 0) return;

                // Сколько может принять цель
                int canReceive = target.receiveEnergy(canExtract, true);
                if (canReceive <= 0) return;

                // Реальная передача
                int extracted = source.extractEnergy(canReceive, false);
                if (extracted > 0) {
                    target.receiveEnergy(extracted, false);
                    setChanged();
                }
            });
        });
    }

    // Геттеры для GUI/рендера
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public float getEnergyFillRatio() {
        return energyStorage.getMaxEnergyStored() > 0 ?
                (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored() : 0;
    }

    // NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(tag.get("energy"));
        }
    }

    // Capabilities - помпа сама имеет небольшой буфер энергии
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
    }
}