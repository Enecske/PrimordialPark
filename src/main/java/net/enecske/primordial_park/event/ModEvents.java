package net.enecske.primordial_park.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.inventory.menu.FossilPouchMenu;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.ModItems;
import net.enecske.primordial_park.item.component.FossilPouchComponent;
import net.enecske.primordial_park.item.custom.FossilPouchItem;
import net.enecske.primordial_park.network.SyncSpeciesIndexPayload;
import net.enecske.primordial_park.villager.ModVillagerProfessions;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class ModEvents {
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

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) return;

        ItemEntity itemEntity = event.getItemEntity();
        ItemStack groundStack = itemEntity.getItem();

        if (groundStack.get(ModDataComponents.FOSSIL) == null || groundStack.getItem() instanceof FossilPouchItem)
            return;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack pouch = player.getInventory().getItem(i);

            if (pouch.getItem() instanceof FossilPouchItem &&
                    pouch.getOrDefault(ModDataComponents.FOSSIL_POUCH.get(), FossilPouchComponent.EMPTY).autoPickup()) {

                ItemStackHandler handler = new ItemStackHandler(FossilPouchMenu.POUCH_SIZE);
                ItemContainerContents contents = pouch.getOrDefault(ModDataComponents.FOSSIL_POUCH, FossilPouchComponent.EMPTY).contents();

                NonNullList<ItemStack> list = NonNullList.withSize(FossilPouchMenu.POUCH_SIZE, ItemStack.EMPTY);
                contents.copyInto(list);
                for (int slot = 0; slot < FossilPouchMenu.POUCH_SIZE; slot++) {
                    handler.setStackInSlot(slot, list.get(slot));
                }

                ItemStack remainder = ItemHandlerHelper.insertItem(handler, groundStack.copy(), false);

                if (remainder.getCount() != groundStack.getCount()) {
                    NonNullList<ItemStack> updatedList = NonNullList.withSize(FossilPouchMenu.POUCH_SIZE, ItemStack.EMPTY);
                    for (int slot = 0; slot < FossilPouchMenu.POUCH_SIZE; slot++) {
                        updatedList.set(slot, handler.getStackInSlot(slot));
                    }
                    pouch.set(ModDataComponents.FOSSIL_POUCH, new FossilPouchComponent(ItemContainerContents.fromItems(updatedList), true));
                    if (player.containerMenu instanceof FossilPouchMenu menu) menu.loadData();

                    player.getInventory().removeItem(new ItemStack(groundStack.getItem(), groundStack.getCount() - remainder.getCount()));

                    if (remainder.isEmpty()) {
                        groundStack.setCount(0);
                        itemEntity.discard();
                        return;
                    } else {
                        groundStack.setCount(remainder.getCount());
                    }
                }
            }
        }
    }
}
