package net.enecske.primordial_park.client.gui;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.inventory.menu.FossilPouchMenu;
import net.enecske.primordial_park.network.ToggleAutoPickupPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class FossilPouchScreen extends AbstractContainerScreen<FossilPouchMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "textures/gui/fossil_pouch.png");

    private int leftPos;
    private int topPos;

    public FossilPouchScreen(FossilPouchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 198;
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;

        addRenderableWidget(new AbstractWidget(leftPos + 149, topPos + 7, 20, 20, Component.translatable("button.primordial_park.auto_pickup")) {
            @Override
            protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                int u = !isHovered() ? 175 : 195;
                int v = !menu.isAutoPickupEnabled() ? 0 : 20;

                guiGraphics.blit(
                        TEXTURE,
                        getX(), getY(),
                        u, v,
                        20, 20,
                        256, 256
                );
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                PacketDistributor.sendToServer(ToggleAutoPickupPayload.INSTANCE);
                menu.toggleAutoPickup();

                playDownSound(Minecraft.getInstance().getSoundManager());

                super.onClick(mouseX, mouseY, button);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
                defaultButtonNarrationText(narrationElementOutput);
            }
        }).setTooltip(Tooltip.create(Component.translatable("button.primordial_park.auto_pickup")));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
