package net.enecske.primordial_park.inventory;

import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.ModItems;
import net.enecske.primordial_park.item.component.FossilPouchComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FossilPouchMenu extends AbstractContainerMenu {
    public static final int POUCH_SIZE = 15;

    private final ItemStackHandler inventory = new ItemStackHandler(POUCH_SIZE) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.get(ModDataComponents.FOSSIL.get()) != null && !stack.is(ModItems.FOSSIL_POUCH.get());
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (loaded) saveData();
        }
    };
    private final ItemStack stack;

    private boolean loaded = false;
    private boolean autoPickup;

    public FossilPouchMenu(int containerId, Inventory playerInventory, ItemStack stack) {
        super(ModMenuTypes.FOSSIL_POUCH.get(), containerId);
        this.stack = stack;

        loadData();

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 5; ++col) {
                int x = 44 + col * 18;
                int y = 17 + row * 18;
                addSlot(new SlotItemHandler(inventory, col + row * 5, x, y));
            }
        }

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

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < POUCH_SIZE) {
                if (!this.moveItemStackTo(stackInSlot, POUCH_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, POUCH_SIZE, false)) {
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

    public void loadData() {
        FossilPouchComponent component = stack.getOrDefault(ModDataComponents.FOSSIL_POUCH, FossilPouchComponent.EMPTY);
        ItemContainerContents contents = component.contents();

        NonNullList<ItemStack> list = NonNullList.withSize(POUCH_SIZE, ItemStack.EMPTY);
        contents.copyInto(list);

        for (int i = 0; i < POUCH_SIZE; i++) {
            inventory.setStackInSlot(i, list.get(i));
        }

        autoPickup = component.autoPickup();

        loaded = true;
    }

    private void saveData() {
        if (stack.isEmpty()) return;

        NonNullList<ItemStack> list = NonNullList.withSize(POUCH_SIZE, ItemStack.EMPTY);
        boolean hasItems = false;

        for (int i = 0; i < POUCH_SIZE; i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            list.set(i, itemStack);
            if (!stack.isEmpty()) hasItems = true;
        }

        stack.set(ModDataComponents.FOSSIL_POUCH, new FossilPouchComponent(hasItems ? ItemContainerContents.fromItems(list) : ItemContainerContents.EMPTY, autoPickup));
    }

    public boolean isAutoPickupEnabled() {
        return autoPickup;
    }

    public void toggleAutoPickup() {
        autoPickup = !autoPickup;
        saveData();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return !stack.isEmpty() && (player.getMainHandItem() == stack || player.getOffhandItem() == stack) && !player.isSpectator();
    }
}
