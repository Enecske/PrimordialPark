package net.enecske.primordial_park;

import com.mojang.logging.LogUtils;
import net.enecske.primordial_park.block.ModBlockEntities;
import net.enecske.primordial_park.block.ModBlocks;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.ModEntities;
import net.enecske.primordial_park.inventory.ModMenuTypes;
import net.enecske.primordial_park.item.ModCreativeModeTabs;
import net.enecske.primordial_park.item.ModDataComponents;
import net.enecske.primordial_park.item.ModItems;
import net.enecske.primordial_park.villager.ModPoiTypes;
import net.enecske.primordial_park.villager.ModVillagerProfessions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(PrimordialPark.MODID)
public class PrimordialPark {
    public static final String MODID = "primordial_park";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public PrimordialPark(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModDataComponents.register(modEventBus);
        ModAttachments.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModEntities.register(modEventBus);

        ModPoiTypes.register(modEventBus);
        ModVillagerProfessions.register(modEventBus);

        ModMenuTypes.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
