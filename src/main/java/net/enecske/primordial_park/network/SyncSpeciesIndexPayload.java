package net.enecske.primordial_park.network;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncSpeciesIndexPayload(SpeciesIndexAttachment attachment) implements CustomPacketPayload {
    public static final Type<SyncSpeciesIndexPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "sync_species_index"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpeciesIndexPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(SpeciesIndexAttachment.CODEC),
            SyncSpeciesIndexPayload::attachment,
            SyncSpeciesIndexPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnClient(SyncSpeciesIndexPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof LocalPlayer) {
                context.player().setData(ModAttachments.SPECIES_INDEX, payload.attachment());
            }
        });
    }
}
