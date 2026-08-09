package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.item.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnknownFossilItem extends Item {
    public UnknownFossilItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        Component storedText = stack.get(ModDataComponents.UNKNOWN_FOSSIL_HINT.get());

        if (storedText != null)
            tooltipComponents.add(storedText);
    }
}
