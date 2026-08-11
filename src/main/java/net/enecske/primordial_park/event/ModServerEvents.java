package net.enecske.primordial_park.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.item.ModItems;
import net.enecske.primordial_park.network.SyncSpeciesIndexPayload;
import net.enecske.primordial_park.villager.ModVillagerProfessions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class ModServerEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SpeciesIndexAttachment attachment = serverPlayer.getData(ModAttachments.SPECIES_INDEX.get());
            PacketDistributor.sendToPlayer(serverPlayer, new SyncSpeciesIndexPayload(attachment));
        }
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == ModVillagerProfessions.PALEONTOLOGIST.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 16),
                    new ItemStack(ModItems.HANDBOOK.get(), 1),
                    3, 15, 0.2f
            ));
            trades.get(1).add((trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(Items.BRUSH, 1),
                    8, 1, 0.2f
            ));
        }
    }

    @SubscribeEvent
    public static void addWandererTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        rareTrades.add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, random.nextIntBetweenInclusive(28, 34)),
                Optional.of(new ItemCost(Items.DIAMOND, random.nextIntBetweenInclusive(4, 8))),
                ModItems.HandbookUpgrades.ZOO_BASIC,
                1, 30, 0.2f
        ));
    }
}
