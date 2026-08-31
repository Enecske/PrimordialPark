package net.enecske.primordial_park.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.enecske.primordial_park.PrimordialPark;
import net.enecske.primordial_park.entity.ModAttachments;
import net.enecske.primordial_park.entity.attachment.SpeciesIndexAttachment;
import net.enecske.primordial_park.helper.FossilDiscoveryHelper;
import net.enecske.primordial_park.network.SyncSpeciesIndexPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class IndexCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(ResourceLocation.fromNamespaceAndPath(PrimordialPark.MODID, "index").toString())
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.literal("clear")
                                        .executes(IndexCommand::clear))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("species", StringArgumentType.word())
                                                .then(Commands.argument("id", StringArgumentType.word())
                                                        .executes(IndexCommand::add))))
                        )
        );
    }

    private static int clear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        FossilDiscoveryHelper.clearSpeciesIndex(player);

        context.getSource().sendSuccess(() ->
                Component.literal("Cleared fossil index for %s".formatted(player.getScoreboardName())), true);
        return 1;
    }

    private static int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        String species = StringArgumentType.getString(context, "species");
        String id = StringArgumentType.getString(context, "id");

        SpeciesIndexAttachment attachment = player.getData(ModAttachments.SPECIES_INDEX.get());
        if (attachment.hasFossil(species, id)) {
            context.getSource().sendFailure(Component.literal("Fossil is already registered for %s".formatted(player.getScoreboardName())));
            return 0;
        }

        if (!attachment.hasSpecies(species)) attachment = attachment.addSpecies(species);
        attachment = attachment.addFossil(species, id);

        player.setData(ModAttachments.SPECIES_INDEX.get(), attachment);
        PacketDistributor.sendToPlayer(player, new SyncSpeciesIndexPayload(attachment));

        context.getSource().sendSuccess(() ->
                Component.literal("Fossil registered for %s".formatted(player.getScoreboardName())), true);
        return 1;
    }
}
