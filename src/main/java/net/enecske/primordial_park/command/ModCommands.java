package net.enecske.primordial_park.command;

import com.mojang.brigadier.CommandDispatcher;
import net.enecske.primordial_park.PrimordialPark;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = PrimordialPark.MODID)
public class ModCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        IndexCommand.register(dispatcher);
    }
}
