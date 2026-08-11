package net.enecske.primordial_park.block;

import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.block.entity.PaleontologyTableBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class ModCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.PALEONTOLOGY_TABLE.get(),
                PaleontologyTableBlockEntity::getItemHandler
        );
    }
}
