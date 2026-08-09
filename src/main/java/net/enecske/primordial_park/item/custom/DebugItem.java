package net.enecske.primordial_park.item.custom;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.network.SyncSpeciesIndexPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class DebugItem extends Item {
    public DebugItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        try {
            if (level.isClientSide) return InteractionResultHolder.sidedSuccess(stack, true);

            String fossil = "mammoth_ivory_tusk";

            SpeciesIndexAttachment attachment = player.getData(ModAttachments.SPECIES_INDEX.get()).addSpecies("woolly_mammoth");

            player.setData(ModAttachments.SPECIES_INDEX.get(), player.isCrouching() ? attachment.removeFossil("woolly_mammoth", fossil) : attachment.addFossil("woolly_mammoth", fossil));

            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncSpeciesIndexPayload(player.getData(ModAttachments.SPECIES_INDEX.get())));

        } catch (Exception e) {
            PrimordialPark.LOGGER.error("error", e);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
