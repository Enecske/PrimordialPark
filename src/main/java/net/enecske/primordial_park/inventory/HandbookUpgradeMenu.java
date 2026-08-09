package net.enecske.primordial_park.inventory;

import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.ModItems;
import net.enecske.primordial_park.item.component.HandbookComponent;
import net.enecske.primordial_park.item.component.HandbookUpgradeComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HandbookUpgradeMenu extends AbstractContainerMenu {
    private final Container resultSlot = new ResultContainer();

    private final Container inputSlots = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            HandbookUpgradeMenu.this.slotsChanged(this);
        }
    };

    public HandbookUpgradeMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.HANDBOOK_UPGRADE.get(), containerId);

        this.addSlot(new Slot(inputSlots, 0, 68, 25) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.get(ModDataComponents.HANDBOOK.get()) != null;
            }
        });

        this.addSlot(new Slot(inputSlots, 1, 68, 46) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.get(ModDataComponents.HANDBOOK_UPGRADE.get()) != null;
            }
        });

        this.addSlot(new Slot(resultSlot, 2, 128, 35) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                inputSlots.setItem(0, ItemStack.EMPTY);
                inputSlots.setItem(1, ItemStack.EMPTY);
            }
        });

        addPlayerInventory(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, col * 18 + 8, 142));
        }
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        super.slotsChanged(container);
        if (container == inputSlots)
            resultSlot.setItem(0, createResult());
    }

    private ItemStack createResult() {
        if (inputSlots.getItem(0).isEmpty() || inputSlots.getItem(1).isEmpty())
            return ItemStack.EMPTY;

        HandbookComponent handbookComponent = inputSlots.getItem(0).get(ModDataComponents.HANDBOOK.get());
        assert handbookComponent != null;

        HandbookUpgradeComponent upgradeComponent = inputSlots.getItem(1).get(ModDataComponents.HANDBOOK_UPGRADE.get());
        assert upgradeComponent != null;

        HandbookComponent.UpgradeComponent handbookUpgradeComponent = handbookComponent.upgrade();

        boolean[] values = {
                handbookUpgradeComponent.zooBasic(),
                handbookUpgradeComponent.zooAdvanced(),
                handbookUpgradeComponent.biotechBasic(),
                handbookUpgradeComponent.biotechAdvanced(),
                handbookUpgradeComponent.biosynthesis()
        };

        String[] keys = {
                "zoo_basic",
                "zoo_advanced",
                "biotech_basic",
                "biotech_advanced",
                "biosynthesis"
        };

        int k = -1;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(upgradeComponent.id())) {
                k = i;
                break;
            }
        }
        if (k == -1 || values[k]) return ItemStack.EMPTY;

        values[k] = true;

        HandbookComponent newComponent = new HandbookComponent(new HandbookComponent.UpgradeComponent(
                values[0],
                values[1],
                values[2],
                values[3],
                values[4]
        ));

        ItemStack newItemStack = new ItemStack(ModItems.HANDBOOK.get());
        newItemStack.set(ModDataComponents.HANDBOOK.get(), newComponent);

        return newItemStack;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(0);
            ItemStack itemstack3 = this.inputSlots.getItem(1);
            if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (!itemstack2.isEmpty() && !itemstack3.isEmpty()) {
                    if (index >= 3 && index < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 30 && index < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
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
    public void removed(@NotNull Player player) {
        super.removed(player);

        ItemStack stack1 = inputSlots.getItem(0);
        ItemStack stack2 = inputSlots.getItem(1);

        if (!stack1.isEmpty()) {
            if (!player.getInventory().add(stack1.copy())) {
                player.drop(stack1.copy(), false);
            }
            inputSlots.setItem(0, ItemStack.EMPTY);
        }
        if (!stack2.isEmpty()) {
            if (!player.getInventory().add(stack2.copy())) {
                player.drop(stack2.copy(), false);
            }
            inputSlots.setItem(1, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }


}
