package net.sunbuilder2020.midieval_classes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.ClassSeasonsProvider;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class CustomCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("class")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("get")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .executes(context -> executeGetClass(context, EntityArgument.getPlayer(context, "playerName")))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .then(Commands.argument("option", StringArgumentType.word())
                                                .suggests((context, builder) -> builder.suggest("paladin").suggest("giant").suggest("jester").suggest("berserk").suggest("wizard").suggest("thief").suggest("archer").suggest("executioner").suggest("blacksmith").suggest("elve").suggest("monk").suggest("dwarf").buildFuture())
                                                .executes(context -> executeSetClass(context, EntityArgument.getPlayer(context, "playerName"), StringArgumentType.getString(context, "option"))))))
                        .then(Commands.literal("startNewSeason")
                                .then(Commands.argument("seasonClassAmount", IntegerArgumentType.integer(0, 12))
                                        .executes(context -> startNewSeason(context, IntegerArgumentType.getInteger(context, "seasonClassAmount"))))));
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
            case "dwarf" -> newPlayerClass = ClassManager.DwarfClassID;
            case "monk" -> newPlayerClass = ClassManager.MonkClassID;
            case "elve" -> newPlayerClass = ClassManager.ElveClassID;
            case "executioner" -> newPlayerClass = ClassManager.ExecutionerClassID;
            case "archer" -> newPlayerClass = ClassManager.ArcherClassID;
            case "wizard" -> newPlayerClass = ClassManager.WizardClassID;
            case "giant" -> newPlayerClass = ClassManager.GiantClassID;
            case "berserk" -> newPlayerClass = ClassManager.BerserkClassID;
            case "jester" -> newPlayerClass = ClassManager.JesterClassID;
        }

        String finalNewPlayerClass = newPlayerClass;
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            classes.setClass(finalNewPlayerClass);

            ClassManager.applyClassChanges((Player) player);

            ModMessages.sendToClient(new ClassDataSyncS2CPacket(String.valueOf(classes.getClass())), player);

            ClassManager.sendClassAssignedMessage(player, finalNewPlayerClass);
        });

        context.getSource().sendSystemMessage(Component.literal(player.getName().getString() + "'s Profession is now set to " + option));

        return 1;
    }

    private static int startNewSeason(CommandContext<CommandSourceStack> context, int availableClassesAmount) {
        Level level = context.getSource().getLevel();

        if(!level.isClientSide) {
            level.getCapability(ClassSeasonsProvider.CLASS_SEASONS).ifPresent(seasons -> {
                seasons.setCurrentSeason(seasons.getCurrentSeason() + 1);
                seasons.setAvailableClasses(new ArrayList<>());

                List<String> availableClasses = new ArrayList<>();

                for (int i = 0; i < availableClassesAmount; ) {
                    String randomClass = ClassManager.getRandomValidClass((ServerLevel) level);
                    if(!availableClasses.contains(randomClass)) {
                        availableClasses.add(randomClass);
                        i++;
                    }

                    if(availableClasses.size() >= ClassManager.getAllClasses().size()) {
                        break;
                    }
                }

                seasons.setAvailableClasses(availableClasses);

                List<ServerPlayer> onlinePlayers = context.getSource().getServer().getPlayerList().getPlayers();

                for (Player player : onlinePlayers) {
                    ClassManager.sendNewSeasonStartedMessage(player, availableClasses);

                    player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                        String randomClass = ClassManager.getRandomValidClass((ServerLevel) level);

                        classes.setClass(randomClass);

                        ClassManager.applyClassChanges((Player) player);

                        ModMessages.sendToClient(new ClassDataSyncS2CPacket(randomClass), (ServerPlayer) player);

                        ClassManager.sendClassAssignedMessage(player, randomClass);
                    });

                }
            });
        }

        return 1;
    }
}
