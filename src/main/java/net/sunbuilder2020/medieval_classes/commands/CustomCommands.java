package net.sunbuilder2020.medieval_classes.commands;

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
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import net.sunbuilder2020.medieval_classes.classes.ClassSeasonsProvider;
import net.sunbuilder2020.medieval_classes.classes.PlayerClassesProvider;

import java.util.*;

@Mod.EventBusSubscriber
public class CustomCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("class")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .then(Commands.argument("option", StringArgumentType.word())
                                                .suggests((context, builder) -> builder.suggest("paladin").suggest("giant").suggest("jester").suggest("berserk").suggest("wizard").suggest("thief").suggest("archer").suggest("executioner").suggest("blacksmith").suggest("elve").suggest("monk").suggest("dwarf").buildFuture())
                                                .executes(context -> executeSetClass(context, EntityArgument.getPlayer(context, "playerName"), StringArgumentType.getString(context, "option"))))))
                        .then(Commands.literal("force")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .then(Commands.argument("option", StringArgumentType.word())
                                                .suggests((context, builder) -> builder.suggest("paladin").suggest("giant").suggest("jester").suggest("berserk").suggest("wizard").suggest("thief").suggest("archer").suggest("executioner").suggest("blacksmith").suggest("elve").suggest("monk").suggest("dwarf").buildFuture())
                                                .then(Commands.argument("forcedClassDuration", IntegerArgumentType.integer(1))
                                                        .executes(context -> executeForceClass(context, EntityArgument.getPlayer(context, "playerName"), StringArgumentType.getString(context, "option"), IntegerArgumentType.getInteger(context, "forcedClassDuration")))))))
                        .then(Commands.literal("startNewSeason")
                                .then(Commands.argument("seasonClassAmount", IntegerArgumentType.integer(0, 12))
                                        .executes(context -> startNewSeason(context, IntegerArgumentType.getInteger(context, "seasonClassAmount")))))
        );
    }

    @SubscribeEvent
    public static void onRegisterInfoCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("classInfo")
                        .then(Commands.literal("getSelf")
                                .executes(context -> executeGetClassInfo(context, Objects.requireNonNull(context.getSource().getPlayer()))))
                        .requires(source -> source.hasPermission(1))
                        .then(Commands.literal("get")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .executes(context -> executeGetClassInfo(context, EntityArgument.getPlayer(context, "playerName")))))
        );
    }

    private static int executeGetClassInfo(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        ServerLevel level = context.getSource().getLevel();

        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            level.getCapability(ClassSeasonsProvider.CLASS_SEASONS).ifPresent(seasons -> {
                int activeSeason = seasons.getCurrentSeason();
                List<String> availableClasses = seasons.getAvailableClasses();
                String availableClassesString = String.join(", ", availableClasses);

                String playerActiveClass = classes.getClasses();
                boolean playerIsKing = classes.getIsKing();

                boolean isForcedClass = classes.isForcedClass();
                String originalClass = classes.getOriginalClass();
                int forcedClassTicks = classes.getRemainingForcedClassTicks();

                ServerPlayer sender =  context.getSource().getPlayer();
                sender.sendSystemMessage(Component.literal("Info of current season: "));
                sender.sendSystemMessage(Component.literal("    Current Season: " + activeSeason));
                sender.sendSystemMessage(Component.literal("    Current Season's available Classes: " + availableClassesString));
                sender.sendSystemMessage(Component.literal("ClassInfo of Player: " + player.getName().getString()));
                sender.sendSystemMessage(Component.literal("    Player's active Class: " + playerActiveClass));
                sender.sendSystemMessage(Component.literal("    Player is King: " + playerIsKing));
                sender.sendSystemMessage(Component.literal("    Player is Forced Class: " + isForcedClass));
                if(isForcedClass) {
                    sender.sendSystemMessage(Component.literal("    Player's Original Class: " + originalClass));
                    sender.sendSystemMessage(Component.literal("    Forced minutes left: " + forcedClassTicks / 20 / 60));
                }
            });
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

            ClassManager.sendClassMessages(player, finalNewPlayerClass, 2);
        });

        context.getSource().sendSystemMessage(Component.literal(player.getName().getString() + "'s Profession is now set to " + option));

        return 1;
    }

    private static int startNewSeason(CommandContext<CommandSourceStack> context, int availableClassesAmount) {
        ServerLevel level = context.getSource().getLevel();

        ClassManager.startNewSeason(level, availableClassesAmount);

        return 1;
    }

    private static int executeForceClass(CommandContext<CommandSourceStack> context, ServerPlayer player, String option, int duration) {
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
            String originalClass = "";

            if(classes.getOriginalClass().isEmpty()) {
                originalClass = classes.getClasses();
            } else {
                originalClass = classes.getOriginalClass();
            }

            ClassManager.setClass(player, finalNewPlayerClass, classes.getIsKing(), originalClass, duration * 60 * 20);

            ClassManager.applyClassChanges((Player) player);

            ClassManager.sendClassMessages(player, finalNewPlayerClass, 5);
        });

        context.getSource().sendSystemMessage(Component.literal(player.getName().getString() + "'s Profession is now Forced to " + option + " for " + duration + " minutes!"));

        return 1;
    }
}
