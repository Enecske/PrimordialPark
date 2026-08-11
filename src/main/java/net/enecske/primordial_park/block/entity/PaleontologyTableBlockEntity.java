package net.enecske.primordial_park.block.entity;

import net.enecske.primordial_park.block.ModBlockEntities;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.ModItems;
import net.enecske.primordial_park.item.component.FossilComponent;
import net.enecske.primordial_park.item.component.ReportCardComponent;
import net.enecske.primordial_park.villager.ModVillagerProfessions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PaleontologyTableBlockEntity extends BlockEntity {
    public static final int SLOT_FOSSIL = 0;
    public static final int SLOT_REPORT_CARD = 1;
    public static final int SLOT_RESULT = 2;

    public static final int WORKING_TIME = 200;

    boolean villagerNearby = false;
    boolean isReady = false;
    int workTime = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case SLOT_FOSSIL -> stack.get(ModDataComponents.FOSSIL.get()) != null;
                case SLOT_REPORT_CARD -> stack.is(ModItems.REPORT_CARD.get());
                case SLOT_RESULT -> false;

                default -> super.isItemValid(slot, stack);
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide) {
                level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            }
        }
    };

    private final IItemHandler topHandler = new RangedWrapper(inventory, SLOT_FOSSIL, SLOT_FOSSIL + 1);
    private final IItemHandler sideHandler = new RangedWrapper(inventory, SLOT_REPORT_CARD, SLOT_REPORT_CARD + 1);

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> workTime;
                case 1 -> WORKING_TIME;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) workTime = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public PaleontologyTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PALEONTOLOGY_TABLE.get(), pos, blockState);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        return switch (side) {
            case UP -> topHandler;
            case NORTH, SOUTH, EAST, WEST -> sideHandler;
            case DOWN -> new ConditionalBottomHandler(inventory, level, getBlockPos());
            case null -> inventory;
        };
    }

    public static void serverTick(@SuppressWarnings("unused") Level level, @SuppressWarnings("unused") BlockPos pos, @SuppressWarnings("unused") BlockState state, PaleontologyTableBlockEntity blockEntity) {
        if (level.getGameTime() % 40 == 0) {
            boolean villagerWorking = checkNearbyVillager(level, pos);

            if (villagerWorking != blockEntity.villagerNearby) {
                blockEntity.villagerNearby = villagerWorking;
                blockEntity.setChanged();
            }
        }

        boolean canWork = canWork(blockEntity);
        if (canWork != blockEntity.isReady) {
            blockEntity.isReady = canWork;
            blockEntity.setChanged();
        }

        if (blockEntity.villagerNearby && blockEntity.isReady) {
            blockEntity.workTime++;

            if (blockEntity.workTime >= WORKING_TIME) {
                blockEntity.inventory.getStackInSlot(SLOT_REPORT_CARD).shrink(1);
                blockEntity.workTime = 0;

                if (blockEntity.inventory.getStackInSlot(SLOT_RESULT).isEmpty()) {
                    ItemStack newStack = new ItemStack(ModItems.REPORT_CARD.get());
                    newStack.set(ModDataComponents.REPORT_CARD.get(), ReportCardComponent.copyFrom(blockEntity.inventory.getStackInSlot(SLOT_FOSSIL).get(ModDataComponents.FOSSIL.get())));
                    blockEntity.inventory.setStackInSlot(SLOT_RESULT, newStack);
                } else blockEntity.inventory.getStackInSlot(SLOT_RESULT).grow(1);

                blockEntity.setChanged();
            }
        } else if (blockEntity.workTime != 0) {
            blockEntity.setChanged();
            blockEntity.workTime = 0;
        }
    }

    private static boolean checkNearbyVillager(Level level, BlockPos pos) {
        AABB searchBox = new AABB(pos).inflate(2);

        List<Villager> nearbyVillagers = level.getEntitiesOfClass(Villager.class, searchBox);

        for (Villager villager : nearbyVillagers) {
            boolean isPaleontologist = villager.getVillagerData().getProfession() == ModVillagerProfessions.PALEONTOLOGIST.value();
            boolean isActive = !villager.isBaby() && !villager.isSleeping() && villager.isAlive();

            if (isPaleontologist && isActive) {
                Optional<GlobalPos> jobSiteMemory = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);

                if (jobSiteMemory.isPresent()) {
                    GlobalPos globalPos = jobSiteMemory.get();

                    boolean isSameDimension = globalPos.dimension() == level.dimension();
                    boolean isSamePosition = globalPos.pos().equals(pos);

                    if (isSameDimension && isSamePosition) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean canWork(PaleontologyTableBlockEntity blockEntity) {
        ItemStack fossilStack = blockEntity.inventory.getStackInSlot(SLOT_FOSSIL);
        ItemStack reportCardStack = blockEntity.inventory.getStackInSlot(SLOT_REPORT_CARD);
        ItemStack resultStack = blockEntity.inventory.getStackInSlot(SLOT_RESULT);

        if (fossilStack.isEmpty()) return false;
        if (reportCardStack.isEmpty()) return false;
        if (resultStack.getCount() >= 64) return false;

        if (reportCardStack.getItem() != ModItems.REPORT_CARD.get()) return false;

        FossilComponent fossilComponent = fossilStack.get(ModDataComponents.FOSSIL);
        ReportCardComponent resultComponent = resultStack.get(ModDataComponents.REPORT_CARD);

        if (fossilComponent == null) return false;

        return resultComponent == null || fossilComponent.matches(resultComponent);
    }

    public boolean isReady() {
        return isReady;
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            container.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(Objects.requireNonNull(level), worldPosition, container);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putBoolean("VillagerNearby", villagerNearby);
        tag.putInt("WorkTime", workTime);
        tag.putBoolean("isReady", isReady);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        villagerNearby = tag.getBoolean("VillagerNearby");
        workTime = tag.getInt("WorkTime");
        isReady = tag.getBoolean("isReady");
    }

    private record ConditionalBottomHandler(ItemStackHandler internal, Level level,
                                            BlockPos pos) implements IItemHandler {

        private boolean isPowered() {
            return level != null && level.hasNeighborSignal(pos);
        }

        @Override
        public int getSlots() {
            return 3;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return internal.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == SLOT_RESULT)
                return internal.extractItem(SLOT_RESULT, amount, simulate);

            if (slot == SLOT_FOSSIL && isPowered()) {
                if (!internal.getStackInSlot(SLOT_RESULT).isEmpty()) return ItemStack.EMPTY;
                return internal.extractItem(SLOT_FOSSIL, amount, simulate);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return internal.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    }
}
