package net.enecske.primordial_park.client.gui;

import net.enecske.primordial_park.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FossilToast implements Toast {
    private final String id;
    private final ItemStack stack;

    public static final StreamCodec<RegistryFriendlyByteBuf, FossilToast> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, toast -> toast.id,
            ItemStack.STREAM_CODEC, toast -> toast.stack,
            FossilToast::new
    );

    public FossilToast(String id, ItemStack stack) {
        this.id = id;

        if (stack == null) this.stack = new ItemStack(ModItems.UNKNOWN_FOSSIL.get());
        else this.stack = stack;
    }

    @Override
    public @NotNull Visibility render(@NotNull GuiGraphics guiGraphics, @NotNull ToastComponent toastComponent, long timeSinceLastVisible) {
        guiGraphics.blitSprite(ResourceLocation.withDefaultNamespace("toast/advancement"), 0, 0, this.width(), this.height());

        guiGraphics.drawString(toastComponent.getMinecraft().font,
                Component.translatable("toast.primordial_park.fossil_discovered"),
                30,
                7,
                0xFFFF00,
                false);

        guiGraphics.drawString(toastComponent.getMinecraft().font,
                Component.translatable("item.primordial_park.%s".formatted(id)),
                30,
                18,
                0xFFFFFF,
                false);

        guiGraphics.renderFakeItem(stack, 8, 8);

        return timeSinceLastVisible >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }

    public void show() {
        Minecraft.getInstance().getToasts().addToast(this);
    }

    public static void show(String id, ItemStack stack) {
        Minecraft.getInstance().getToasts().addToast(new FossilToast(id, stack));
    }
}
