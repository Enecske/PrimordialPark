package net.enecske.primordial_park.loot;

import  net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class ModLootEvents {
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(ResourceLocation.withDefaultNamespace("chests/ancient_city"))) {
            LootPool pool = LootPool.lootPool()
                    .name("%s_resonant_shard_pool".formatted(PrimordialPark.MODID))
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.RESONANT_SHARD.get())
                            .setWeight(1)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                    .add(LootItem.lootTableItem(Items.AIR)
                            .setWeight(4))
                    .build();

            event.getTable().addPool(pool);
        }
    }
}
