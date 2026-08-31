package net.enecske.primordial_park.helper;

import net.enecske.primordial_park.client.gui.FossilToast;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.component.FossilComponent;
import net.enecske.primordial_park.network.ShowFossilToastPayload;
import net.enecske.primordial_park.network.SyncSpeciesIndexPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class FossilDiscoveryHelper {
    public static void checkAndRegisterFossil(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return;

        FossilComponent component = stack.get(ModDataComponents.FOSSIL.get());
        if (component == null) return;

        String species = component.species();
        String id = component.id();

        SpeciesIndexAttachment attachment = player.getData(ModAttachments.SPECIES_INDEX);
        if (attachment.hasFossil(species, id)) return;

        if (!attachment.hasSpecies(species)) attachment = attachment.addSpecies(species);
        attachment = attachment.addFossil(species, id);

        player.setData(ModAttachments.SPECIES_INDEX.get(), attachment);
        PacketDistributor.sendToPlayer(player, new SyncSpeciesIndexPayload(attachment));
        PacketDistributor.sendToPlayer(player, new ShowFossilToastPayload(new FossilToast(id, stack.copy())));
    }

    public static void clearSpeciesIndex(ServerPlayer player) {
        player.setData(ModAttachments.SPECIES_INDEX.get(), SpeciesIndexAttachment.EMPTY);
        PacketDistributor.sendToPlayer(player, new SyncSpeciesIndexPayload(SpeciesIndexAttachment.EMPTY));
    }
}
