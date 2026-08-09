package net.enecske.primordial_park.event;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.client.gui.HandbookUpgradeScreen;
import net.enecske.primordial_park.client.handbook.HandbookContentLoader;
import net.enecske.primordial_park.client.species_index.EntryContentLoader;
import net.enecske.primordial_park.inventory.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = PrimordialPark.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(HandbookContentLoader.INSTANCE);
        event.registerReloadListener(EntryContentLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.HANDBOOK_UPGRADE.get(), HandbookUpgradeScreen::new);
    }
}
