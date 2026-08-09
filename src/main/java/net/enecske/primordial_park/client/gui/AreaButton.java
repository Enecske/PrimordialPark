package net.enecske.primordial_park.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.enecske.primordial_park.PrimordialPark;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class AreaButton extends AbstractWidget {
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "textures/gui/handbook.png");

    private final OnPress onPress;

    private int pressed = -1;

    public AreaButton(int x, int y, OnPress onPress) {
        super(x, y, 134, 38, Component.empty());

        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            guiGraphics.blit(
                    TEXTURE,
                    this.getX(), this.getY(),
                    320, pressed >= 0 ? 38 : 0,
                    this.width, this.height,
                    512, 512
            );

            RenderSystem.disableBlend();
        }
        if (pressed >= 0) pressed--;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        pressed = 4;

        playDownSound(Minecraft.getInstance().getSoundManager());

        this.onPress.onPress();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress();
    }
}
