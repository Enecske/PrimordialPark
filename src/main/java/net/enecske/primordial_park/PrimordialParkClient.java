package net.enecske.primordial_park;

import net.enecske.primordial_park.entity.ModEntities;
import net.enecske.primordial_park.entity.client.GeckoRenderer;
import net.enecske.primordial_park.item.ModItemPropertyGetters;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = PrimordialPark.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = PrimordialPark.MODID, value = Dist.CLIENT)
public class PrimordialParkClient {
    public PrimordialParkClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.GECKO.get(), GeckoRenderer::new);

        event.enqueueWork(ModItemPropertyGetters::register);
    }
}
