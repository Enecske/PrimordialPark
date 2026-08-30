package net.enecske.primordial_park.item;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.TimePeriod;
import net.enecske.primordial_park.entity.ModEntities;
import net.enecske.primordial_park.item.component.FossilComponent;
import net.enecske.primordial_park.item.component.FossilPouchComponent;
import net.enecske.primordial_park.item.component.HandbookComponent;
import net.enecske.primordial_park.item.component.HandbookUpgradeComponent;
import net.enecske.primordial_park.item.custom.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("unused")
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PrimordialPark.MODID);

    public static final DeferredItem<Item> FOSSIL_CONCRETION = ITEMS.register("fossil_concretion",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> HANDBOOK = ITEMS.register("handbook",
            () -> new HandbookItem(new Item.Properties()
                    .component(ModDataComponents.HANDBOOK.get(), new HandbookComponent(new HandbookComponent.UpgradeComponent(false, false, false, false, false)))
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> HANDBOOK_UPGRADE = ITEMS.register("handbook_upgrade",
            () -> new HandbookUpgradeItem(new Item.Properties()
                    .component(ModDataComponents.HANDBOOK_UPGRADE.get(), new HandbookUpgradeComponent("zoo_basic"))
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> FOSSIL_POUCH = ITEMS.register("fossil_pouch",
            () -> new FossilPouchItem(new Item.Properties()
                    .stacksTo(1)
                    .component(ModDataComponents.FOSSIL_POUCH, FossilPouchComponent.EMPTY)));

    public static final DeferredItem<Item> UNKNOWN_FOSSIL = ITEMS.register("unknown_fossil",
            () -> new UnknownFossilItem(new Item.Properties()
                    .stacksTo(1)));

    public static final DeferredItem<Item> REPORT_CARD = ITEMS.register("report_card",
            () -> new ReportCardItem(new Item.Properties()));

    public static final DeferredItem<Item> RESONANT_SHARD = ITEMS.register("resonant_shard",
            () -> new Item(new Item.Properties()
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> CALIBRATION_MATRIX = ITEMS.register("calibration_matrix",
            () -> new CalibrationMatrixItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));


    public static final ArrayList<DeferredItem<Item>> FOSSILS = new ArrayList<>();
    public static final DeferredItem<Item> MAMMOTH_CRANIUM = registerFossil("mammoth_cranium", "woolly_mammoth", new FossilComponent.ReportCardDataComponent(TimePeriod.ICE_AGE));
    public static final DeferredItem<Item> FROZEN_MAMMOTH_FLESH = registerFossil("frozen_mammoth_flesh", "woolly_mammoth", new FossilComponent.DnaDataComponent("wip"));


    public static final DeferredItem<Item> DEBUG = ITEMS.register("debug",
            () -> new DebugItem(new Item.Properties()));

    public static final DeferredItem<Item> GECKO_SPAWN_EGG = ITEMS.register("gecko_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GECKO, 0x31afaf, 0xffac00,
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static DeferredItem<Item> registerFossil(String id, String species) {
        return ITEMS.register(id,
                () -> new Item(new Item.Properties()
                        .component(ModDataComponents.FOSSIL.get(), new FossilComponent(id, species, Optional.empty()))
                        .rarity(Rarity.UNCOMMON)
                ));
    }

    private static DeferredItem<Item> registerFossil(String id, String species, FossilComponent.DataComponent data) {
        DeferredItem<Item> item = ITEMS.register(id,
                () -> new Item(new Item.Properties()
                        .component(ModDataComponents.FOSSIL.get(), new FossilComponent(id, species, Optional.of(data)))
                        .rarity(Rarity.UNCOMMON)
                ));

        FOSSILS.add(item);

        return item;
    }

    public static class HandbookUpgrades {
        public static final ItemStack ZOO_BASIC = createUpgradeStack("zoo_basic");
        public static final ItemStack ZOO_ADVANCED = createUpgradeStack("zoo_advanced");
        public static final ItemStack BIOTECH_BASIC = createUpgradeStack("biotech_basic");
        public static final ItemStack BIOTECH_ADVANCED = createUpgradeStack("biotech_advanced");
        public static final ItemStack BIOSYNTHESIS = createUpgradeStack("biosynthesis");

        private static ItemStack createUpgradeStack(String id) {
            ItemStack stack = new ItemStack(ModItems.HANDBOOK_UPGRADE.get());
            stack.set(ModDataComponents.HANDBOOK_UPGRADE, new HandbookUpgradeComponent(id));
            return stack;
        }
    }
}
