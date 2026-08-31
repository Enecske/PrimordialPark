package net.enecske.primordial_park.network;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.client.gui.FossilToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ShowFossilToastPayload(FossilToast toast) implements CustomPacketPayload {
    public static final Type<ShowFossilToastPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "show_fossil_toast_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShowFossilToastPayload> STREAM_CODEC = StreamCodec.composite(
            FossilToast.STREAM_CODEC, ShowFossilToastPayload::toast,
            ShowFossilToastPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnClient(ShowFossilToastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof LocalPlayer) {
                payload.toast().show();
            }
        });
    }
}
