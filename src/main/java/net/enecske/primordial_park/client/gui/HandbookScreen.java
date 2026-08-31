package net.enecske.primordial_park.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.TimePeriod;
import net.enecske.primordial_park.client.handbook.HandbookContent;
import net.enecske.primordial_park.client.handbook.HandbookContentLoader;
import net.enecske.primordial_park.client.species_index.EntryContent;
import net.enecske.primordial_park.client.species_index.EntryContentLoader;
import net.enecske.primordial_park.client.species_index.SpeciesIndexEntry;
import net.enecske.primordial_park.client.species_index.SpeciesIndexRegistry;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.ModItems;
import net.enecske.primordial_park.item.component.HandbookComponent;
import net.enecske.primordial_park.network.OpenHandbookUpgradeScreenPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

// TODO artwork

@OnlyIn(Dist.CLIENT)
public class HandbookScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "textures/gui/handbook.png");
    private static final int backgroundWidth = 320;
    private static final int backgroundHeight = 200;

    private static final int margin = 13;
    @SuppressWarnings("FieldCanBeLocal")
    private final int lineWidth = 160 - (margin * 2);
    private static final int lineHeight = 12;
    private static final int entrySize = 40;
    private static final float itemScale = 4f;

    private static int leftPos;
    private static int topPos;

    private static int entityMinX;
    private static int entityMinY;
    private static int entityMaxX;
    private static int entityMaxY;

    private static int boxLeft;
    private static int boxTop;
    private static int boxRight;
    private static int boxBottom;
    private static int contentHeight = 800;

    private Chapter currentChapter = Chapter.HOME;
    private int currentPage = 0;

    private final HandbookComponent data;
    private boolean zookeepingUnlocked = false;
    private boolean biotechUnlocked = false;
    private ArrayList<String> conditions;

    private final SpeciesIndexAttachment attachment;

    private HandbookContent handbookContent;
    private EntryContent entryContent;

    private SpeciesIndexPage[] indexPages;
    private ArrayList<ArrayList<AbstractWidget>> entryButtons;
    private Map<String, LivingEntity> entities;
    private SpeciesIndexEntry selectedEntry = null;
    private Map<String, ItemStack> fossilItemStacks;
    private int fossilsFound;
    private float scrolledScaleMultiplier = 1;
    private float scrollAmount;

    private PageButton forwardButton;
    private PageButton backButton;

    private final AreaButton[] navButtons = new AreaButton[4];

    private AbstractWidget upgradeButton;

    public HandbookScreen(HandbookComponent data, SpeciesIndexAttachment attachment) {
        super(GameNarrator.NO_TITLE);
        this.data = data;
        this.attachment = attachment;
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (this.width - backgroundWidth) / 2;
        topPos = (this.height - backgroundHeight) / 2;

        boxLeft = leftPos + (backgroundWidth / 2) + margin;
        boxTop = topPos + margin;
        boxRight = leftPos + backgroundWidth - margin;
        boxBottom = topPos + backgroundHeight - margin;

        handbookContent = HandbookContentLoader.getContent();

        indexPages = SpeciesIndexPage.parse(SpeciesIndexRegistry.getEntriesByTimePeriod());

        entities = new HashMap<>();
        SpeciesIndexRegistry.getEntries().forEach((key, value) -> entities.put(key, value.entity()));

        HandbookComponent.UpgradeComponent upgradeComponent = data.upgrade();

        zookeepingUnlocked = upgradeComponent.zooBasic() || upgradeComponent.zooAdvanced();
        biotechUnlocked = upgradeComponent.biotechBasic() || upgradeComponent.biotechAdvanced() || upgradeComponent.biosynthesis();

        conditions = new ArrayList<>();
        for (int i = 0; i < upgradeComponent.getValues().length; i++) {
            if (upgradeComponent.getValues()[i]) conditions.add(upgradeComponent.getKeys()[i]);
        }

        navButtons[0] = addRenderableWidget(new AreaButton(
                leftPos + 171, topPos + 19,
                () -> {
                    currentChapter = Chapter.SPECIES_INDEX;
                    currentPage = 0;
                }
        ));
        navButtons[1] = addRenderableWidget(new AreaButton(
                leftPos + 171, topPos + 60,
                () -> {
                    currentChapter = Chapter.TIME_TRAVEL;
                    currentPage = 0;
                }
        ));
        navButtons[2] = addRenderableWidget(new AreaButton(
                leftPos + 171, topPos + 101,
                () -> {
                    if (!zookeepingUnlocked) {
                        Minecraft.getInstance().getSoundManager().play(
                                SimpleSoundInstance.forUI(SoundEvents.ITEM_BREAK, 1F)
                        );
                        return;
                    }

                    currentChapter = Chapter.ZOOKEEPING;
                    currentPage = 0;
                }
        ));
        navButtons[3] = addRenderableWidget(new AreaButton(
                leftPos + 171, topPos + 142,
                () -> {
                    if (!biotechUnlocked) {
                        Minecraft.getInstance().getSoundManager().play(
                                SimpleSoundInstance.forUI(SoundEvents.ITEM_BREAK, 1F)
                        );
                        return;
                    }

                    currentChapter = Chapter.BIOTECH;
                    currentPage = 0;
                }
        ));

        upgradeButton = addRenderableWidget(new AbstractWidget(leftPos + 306, topPos + 10, 32, 32, Component.empty()) {
            @Override
            protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                int u = isHovered() ? 320 + 32 : 320;

                guiGraphics.blit(
                        TEXTURE,
                        getX(), getY(),
                        u, 76,
                        width, height,
                        512, 512
                );
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                playDownSound(Minecraft.getInstance().getSoundManager());

                PacketDistributor.sendToServer(OpenHandbookUpgradeScreenPayload.INSTANCE);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
                defaultButtonNarrationText(narrationElementOutput);
            }
        });

        setHomeButtonsVisibility(false);

        backButton = addRenderableWidget(new PageButton(
                leftPos + 15, topPos + 176,
                false,
                button -> currentPage--,
                true
        ));
        forwardButton = addRenderableWidget(new PageButton(
                leftPos + 282, topPos + 176,
                true,
                button -> currentPage++,
                true
        ));
        hidePageButtons();

        entryButtons = new ArrayList<>();
        for (int i = 0; i < indexPages.length; i++) {
            SpeciesIndexPage page = indexPages[i];
            entryButtons.add(new ArrayList<>());

            int y = topPos + margin + 5;
            int startX = leftPos + (i % 2 == 0 ? 0 : backgroundWidth / 2) + ((backgroundWidth / 2) - entrySize * 3) / 2;

            for (SpeciesIndexElement[] elements : page.elements()) {
                if (elements[0].TYPE == SpeciesIndexElement.HEADING) {
                    y += lineHeight;
                    continue;
                }

                for (int j = 0; j < elements.length; j++) {
                    SpeciesIndexElement element = elements[j];

                    entryButtons.get(i).add(addRenderableWidget(new AbstractWidget(startX + 4 + j * entrySize, y, 36, 36, Component.empty()) {
                        @Override
                        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                            int u = isHovered() ? 320 + 36 : 320;

                            guiGraphics.blit(
                                    TEXTURE,
                                    getX(), getY(),
                                    u, 108,
                                    width, height,
                                    512, 512
                            );
                        }

                        @Override
                        public void onClick(double mouseX, double mouseY, int button) {
                            selectedEntry = element.entry;
                            currentChapter = Chapter.SPECIES_ENTRY;
                            currentPage = 0;
                            scrolledScaleMultiplier = 1;
                            scrollAmount = 0;

                            assert selectedEntry != null;

                            entryContent = EntryContentLoader.getContent(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, selectedEntry.id()));
                            assert entryContent != null;
                            contentHeight = 0;
                            for (Component paragraph : entryContent.paragraphs) {
                                contentHeight += font.wordWrapHeight(paragraph, boxRight - boxLeft) + 4;
                            }

                            SpeciesIndexAttachment.SpeciesEntryAttachment entryAttachment = attachment.entries().stream()
                                    .filter(entry -> Objects.equals(entry.id(), selectedEntry.id()))
                                    .findFirst()
                                    .orElse(new SpeciesIndexAttachment.SpeciesEntryAttachment(selectedEntry.id(), List.of(), false));

                            fossilItemStacks = new LinkedHashMap<>();
                            fossilsFound = 0;
                            for (Map.Entry<String, ItemStack> entry : selectedEntry.fossils().entrySet()) {
                                if (entryAttachment.fossils().contains(entry.getKey())) {
                                    fossilItemStacks.put(entry.getKey(), entry.getValue());
                                    fossilsFound++;
                                } else {
                                    ItemStack unknown = new ItemStack(ModItems.UNKNOWN_FOSSIL.get());
                                    unknown.set(ModDataComponents.UNKNOWN_FOSSIL_HINT, selectedEntry.hints().get(entry.getKey()));
                                    fossilItemStacks.put(entry.getKey(), unknown);
                                }
                            }
                        }

                        @Override
                        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
                            defaultButtonNarrationText(narrationElementOutput);
                        }
                    }));
                }

                y += entrySize;
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(
                TEXTURE,
                leftPos, topPos,
                0, 0,
                backgroundWidth, backgroundHeight,
                512, 512
        );

        renderPageContent(guiGraphics, mouseX, mouseY);

        /*guiGraphics.drawString(this.font, Component.literal(Integer.toString(mouseX)), leftPos + 10, topPos + 10, 0x000000, false);
        guiGraphics.drawString(this.font, Component.literal(Integer.toString(mouseY)), leftPos + 10, topPos + 20, 0x000000, false);*/

        for (Renderable renderable : renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderPageContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (currentPage == -1) {
            currentPage = 0;
            currentChapter = Chapter.HOME;
        }

        switch (currentChapter) {
            case HOME -> {
                guiGraphics.blit(
                        TEXTURE,
                        leftPos, topPos,
                        0, backgroundHeight,
                        backgroundWidth, backgroundHeight,
                        512, 512
                );

                guiGraphics.drawString(this.font, Component.translatable("handbook.primordial_park.species_index"), leftPos + 211, topPos + 32, 0x000000, false);
                guiGraphics.drawString(this.font, Component.translatable("handbook.primordial_park.time_travel"), leftPos + 211, topPos + 73, 0x000000, false);
                guiGraphics.drawString(this.font, obfuscateIf(Component.translatable("handbook.primordial_park.zookeping"), zookeepingUnlocked), leftPos + 211, topPos + 114, 0x000000, false);
                guiGraphics.drawString(this.font, obfuscateIf(Component.translatable("handbook.primordial_park.biotechnology"), biotechUnlocked), leftPos + 211, topPos + 155, 0x000000, false);

                setHomeButtonsVisibility(true);
                hidePageButtons();
                setEntryButtonVisibility(integer -> false);
            }
            case TIME_TRAVEL, ZOOKEEPING, BIOTECH -> {
                try {
                    HandbookContent.HandbookChapter chapter = handbookContent.getChapters().get(currentChapter.id);

                    HandbookContent.HandbookPage[] filteredPages = chapter.getPagesWithCondition(conditions.toArray(new String[0]));

                    int maxPage = (int) Math.floor((filteredPages.length - 1f) / 2f);
                    if (currentPage > maxPage) currentPage = 0;

                    backButton.visible = true;
                    forwardButton.visible = currentPage != maxPage;

                    setHomeButtonsVisibility(false);
                    setEntryButtonVisibility(integer -> false);

                    int paragraphStart = topPos + margin;

                    HandbookContent.HandbookPage leftPage = filteredPages[currentPage * 2];
                    for (HandbookContent.HandbookParagraph paragraph : leftPage.paragraphs) {
                        if (paragraph instanceof HandbookContent.HandbookTextParagraph textParagraph) {
                            guiGraphics.drawWordWrap(
                                    this.font,
                                    textParagraph.text,
                                    leftPos + margin,
                                    paragraphStart,
                                    lineWidth,
                                    0x000000
                            );
                            paragraphStart += this.font.wordWrapHeight(textParagraph.text, lineWidth) + 5;
                        } else if (paragraph instanceof HandbookContent.HandbookImageParagraph imageParagraph) {
                            drawImageWithAlpha(
                                    guiGraphics,
                                    imageParagraph.imageLocation,
                                    leftPos + (backgroundWidth / 2 - imageParagraph.width) / 2, paragraphStart,
                                    imageParagraph.width, imageParagraph.height,
                                    imageParagraph.width, imageParagraph.height
                            );
                            paragraphStart += imageParagraph.height + 5;
                        } else if (paragraph instanceof HandbookContent.HandbookItemParagraph itemParagraph) {
                            renderScaledItem(
                                    guiGraphics,
                                    itemParagraph.stack,
                                    leftPos + (backgroundWidth / 4) - (int) (itemScale * 16 / 2), paragraphStart
                            );
                            paragraphStart += (int) (itemScale * 16) + 5;
                        }
                    }

                    if (currentPage == maxPage && filteredPages.length % 2 == 1) break;

                    paragraphStart = topPos + margin;

                    HandbookContent.HandbookPage rightPage = filteredPages[currentPage * 2 + 1];
                    for (HandbookContent.HandbookParagraph paragraph : rightPage.paragraphs) {
                        if (paragraph instanceof HandbookContent.HandbookTextParagraph textParagraph) {
                            guiGraphics.drawWordWrap(
                                    this.font,
                                    textParagraph.text,
                                    leftPos + (backgroundWidth / 2) + margin,
                                    paragraphStart,
                                    lineWidth,
                                    0x000000
                            );
                            paragraphStart += this.font.wordWrapHeight(textParagraph.text, lineWidth) + 5;
                        } else if (paragraph instanceof HandbookContent.HandbookImageParagraph imageParagraph) {
                            drawImageWithAlpha(
                                    guiGraphics,
                                    imageParagraph.imageLocation,
                                    leftPos + (backgroundWidth / 2) + (backgroundWidth / 2 - imageParagraph.width) / 2, paragraphStart,
                                    imageParagraph.width, imageParagraph.height,
                                    imageParagraph.width, imageParagraph.height
                            );
                            paragraphStart += imageParagraph.height + 5;
                        }  else if (paragraph instanceof HandbookContent.HandbookItemParagraph itemParagraph) {
                            renderScaledItem(
                                    guiGraphics,
                                    itemParagraph.stack,
                                    leftPos + (backgroundWidth / 4 * 3) - (int) (itemScale * 16 / 2), paragraphStart
                            );
                            paragraphStart += (int) (itemScale * 16) + 5;
                        }
                    }
                } catch (Exception e) {
                    PrimordialPark.LOGGER.error("Error rendering chapter {} of Handbook", currentChapter.id, e);
                    this.onClose();
                }
            }
            case SPECIES_INDEX -> {
                int maxPage = (int) Math.floor((indexPages.length - 1f) / 2f);
                if (currentPage > maxPage) currentPage = 0;

                backButton.visible = true;
                forwardButton.visible = currentPage != maxPage;

                setEntryButtonVisibility(integer -> integer == currentPage * 2 || integer == currentPage * 2 + 1);
                setHomeButtonsVisibility(false);

                int y = topPos + margin + 5;
                int startX = leftPos + ((backgroundWidth / 2) - entrySize * 3) / 2;

                SpeciesIndexPage leftPage = indexPages[currentPage * 2];
                for (SpeciesIndexElement[] elements : leftPage.elements) {
                    if (elements[0].TYPE == SpeciesIndexElement.ENTRY) {
                        for (int i = 0; i < elements.length; i++) {
                            SpeciesIndexEntry entry = elements[i].entry;
                            assert entry != null;
                            LivingEntity entity = entities.get(entry.id());

                            InventoryScreen.renderEntityInInventory(
                                    guiGraphics,
                                    startX + 6 + i * entrySize + entry.entityDimensions().xOffset(),
                                    y + entry.entityDimensions().yOffset(),
                                    entry.entityDimensions().scale(),
                                    entry.entityDimensions().rotationOffset(),
                                    entry.entityDimensions().rotation(),
                                    null,
                                    entity
                            );
                        }

                        y += entrySize;
                        continue;
                    }

                    int textWidth = this.font.width(Component.translatable("time_period.primordial_park.%s".formatted(elements[0].heading)));
                    guiGraphics.drawString(
                            this.font,
                            Component.translatable("time_period.primordial_park.%s".formatted(elements[0].heading)).withStyle(ChatFormatting.UNDERLINE),
                            leftPos + backgroundWidth / 4 - textWidth / 2,
                            y,
                            0x000000,
                            false
                    );
                    y += lineHeight;
                }

                if (currentPage == maxPage && indexPages.length % 2 == 1) break;

                y = topPos + margin + 5;
                startX = leftPos + backgroundWidth / 2 + ((backgroundWidth / 2) - entrySize * 3) / 2;

                SpeciesIndexPage rightPage = indexPages[currentPage * 2 + 1];
                for (SpeciesIndexElement[] elements : rightPage.elements) {
                    if (elements[0].TYPE == SpeciesIndexElement.ENTRY) {
                        for (int i = 0; i < elements.length; i++) {
                            SpeciesIndexEntry entry = elements[i].entry;
                            assert entry != null;
                            LivingEntity entity = entities.get(entry.id());

                            InventoryScreen.renderEntityInInventory(
                                    guiGraphics,
                                    startX + 6 + i * entrySize + entry.entityDimensions().xOffset(),
                                    y + entry.entityDimensions().yOffset(),
                                    entry.entityDimensions().scale(),
                                    entry.entityDimensions().rotationOffset(),
                                    entry.entityDimensions().rotation(),
                                    null,
                                    entity
                            );
                        }

                        y += entrySize;
                        continue;
                    }

                    int textWidth = this.font.width(Component.translatable("time_period.primordial_park.%s".formatted(elements[0].heading)));
                    guiGraphics.drawString(
                            this.font,
                            Component.translatable("time_period.primordial_park.%s".formatted(elements[0].heading)).withStyle(ChatFormatting.UNDERLINE),
                            leftPos + backgroundWidth / 4 * 3 - textWidth / 2,
                            y,
                            0x000000,
                            false
                    );
                    y += lineHeight;
                }
            }
            case SPECIES_ENTRY -> {
                Component title = Component.translatable("entity.primordial_park.%s".formatted(selectedEntry.id())).withStyle(ChatFormatting.UNDERLINE);
                int titleWidth = font.width(title);
                guiGraphics.drawString(this.font, title, leftPos + (backgroundWidth / 4) - (titleWidth / 2), topPos + margin, 0x000000, false);

                entityMinX = leftPos + 16;
                entityMinY = topPos + margin + lineHeight;
                entityMaxX = leftPos + 16 + 128;
                entityMaxY = topPos + margin + lineHeight + 96;

                LivingEntity entity = entities.get(selectedEntry.id());
                SpeciesIndexEntry.Dimensions dimensions = selectedEntry.entityDimensions();
                int scaleMultiplier = 3;

                InventoryScreen.renderEntityInInventoryFollowsMouse(
                        guiGraphics,
                        entityMinX,
                        entityMinY,
                        entityMaxX,
                        entityMaxY,
                        (int) (dimensions.scale() * scaleMultiplier * scrolledScaleMultiplier),
                        dimensions.followMouseYOffset(),
                        mouseX,
                        mouseY,
                        entity
                );

                int y = entityMaxY + 16 - 18;

                int i = 0;
                for (Map.Entry<String, ItemStack> entry : fossilItemStacks.entrySet()) {
                    if (i % 7 == 0) {
                        y += 18;
                        guiGraphics.blit(
                                TEXTURE,
                                leftPos + 17, y,
                                320, 144,
                                126, 18,
                                512, 512
                        );
                    }

                    ItemStack stack = entry.getValue();
                    int stackX = leftPos + 18 + (i % 7) * 18;
                    int stackY = y + 1;
                    guiGraphics.renderItem(stack, stackX, stackY);

                    if (mouseX >= stackX && mouseX <= stackX + 16 && mouseY >= stackY && mouseY <= stackY + 16)
                        guiGraphics.renderTooltip(this.font, stack, mouseX, mouseY);

                    i++;
                }

                Component found = Component.translatable("label.primordial_park.fossils_found")
                        .append(Component.literal(": "))
                        .append(Component.literal(Integer.toString(fossilsFound)).withStyle(fossilsFound == i ? ChatFormatting.GOLD : ChatFormatting.AQUA))
                        .append(Component.literal("/%s".formatted(i)));

                guiGraphics.drawString(font, found, entityMinX, entityMaxY + 6, 0x000000, false);

                guiGraphics.enableScissor(boxLeft, boxTop, boxRight, boxBottom);

                int currentY = boxTop - (int) scrollAmount;
                for (Component paragraph : entryContent.paragraphs) {
                    guiGraphics.drawWordWrap(this.font, paragraph, boxLeft, currentY, boxRight - boxLeft, 0x000000);
                    currentY += this.font.wordWrapHeight(paragraph, lineWidth) + 4;
                }

                guiGraphics.disableScissor();

                setHomeButtonsVisibility(false);
                backButton.visible = true;
                forwardButton.visible = false;
                setEntryButtonVisibility(integer -> false);
            }
            default -> {
                setHomeButtonsVisibility(false);
                hidePageButtons();
                setEntryButtonVisibility(integer -> false);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (currentChapter == Chapter.SPECIES_ENTRY && mouseX >= entityMinX && mouseX <= entityMaxX && mouseY >= entityMinY && mouseY <= entityMaxY) {
            float scrollSpeed = .15f;

            scrolledScaleMultiplier += (float) scrollY * scrollSpeed;
            scrolledScaleMultiplier = Math.clamp(scrolledScaleMultiplier, .4f, 2.5f);

            return true;
        }
        if (currentChapter == Chapter.SPECIES_ENTRY && mouseX >= boxLeft && mouseX <= boxRight && mouseY >= boxTop && mouseY <= boxBottom) {
            float scrollSpeed = 6f;
            int maxScroll = Math.max(0, contentHeight - (boxBottom - boxTop));

            scrollAmount = Math.clamp(scrollAmount - (float) scrollY * scrollSpeed, 0, maxScroll);

            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private MutableComponent obfuscateIf(MutableComponent component, boolean condition) {
        return condition ? component : component.withStyle(ChatFormatting.OBFUSCATED);
    }

    private void hidePageButtons() {
        backButton.visible = false;
        forwardButton.visible = false;
    }

    private void setHomeButtonsVisibility(boolean visible) {
        for (AreaButton button : navButtons) {
            button.visible = visible;
        }
        upgradeButton.visible = visible;
    }

    private void setEntryButtonVisibility(Function<Integer, Boolean> condition) {
        for (int i = 0; i < entryButtons.size(); i++) {
            ArrayList<AbstractWidget> buttons = entryButtons.get(i);

            if (condition.apply(i)) buttons.forEach(button -> button.visible = true);
            else buttons.forEach(button -> button.visible = false);
        }
    }

    private void drawImageWithAlpha(GuiGraphics guiGraphics, ResourceLocation atlasLocation, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(
                atlasLocation,
                x, y,
                (float) 0, (float) 0,
                width, height,
                textureWidth, textureHeight
        );

        RenderSystem.disableBlend();
    }

    private void renderScaledItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) return;

        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(itemScale, itemScale, 1f);

        guiGraphics.renderItem(stack, 0, 0);

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public enum Chapter {
        HOME("home"),
        SPECIES_INDEX("index"),
        SPECIES_ENTRY("entry"),
        TIME_TRAVEL("time_travel"),
        ZOOKEEPING("zookeeping"),
        BIOTECH("biotech");

        public final String id;

        Chapter(String id) {
            this.id = id;
        }
    }

    private static class SpeciesIndexElement {
        public final int TYPE;

        public static final int HEADING = 0;
        public static final int ENTRY = 1;

        public final String heading;
        public final SpeciesIndexEntry entry;

        public SpeciesIndexElement(String heading) {
            this.TYPE = HEADING;
            this.heading = heading;
            this.entry = null;
        }

        public SpeciesIndexElement(SpeciesIndexEntry entry) {
            this.TYPE = ENTRY;
            this.heading = null;
            this.entry = entry;
        }

        @Override
        public String toString() {
            return "%s: %s".formatted(TYPE == HEADING ? "heading" : "entry", TYPE == HEADING ? heading : Objects.requireNonNull(entry).id());
        }
    }

    private record SpeciesIndexPage(ArrayList<SpeciesIndexElement[]> elements) {
        public static SpeciesIndexPage[] parse(Map<TimePeriod, ArrayList<SpeciesIndexEntry>> entries) {
            ArrayList<SpeciesIndexPage> pages = new ArrayList<>();

            int y = 0;
            ArrayList<SpeciesIndexElement[]> rows = new ArrayList<>();

            for (TimePeriod period : TimePeriod.values()) {
                ArrayList<SpeciesIndexEntry> indexEntries = entries.get(period);

                if (indexEntries.isEmpty()) continue;

                if (y >= 176 - lineHeight - entrySize) {
                    y = 0;
                    pages.add(new SpeciesIndexPage(rows));
                    rows = new ArrayList<>();
                }

                rows.add(new SpeciesIndexElement[]{new SpeciesIndexElement(period.id)});
                y += lineHeight;

                ArrayList<SpeciesIndexElement> row = new ArrayList<>();

                int k = 0;
                for (SpeciesIndexEntry entry : indexEntries) {
                    row.add(new SpeciesIndexElement(entry));
                    k++;

                    if (k >= 3) {
                        k = 0;
                        rows.add(row.toArray(new SpeciesIndexElement[0]));
                        row = new ArrayList<>();
                        y += entrySize;

                        if (y >= 176 - entrySize) {
                            y = 0;
                            pages.add(new SpeciesIndexPage(rows));
                            rows = new ArrayList<>();
                        }
                    }
                }

                if (!row.isEmpty())
                    rows.add(row.toArray(new SpeciesIndexElement[0]));
                y += entrySize;
            }

            if (!rows.isEmpty())
                pages.add(new SpeciesIndexPage(rows));

            return pages.toArray(new SpeciesIndexPage[0]);
        }
    }
}
