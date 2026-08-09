package net.enecske.primordial_park;

import net.enecske.primordial_park.network.OpenHandbookUpgradeScreenPayload;
import net.enecske.primordial_park.network.SyncSpeciesIndexPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class ModNetwork {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0");

        registrar.playToServer(
                OpenHandbookUpgradeScreenPayload.TYPE,
                OpenHandbookUpgradeScreenPayload.STREAM_CODEC,
                OpenHandbookUpgradeScreenPayload::handleOnServer
        );

        registrar.playToClient(
                SyncSpeciesIndexPayload.TYPE,
                SyncSpeciesIndexPayload.STREAM_CODEC,
                SyncSpeciesIndexPayload::handleOnClient
        );
    }
}
