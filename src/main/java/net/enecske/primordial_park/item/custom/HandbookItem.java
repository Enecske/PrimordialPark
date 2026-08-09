package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.client.helper.ClientScreenHelper;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.component.HandbookComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HandbookItem extends Item {
    public HandbookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HandbookComponent data = stack.get(ModDataComponents.HANDBOOK);
        SpeciesIndexAttachment attachment = player.getData(ModAttachments.SPECIES_INDEX);

        if (data == null) return InteractionResultHolder.fail(stack);

        if (level.isClientSide()) {
            ClientScreenHelper.openHandbookScreen(data, attachment);
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        HandbookComponent handbookComponent = stack.get(ModDataComponents.HANDBOOK.get());
        if (handbookComponent == null) return;

        HandbookComponent.UpgradeComponent upgradeComponent = handbookComponent.upgrade();
        String[] keys = upgradeComponent.getKeys();
        boolean[] values = upgradeComponent.getValues();

        int n = 0;
        for (boolean value : values) {
            if (value) n++;
        }

        tooltipComponents.add(Component.translatable("tooltip.primordial_park.handbook_upgrades")
                .append(Component.literal(": "))
                .append(Component.literal(Integer.toString(n)).withStyle(n == values.length ? ChatFormatting.GOLD : ChatFormatting.AQUA))
                .append(Component.literal("/%s".formatted(values.length)))
        );

        if (n == 0) return;

        for (int i = 0; i < values.length; i++) {
            if (values[i])
                tooltipComponents.add(Component.literal(" - ").withStyle(ChatFormatting.GRAY)
                        .append(Component.translatable("handbook_upgrade.primordial_park.%s".formatted(keys[i])))
                );
        }
    }
}
