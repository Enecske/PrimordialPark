package net.enecske.primordial_park.item;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.ModEntities;
import net.enecske.primordial_park.item.component.HandbookComponent;
import net.enecske.primordial_park.item.component.HandbookUpgradeComponent;
import net.enecske.primordial_park.item.custom.DebugItem;
import net.enecske.primordial_park.item.custom.HandbookItem;
import net.enecske.primordial_park.item.custom.HandbookUpgradeItem;
import net.enecske.primordial_park.item.custom.UnknownFossilItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

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

    public static final DeferredItem<Item> UNKNOWN_FOSSIL = ITEMS.register("unknown_fossil",
            () -> new UnknownFossilItem(new Item.Properties()
                    .stacksTo(1)
            ));

    public static final DeferredItem<Item> DEBUG = ITEMS.register("debug",
            () -> new DebugItem(new Item.Properties()));

    public static final DeferredItem<Item> GECKO_SPAWN_EGG = ITEMS.register("gecko_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GECKO, 0x31afaf, 0xffac00,
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
