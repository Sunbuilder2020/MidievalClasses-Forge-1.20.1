package net.sunbuilder2020.midieval_classes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClasses;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.SetClassC2SPacket;

@Mod.EventBusSubscriber
public class CustomCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("class")
                        .then(Commands.literal("get")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .executes(context -> executeGetClass(context, EntityArgument.getPlayer(context, "playerName")))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .then(Commands.argument("option", StringArgumentType.word())
                                                .suggests((context, builder) -> builder.suggest("paladin").suggest("thief").suggest("blacksmith").buildFuture())
                                                .executes(context -> executeSetClass(context, EntityArgument.getPlayer(context, "playerName"), StringArgumentType.getString(context, "option")))))));
    }

    private static int executeGetClass(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            String playerClass = classes.getClasses();
            context.getSource().sendSystemMessage(Component.literal(player.getName().getString() + "'s Profession is " + playerClass));
        });

        return 1;
    }

    private static int executeSetClass(CommandContext<CommandSourceStack> context, ServerPlayer player, String option) {
        String newPlayerClass = "";

        switch (option) {
            case "paladin" -> newPlayerClass = ClassManager.PaladinClassID;
            case "thief" -> newPlayerClass = ClassManager.ThiefClassID;
            case "blacksmith" -> newPlayerClass = ClassManager.BlacksmithClassID;

        }
        ModMessages.sendToServer(new SetClassC2SPacket(newPlayerClass));
        context.getSource().sendSystemMessage(Component.literal(player.getName().getString() + "'s Profession is now set to " + option));

        return 1;
    }
}
