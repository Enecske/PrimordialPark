package net.enecske.primordial_park.event;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.network.SyncSpeciesIndexPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class ModServerEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SpeciesIndexAttachment attachment = serverPlayer.getData(ModAttachments.SPECIES_INDEX.get());
            PacketDistributor.sendToPlayer(serverPlayer, new SyncSpeciesIndexPayload(attachment));
        }
    }
}
