package net.enecske.primordial_park.inventory.menu;

import net.enecske.primordial_park.TimePeriod;
import net.enecske.primordial_park.inventory.ModMenuTypes;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.component.CalibrationMatrixComponent;
import net.enecske.primordial_park.item.component.ReportCardComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class CalibrationMatrixMenu extends AbstractContainerMenu {
    public OnSlotsChanged onSlotsChanged = null;

    private final ItemStack stack;
    private final ItemStackHandler inputs = new ItemStackHandler(3) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.get(ModDataComponents.REPORT_CARD.get()) != null;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (onSlotsChanged != null) onSlotsChanged.onSlotsChanged();
        }
    };

    public CalibrationMatrixMenu(int containerId, Inventory playerInventory, ItemStack stack) {
        super(ModMenuTypes.CALIBRATION_MATRIX.get(), containerId);
        this.stack = stack;

        addSlot(new SlotItemHandler(inputs, 0, 44, 23));
        addSlot(new SlotItemHandler(inputs, 1, 80, 23));
        addSlot(new SlotItemHandler(inputs, 2, 116, 23));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public boolean canCalibrate() {
        TimePeriod period = null;
        ArrayList<String> species = new ArrayList<>();

        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = inputs.getStackInSlot(slot);
            if (stack.isEmpty()) return false;

            ReportCardComponent component = stack.get(ModDataComponents.REPORT_CARD.get());
            if (component == null) return false;

            if (period == null) period = component.timePeriod();
            else if (component.timePeriod() != period) return false;

            if (species.contains(component.species())) return false;
            species.add(component.species());
        }

        return true;
    }

    public void calibrate() {
        if (!canCalibrate()) return;

        TimePeriod period = Objects.requireNonNull(inputs.getStackInSlot(0).get(ModDataComponents.REPORT_CARD.get())).timePeriod();
        stack.set(ModDataComponents.CALIBRATION_MATRIX.get(), new CalibrationMatrixComponent(period));

        for (int slot = 0; slot < 3; slot++) inputs.setStackInSlot(slot, ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 3) {
                if (!this.moveItemStackTo(stackInSlot, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return !stack.isEmpty() && (player.getMainHandItem() == stack || player.getOffhandItem() == stack) && !player.isSpectator();
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        for (int slot = 0; slot < 3; slot++) {
            ItemStack stack = inputs.getStackInSlot(slot);

            if (!stack.isEmpty()) {
                if (!player.getInventory().add(stack.copy())) {
                    player.drop(stack.copy(), false);
                }
                inputs.setStackInSlot(0, ItemStack.EMPTY);
            }
        }
    }

    public interface OnSlotsChanged {
        void onSlotsChanged();
    }
}
