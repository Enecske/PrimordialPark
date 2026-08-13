package net.enecske.primordial_park.item;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.block.ModBlocks;
import net.enecske.primordial_park.item.component.HandbookComponent;
import net.enecske.primordial_park.item.component.HandbookUpgradeComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PrimordialPark.MODID);

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> PRIMORDIAL_PARK = CREATIVE_MODE_TABS.register("primordial_park_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.HANDBOOK.get()))
                    .title(Component.translatable("creativetab.primordial_park.primordial_park_tab"))
                    .displayItems((itemDisplayParameter, output) -> {
                        output.accept(ModItems.HANDBOOK);

                        ItemStack stack = new ItemStack(ModItems.HANDBOOK.get());
                        stack.set(ModDataComponents.HANDBOOK.get(), new HandbookComponent(new HandbookComponent.UpgradeComponent(true, true, true, true, true)));
                        output.accept(stack);

                        output.accept(ModItems.HANDBOOK_UPGRADE);
                        output.accept(handbookUpgradeWithId("zoo_advanced"));
                        output.accept(handbookUpgradeWithId("biotech_basic"));
                        output.accept(handbookUpgradeWithId("biotech_advanced"));
                        output.accept(handbookUpgradeWithId("biosynthesis"));

                        output.accept(ModItems.FOSSIL_POUCH);

                        output.accept(ModBlocks.PALEONTOLOGY_TABLE);

                        output.accept(ModItems.REPORT_CARD);

                        output.accept(ModItems.FOSSIL_CONCRETION);

                        output.accept(ModItems.GECKO_SPAWN_EGG);
                    })
                    .build());

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> FOSSILS = CREATIVE_MODE_TABS.register("fossils_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.MAMMOTH_CRANIUM.get()))
                    .title(Component.translatable("creativetab.primordial_park.fossils_tab"))
                    .displayItems((parameters, output) ->
                            ModItems.FOSSILS.forEach(output::accept)
                    )
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "primordial_park_tab"))
                    .build());

    // don't forget .withTabsBefore()

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    private static ItemStack handbookUpgradeWithId(String id) {
        ItemStack stack = new ItemStack(ModItems.HANDBOOK_UPGRADE.get());
        stack.set(ModDataComponents.HANDBOOK_UPGRADE.get(), new HandbookUpgradeComponent(id));
        return stack;
    }
}
