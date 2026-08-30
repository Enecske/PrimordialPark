package net.enecske.primordial_park.client.gui;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.inventory.menu.PaleontologyTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PaleontologyTableScreen extends AbstractContainerScreen<PaleontologyTableMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "textures/gui/paleontology_table.png");
    private static final ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "textures/gui/sprites/container/paleontology_table/work_progress.png");

    public PaleontologyTableScreen(PaleontologyTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.isCrafting()) {
            int progressWidth = this.menu.getScaledProgress();

            guiGraphics.blit(ARROW, x + 111, y + 35, 0, 0, progressWidth, 16, 24, 16);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
