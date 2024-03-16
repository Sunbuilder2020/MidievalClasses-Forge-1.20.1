package net.sunbuilder2020.medieval_classes.classes;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.medieval_classes.MedievalClasses;
import net.sunbuilder2020.medieval_classes.classes.player_classes.*;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = MedievalClasses.MOD_ID)
public class ClassManager {
    public static String PaladinClassID = "PaladinClass";
    public static String ThiefClassID = "ThiefClass";
    public static String BlacksmithClassID = "BlacksmithClass";
    public static String DwarfClassID = "DwarfClass";
    public static String MonkClassID = "MonkClass";
    public static String ElveClassID = "ElveClass";
    public static String ExecutionerClassID = "ExecutionerClass";
    public static String ArcherClassID = "ArcherClass";
    public static String WizardClassID = "WizardClass";
    public static String GiantClassID = "GiantClass";
    public static String BerserkClassID = "BerserkClass";
    public static String JesterClassID = "JesterClass";
    public static Map<UUID, String> playerClasses = new HashMap<>();
    public static Map<UUID, Boolean> playerIsKing = new HashMap<>();
    public static final UUID CLASS_ATTRIBUTE_MODIFIER_ID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");

    public static void sendNewSeasonStartedMessage(Player player, List<String> availableClasses) {
        String availableClassesString = String.join(", ", availableClasses);

        player.sendSystemMessage(Component.literal("A new Season has just started, the newly available Classes are: " + availableClassesString).withStyle(ChatFormatting.GOLD));
    }

    /**
     *
     * @param messageType
     * '1': Player didn't already have a Class.
     * '2': Player Class was overwritten.
     * '3': A new season has started.
     * '4': The Player was no longer forced a class.
     * '5': The Player was forced a class.
     * '6': The Player was forced a class by a King.
     */

    public static void sendClassMessages(Player player, String playerClass, int messageType) {
        switch (messageType) {
            case 1 -> player.sendSystemMessage(Component.literal("Since you didn't have a Class you were assigned the " + playerClass + "!").withStyle(ChatFormatting.GOLD));
            case 2 -> player.sendSystemMessage(Component.literal("You were assigned the " + playerClass + "!").withStyle(ChatFormatting.GOLD));
            case 3 -> player.sendSystemMessage(Component.literal("Since a new season has started, you were assigned the " + playerClass + "!").withStyle(ChatFormatting.GOLD));
            case 4 -> player.sendSystemMessage(Component.literal("Since you are no longer forced a class, you were reassigned the " + playerClass + "!").withStyle(ChatFormatting.GOLD));
            case 5 -> player.sendSystemMessage(Component.literal("You were forced the " + playerClass + "!").withStyle(ChatFormatting.GOLD));
            case 6 -> player.sendSystemMessage(Component.literal("Since you were killed by a King, you were forced the " + playerClass + "!").withStyle(ChatFormatting.GOLD));
        }
    }

        public static void setClass(ServerPlayer player, String playerActiveClass, boolean playerIsKing, String originalClass, int forcedClassTicks) {
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(playerClasses -> {
            playerClasses.setClass(playerActiveClass);
            playerClasses.setIsKing(playerIsKing);
            playerClasses.setOriginalClass(originalClass);
            playerClasses.setRemainingForcedClassTicks(forcedClassTicks);
        });
    }

