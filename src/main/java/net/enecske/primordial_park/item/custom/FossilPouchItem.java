package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.inventory.FossilPouchMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FossilPouchItem extends Item {
    public FossilPouchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, player1) ->
                            new FossilPouchMenu(containerId, playerInventory, stack),
                    Component.translatable("item.primordial_park.fossil_pouch")),
                    buf -> ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack)
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        if (!slotChanged && oldStack.is(newStack.getItem())) return false;
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }
}
