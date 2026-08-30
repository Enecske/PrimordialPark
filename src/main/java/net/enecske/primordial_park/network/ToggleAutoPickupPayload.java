package net.enecske.primordial_park.network;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.inventory.menu.FossilPouchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ToggleAutoPickupPayload implements CustomPacketPayload {
    public static final ToggleAutoPickupPayload INSTANCE = new ToggleAutoPickupPayload();

    public static final Type<ToggleAutoPickupPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "toggle_auto_pickup"));

    public static final StreamCodec<FriendlyByteBuf, ToggleAutoPickupPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(@SuppressWarnings("unused") ToggleAutoPickupPayload packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof FossilPouchMenu menu) {
                menu.toggleAutoPickup();
            }
        });
    }
}
