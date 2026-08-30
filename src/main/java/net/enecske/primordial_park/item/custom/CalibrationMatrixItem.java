package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.inventory.menu.CalibrationMatrixMenu;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.component.CalibrationMatrixComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CalibrationMatrixItem extends Item {
    public CalibrationMatrixItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide())
            player.openMenu(new SimpleMenuProvider(
                            (containerId, playerInventory, player1) ->
                                    new CalibrationMatrixMenu(containerId, playerInventory, stack),
                            Component.translatable("item.primordial_park.calibration_matrix")),
                    buf -> ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack));

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CalibrationMatrixComponent component = stack.get(ModDataComponents.CALIBRATION_MATRIX.get());

        if (component != null)
            tooltipComponents.add(Component.translatable("tooltip.primordial_park.calibration_matrix_destination").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("time_period.primordial_park.%s".formatted(component.timePeriod().id)).withStyle(ChatFormatting.AQUA)));
    }
}