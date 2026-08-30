package net.enecske.primordial_park.client.gui;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.inventory.menu.CalibrationMatrixMenu;
import net.enecske.primordial_park.network.CalibrateMatrixPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class CalibrationMatrixScreen extends AbstractContainerScreen<CalibrationMatrixMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "textures/gui/calibration_matrix.png");

    private int leftPos;
    private int topPos;

    private Button calibrateButton;

    public CalibrationMatrixScreen(CalibrationMatrixMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 198;

        menu.onSlotsChanged = () -> {
            if (calibrateButton != null) calibrateButton.active = menu.canCalibrate();
        };
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;

        calibrateButton = addRenderableWidget(
                Button.builder(
                                Component.translatable("button.primordial_park.calibrate"),
                                button -> {
                                    PacketDistributor.sendToServer(CalibrateMatrixPayload.INSTANCE);
                                    menu.calibrate();
                                }
                        )
                        .bounds(leftPos + 48, topPos + 50, 80, 16)
                        .build()
        );
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
