package net.enecske.primordial_park.network;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.inventory.menu.CalibrationMatrixMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class CalibrateMatrixPayload implements CustomPacketPayload {
    public static final CalibrateMatrixPayload INSTANCE = new CalibrateMatrixPayload();

    public static final Type<CalibrateMatrixPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "calibrate_matrix"));

    public static final StreamCodec<FriendlyByteBuf, CalibrateMatrixPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(@SuppressWarnings("unused") CalibrateMatrixPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof CalibrationMatrixMenu menu)
                menu.calibrate();
        });
    }
}
