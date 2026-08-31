package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.client.gui.FossilToast;
import net.enecske.primordial_park.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DebugItem extends Item {
    public DebugItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        try {
            if (!level.isClientSide) return InteractionResultHolder.sidedSuccess(stack, false);

            FossilToast.show("mammoth_cranium", new ItemStack(ModItems.MAMMOTH_CRANIUM.get()));
        } catch (Exception e) {
            PrimordialPark.LOGGER.error("error", e);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
