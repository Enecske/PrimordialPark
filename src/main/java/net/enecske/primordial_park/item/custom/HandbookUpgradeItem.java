package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.component.HandbookUpgradeComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HandbookUpgradeItem extends Item {
    public HandbookUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        HandbookUpgradeComponent component = stack.get(ModDataComponents.HANDBOOK_UPGRADE.get());

        if (component != null)
            tooltipComponents.add(Component.translatable("handbook_upgrade.primordial_park.%s".formatted(component.id())).withStyle(ChatFormatting.GRAY));
        else
            tooltipComponents.add(Component.literal("lorem ipsum").withStyle(ChatFormatting.GRAY, ChatFormatting.OBFUSCATED));
    }
}
