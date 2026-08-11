package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.component.ReportCardComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReportCardItem extends Item {
    public ReportCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        ReportCardComponent component = stack.get(ModDataComponents.REPORT_CARD.get());

        if (component != null) {
            tooltipComponents.add(Component.translatable("tooltip.primordial_park.report_card_time_period").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("time_period.primordial_park.%s".formatted(component.timePeriod().id)).withStyle(ChatFormatting.AQUA)));

            tooltipComponents.add(Component.translatable("tooltip.primordial_park.report_card_species").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("entity.primordial_park.%s".formatted(component.species())).withStyle(ChatFormatting.AQUA)));
        }
        else tooltipComponents.add(Component.translatable("tooltip.primordial_park.report_card_empty").withStyle(ChatFormatting.GRAY));
    }
}
