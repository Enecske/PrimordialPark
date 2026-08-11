package net.enecske.primordial_park.inventory;

import net.enecske.primordial_park.block.ModBlocks;
import net.enecske.primordial_park.block.entity.PaleontologyTableBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PaleontologyTableMenu extends AbstractContainerMenu {
    private final PaleontologyTableBlockEntity blockEntity;
    private final ContainerData data;

    public PaleontologyTableMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public PaleontologyTableMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.PALEONTOLOGY_TABLE.get(), containerId);
        this.blockEntity = (PaleontologyTableBlockEntity) blockEntity;
        this.data = data;

        addDataSlots(data);

        IItemHandler handler = this.blockEntity.inventory;
        this.addSlot(new SlotItemHandler(handler, 0, 54, 35));
        this.addSlot(new SlotItemHandler(handler, 1, 87, 35));
        this.addSlot(new SlotItemHandler(handler, 2, 143, 35));

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

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int progressArrowSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 3) { // From Block Entity to Player Inventory
                if (!this.moveItemStackTo(stackInSlot, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else { // From Player Inventory to Block Entity
                if (!this.moveItemStackTo(stackInSlot, 0, 2, false)) {
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
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player,
                ModBlocks.PALEONTOLOGY_TABLE.get()
        );
    }
}
