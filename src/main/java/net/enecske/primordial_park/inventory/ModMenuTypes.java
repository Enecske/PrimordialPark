package net.enecske.primordial_park.inventory;

import net.enecske.primordial_park.PrimordialPark;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, PrimordialPark.MODID);

    public static final Supplier<MenuType<HandbookUpgradeMenu>> HANDBOOK_UPGRADE =
            MENUS.register("handbook_upgrade", () ->
                    IMenuTypeExtension.create((containerId, inv, data) -> new HandbookUpgradeMenu(containerId, inv)));

    public static final Supplier<MenuType<PaleontologyTableMenu>> PALEONTOLOGY_TABLE =
            MENUS.register("paleontology_table", () ->
                    IMenuTypeExtension.create(PaleontologyTableMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
