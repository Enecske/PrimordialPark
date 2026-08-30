package net.enecske.primordial_park.network;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.inventory.menu.HandbookUpgradeMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenHandbookUpgradeScreenPayload() implements CustomPacketPayload {
    public static final OpenHandbookUpgradeScreenPayload INSTANCE = new OpenHandbookUpgradeScreenPayload();

    public static final Type<OpenHandbookUpgradeScreenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "open_handbook_upgrade_screen"));

    public static final StreamCodec<FriendlyByteBuf, OpenHandbookUpgradeScreenPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(@SuppressWarnings("unused") OpenHandbookUpgradeScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player) -> new HandbookUpgradeMenu(containerId, playerInventory),
                        Component.translatable("container.primordial_park.upgrade_handbook")
                ));
            }
        });
    }
}
