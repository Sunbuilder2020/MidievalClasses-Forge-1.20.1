package net.sunbuilder2020.medieval_classes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import net.sunbuilder2020.medieval_classes.classes.ClassSeasonsProvider;
import net.sunbuilder2020.medieval_classes.classes.PlayerClassesProvider;

import java.util.*;
import java.util.function.Supplier;

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
                        .then(Commands.literal("setKing")
                                .then(Commands.argument("playerName", EntityArgument.player())
                                        .then(Commands.argument("option", BoolArgumentType.bool())
                                                .executes(context -> executeSetKing(context, EntityArgument.getPlayer(context, "playerName"), BoolArgumentType.getBool(context, "option"))))))
        );
    }

    @SubscribeEvent
    public static void onRegisterInfoCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("classInfo")
                        .requires(source -> source.hasPermission(0))
                        .then(Commands.literal("getSelf")
                                .executes(context -> executeGetClassInfo(context, Objects.requireNonNull(context.getSource().getPlayer()))))
                        .then(Commands.literal("getClass")
                                .then(Commands.argument("option", StringArgumentType.word())
                                        .suggests((context, builder) -> builder.suggest("paladin").suggest("giant").suggest("king_status").suggest("jester").suggest("berserk").suggest("wizard").suggest("thief").suggest("archer").suggest("executioner").suggest("blacksmith").suggest("elve").suggest("monk").suggest("dwarf").buildFuture())
                                                .executes(context -> executeGetClassAbilities(context, StringArgumentType.getString(context, "option")))))
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

    public static int executeGetClassAbilities(CommandContext<CommandSourceStack> context, String option) {
        ServerPlayer player = context.getSource().getPlayer();

        String message = "Abilities of Class: " + option + "\n"; // Start the message with the class name

        // Append abilities based on the class
        switch (option) {
            case "paladin" -> message += """
                    - Deals +2 Damage
                    - Takes -5% Damage
                    - Reduces the Damage dealt to friendly entities around you
                    - Your armor takes more Durability Damage""";
            case "thief" -> message += """
                    - Has more Attack Speed
                    - Has reduced Armor
                    - Becomes fully invisible when sneaking and not in Combat
                    - Gets speed after being hit""";
            case "blacksmith" -> message += """
                    -Has more Armor Toughness" +
                    -Has reduced Movement Speed" +
                    -Has more Health" +
                    -May light an entity and all around it on fire and deal extra Damage after hitting it""";
            case "dwarf" -> message += """
                    -Is 1 Block Tall +
                    -Mines Blocks faster +
                    -May random ores, when mining stone-like blocks""";
            case "monk" -> message += """
                    - Has more Holy Spell Power +
                    -Has reduced Armor Toughness +
                    -Heals all friendly entities around you""";
            case "elve" -> message += """
                    -Can step 1 Block high
                    -Has more Movement Speed
                    -Has more Attack Speed
                    -May heal more Health when healing""";
            case "executioner" -> message += """
                    -You may reduce a targets Hunger after hitting it
                    -You may inflict Wither II and Darkness I after hitting an Entity
                    -Deals more Damage the more Players are around him""";
            case "archer" -> message += """
                    -Deals more Damage with arrows
                    -Has more Movement Speed
                    -Inflicts Glowing after hitting an entity with a bow
                    -Shoots 3 Arrows every 5 Bow Shots""";
            case "wizard" -> message += """
                    -Has reduced Armor
                    -Has more Spell Power
                    -May inflict a random negative effect after dealing melee damage""";
            case "giant" -> message += """
                    -Is 3 Blocks tall
                    -Has reduced Attack Speed
                    -Has increased Attack Damage""";
            case "berserk" -> message += """
                    -Takes more Damage
                    -Has more Movement Speed
                    -Deals more Damage with Axes
                    -Heals for a bit, after dealing Damage""";
            case "jester" -> message += """
                    -Damage dealt is Multiplied with a random Number
                    -Damage Taken is multiplied with a random Number
                    -May teleport and become fully invisible after taking Damage
                    -May activate a totem of Undying when dying
                    -May not activate a totem of Undying""";
            case "king_status" -> message += """
                    -A player can have a class normal class while being the King
                    -When the King kills a Player he gives them a random temporary Class
                    -Does not grant a new Class if the players Class was already forced
                    -When a King gets killed by another Player the King Title gets transferred to the killer""";
            default -> {
                message = "This Class doesn't exist!";
                if(player != null) player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
                else context.getSource().sendSuccess((Supplier<Component>) Component.literal(message).withStyle(ChatFormatting.RED), false);

                return 1;
            }
        }

        if(player != null) player.sendSystemMessage(Component.literal(message));
        else context.getSource().sendSuccess((Supplier<Component>) Component.literal(message), false);


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

    private static int executeSetKing(CommandContext<CommandSourceStack> context, ServerPlayer player, boolean option) {
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            if (classes.getIsKing() == option) context.getSource().getPlayer().sendSystemMessage(Component.literal("You can only execute this command to change the Player's King status!").withStyle(ChatFormatting.RED));
            else {
                classes.setIsKing(option);

                ClassManager.sendKingCrownedMessage(player, 0, null);
            }
        });

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