    public static void startNewSeason(ServerLevel level, int availableClassesAmount) {
        level.getCapability(ClassSeasonsProvider.CLASS_SEASONS).ifPresent(seasons -> {
            seasons.setCurrentSeason(seasons.getCurrentSeason() + 1);
            seasons.setAvailableClasses(new ArrayList<>());

            List<String> availableClasses = new ArrayList<>();

            for (int i = 0; i < availableClassesAmount; ) {
                String randomClass = ClassManager.getRandomValidClass(level);
                if (!availableClasses.contains(randomClass)) {
                    availableClasses.add(randomClass);
                    i++;
                }

                if (availableClasses.size() >= ClassManager.getAllClasses().size()) {
                    break;
                }
            }

            seasons.setAvailableClasses(availableClasses);

            List<ServerPlayer> onlinePlayers = level.getServer().getPlayerList().getPlayers();

            for (ServerPlayer player : onlinePlayers) {
                ClassManager.sendNewSeasonStartedMessage(player, availableClasses);

                player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                    String randomClass = ClassManager.getRandomValidClass(level);

                    ClassManager.setClass(player, randomClass, classes.getIsKing(), "", -1);

                    ClassManager.applyClassChanges(player);

                    ClassManager.sendClassMessages(player, classes.getClasses(), 3);
                });

            }
        });
    }

    public static void applyClassChanges(Player player) {
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            clearPlayerClassAttributes(player);
            resetPlayerSize(player);

            if(classes.isClass(PaladinClassID)) {
                PaladinClass.applyClassChanges(player);
            } else if(classes.isClass(ThiefClassID)) {
                ThiefClass.applyClassChanges(player);
            } else if(classes.isClass(BlacksmithClassID)) {
                BlacksmithClass.applyClassChanges(player);
            } else if(classes.isClass(DwarfClassID)) {
                DwarfClass.applyClassChanges(player);
            } else if(classes.isClass(MonkClassID)) {
                MonkClass.applyClassChanges(player);
            } else if(classes.isClass(ElveClassID)) {
                ElveClass.applyClassChanges(player);
            } else if(classes.isClass(ArcherClassID)) {
                ArcherClass.applyClassChanges(player);
            } else if(classes.isClass(WizardClassID)) {
                WizardClass.applyClassChanges(player);
            } else if(classes.isClass(GiantClassID)) {
                GiantClass.applyClassChanges(player);
            } else if(classes.isClass(BerserkClassID)) {
                BerserkClass.applyClassChanges(player);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof Player) {
            applyClassChanges((Player) event.getEntity());
        }
    }

    public static void clearPlayerClassAttributes(Player player) {
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
        AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance holySpellPowerAttribute = player.getAttribute(AttributeRegistry.HOLY_SPELL_POWER.get());
        AttributeInstance spellPowerAttribute = player.getAttribute(AttributeRegistry.SPELL_POWER.get());

        if(attackDamageAttribute.getModifier(CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackDamageAttribute.removeModifier(CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(attackSpeedAttribute.getModifier(CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackSpeedAttribute.removeModifier(CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(armorAttribute.getModifier(CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            armorAttribute.removeModifier(CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(armorToughnessAttribute.getModifier(CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            armorToughnessAttribute.removeModifier(CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(healthAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            healthAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(speedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            speedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if (holySpellPowerAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            holySpellPowerAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if (spellPowerAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            spellPowerAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
    }

    public static void resetPlayerSize(Player player) {
        ScaleData heightScaleData = ScaleTypes.HEIGHT.getScaleData(player);
        ScaleData widthScaleData = ScaleTypes.WIDTH.getScaleData(player);
        ScaleData stepHeightScaleData = ScaleTypes.STEP_HEIGHT.getScaleData(player);
        ScaleData reachScaleData = ScaleTypes.REACH.getScaleData(player);
        ScaleData miningSpeedScaleData = ScaleTypes.MINING_SPEED.getScaleData(player);
        ScaleData dropsScaleData = ScaleTypes.DROPS.getScaleData(player);
        ScaleData heldItemScaleData = ScaleTypes.HELD_ITEM.getScaleData(player);
        ScaleData projectilesScaleData = ScaleTypes.PROJECTILES.getScaleData(player);
        ScaleData thirdPersonScaleData = ScaleTypes.THIRD_PERSON.getScaleData(player);
        ScaleData visibilityScaleData = ScaleTypes.VISIBILITY.getScaleData(player);

        heightScaleData.setScale(1.0F);
        widthScaleData.setScale(1.0F);
        stepHeightScaleData.setScale(1.0F);
        miningSpeedScaleData.setScale(1.0F);
        reachScaleData.setScale(1.0F);
        dropsScaleData.setScale(1.0F);
        heldItemScaleData.setScale(1.0F);
        projectilesScaleData.setScale(1.0F);
        thirdPersonScaleData.setScale(1.0F);
        visibilityScaleData.setScale(1.0F);
    }

    public static String getRandomValidClass(ServerLevel level) {
        AtomicReference<String> result = new AtomicReference<>();

        level.getCapability(ClassSeasonsProvider.CLASS_SEASONS).ifPresent(seasons -> {
            List<String> classes = new ArrayList<>(seasons.getAvailableClasses());
            if (!classes.isEmpty()) {
                Random rand = new Random();
                result.set(classes.get(rand.nextInt(classes.size())));
            }
        });

        if (result.get() != null) {
            return result.get();
        } else {
            List<String> classes = getAllClasses();

            Random rand = new Random();
            return classes.get(rand.nextInt(classes.size()));
        }
    }

    public static List<String> getAllClasses() {
        List<String> classes = Arrays.asList(
                PaladinClassID, ThiefClassID, BlacksmithClassID, DwarfClassID, MonkClassID, ElveClassID,
                ExecutionerClassID, ArcherClassID, WizardClassID, GiantClassID, BerserkClassID, JesterClassID
        );

        return classes;
    }
}
