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
import org.madmen.prosto.block.EnergyCableBlock;

import java.util.ArrayList;
import java.util.List;

public class EnergyCableBlockEntity extends BlockEntity {

    // Внутренний буфер энергии
    private final EnergyStorage energyStorage = new EnergyStorage(1000, 1000, 1000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                setChanged(); // Помечаем как изменённое
                // Можно запустить передачу сразу
                if (level != null && !level.isClientSide) {
                    transferTick = 0; // Ускоряем следующую передачу
                }
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) {
                setChanged();
            }
            return extracted;
        }
    };

    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    // Для оптимизации
    private int transferTick = 0;
    private List<IEnergyStorage> cachedNeighbors = null;
    private long lastNeighborUpdate = 0;

    public EnergyCableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_CABLE.get(), pos, state);
    }

    // Основной тик (серверная сторона)
    public static void tick(Level level, BlockPos pos, BlockState state, EnergyCableBlockEntity cable) {
        if (level.isClientSide) return;

        cable.transferTick++;

        // Передаём энергию каждые 2 тика (10 раз в секунду)
        if (cable.transferTick >= 2) {
            cable.transferEnergy();
            cable.transferTick = 0;
        }
    }

    private void transferEnergy() {
        if (energyStorage.getEnergyStored() == 0) return;

        List<IEnergyStorage> neighbors = getNeighbors();
        if (neighbors.isEmpty()) return;

        // Распределяем энергию поровну между всеми соседями
        int toDistribute = Math.min(energyStorage.getEnergyStored(), 1000); // 1000 FE/тик
        if (toDistribute == 0) return;

        int perNeighbor = toDistribute / neighbors.size();
        int remainder = toDistribute % neighbors.size();

        int totalTransferred = 0;

        for (IEnergyStorage neighbor : neighbors) {
            int toSend = perNeighbor + (remainder-- > 0 ? 1 : 0);
            if (toSend > 0) {
                // Проверяем, можем ли передать
                int canExtract = energyStorage.extractEnergy(toSend, true);
                if (canExtract > 0) {
                    // Проверяем, может ли сосед принять
                    int canReceive = neighbor.receiveEnergy(canExtract, true);
                    if (canReceive > 0) {
                        // Реальная передача
                        int extracted = energyStorage.extractEnergy(canReceive, false);
                        if (extracted > 0) {
                            neighbor.receiveEnergy(extracted, false);
                            totalTransferred += extracted;
                        }
                    }
                }
            }
        }

        if (totalTransferred > 0) {
            setChanged();
        }
    }

    // Получаем список соседей, которым можно передавать энергию
    private List<IEnergyStorage> getNeighbors() {
        if (level == null) return new ArrayList<>();

        // Кэшируем на 20 тиков (1 секунда)
        long currentTime = level.getGameTime();
        if (cachedNeighbors == null || currentTime - lastNeighborUpdate > 20) {
            cachedNeighbors = findNeighbors();
            lastNeighborUpdate = currentTime;
        }

        return cachedNeighbors;
    }

    private List<IEnergyStorage> findNeighbors() {
        List<IEnergyStorage> neighbors = new ArrayList<>();

        if (level == null || getBlockPos() == null) return neighbors;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = getBlockPos().relative(direction);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);

            if (neighbor != null) {
                // ВАЖНО: Подключаемся и к другим кабелям, и к помпам, и к другим блокам
                neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite())
                        .ifPresent(neighbors::add);
            }
        }

        return neighbors;
    }

    // Для рендерера: получаем подключения
    public boolean[] getConnections() {
        boolean[] connections = new boolean[6];

        if (level == null || getBlockState().getBlock() instanceof EnergyCableBlock) {
            BlockState state = getBlockState();
            connections[0] = state.getValue(EnergyCableBlock.NORTH);
            connections[1] = state.getValue(EnergyCableBlock.SOUTH);
            connections[2] = state.getValue(EnergyCableBlock.EAST);
            connections[3] = state.getValue(EnergyCableBlock.WEST);
            connections[4] = state.getValue(EnergyCableBlock.UP);
            connections[5] = state.getValue(EnergyCableBlock.DOWN);
        }

        return connections;
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

    // Capabilities
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

    // Геттеры
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
}