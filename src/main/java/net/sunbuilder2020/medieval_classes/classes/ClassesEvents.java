package net.sunbuilder2020.medieval_classes.classes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.medieval_classes.MedievalClasses;

import java.util.List;

@Mod.EventBusSubscriber(modid = MedievalClasses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClassesEvents {
    @SubscribeEvent
    public static void attachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerClassesProvider.PLAYER_CLASSES).isPresent()) {
                event.addCapability(new ResourceLocation(MedievalClasses.MOD_ID, "player_classes"), new PlayerClassesProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerClasses.class);
        event.register(ClassSeasons.class);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(playerClasses -> {
                player.serverLevel().getCapability(ClassSeasonsProvider.CLASS_SEASONS).ifPresent(seasons -> {
                    if (playerClasses.getLastSeasonOnline() < seasons.getCurrentSeason()) {
                        ClassManager.sendNewSeasonStartedMessage(player, seasons.getAvailableClasses());

                        String randomClass = ClassManager.getRandomValidClass((ServerLevel) event.getEntity().level());

                        ClassManager.setClass(player, randomClass, playerClasses.getIsKing(), "", -1);

                        ClassManager.applyClassChanges(player);

                        ClassManager.sendClassMessages(player, randomClass, 3);
                    }

                    if(seasons.getCurrentSeason() <= 0) {
                        ClassManager.startNewSeason(player.serverLevel(), 6);
                    }

                    if (ClassManager.getAllClasses().contains(playerClasses.getClasses())) {
                        String randomClass = ClassManager.getRandomValidClass((ServerLevel) event.getEntity().level());

                        ClassManager.setClass(player, randomClass, playerClasses.getIsKing(), "", -1);

                        ClassManager.applyClassChanges(player);

                        ClassManager.sendClassMessages(player, playerClasses.getClasses(), 1);
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.serverLevel().getCapability(ClassSeasonsProvider.CLASS_SEASONS).ifPresent(seasons -> {
                player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                    classes.setLastSeasonOnline(seasons.getCurrentSeason());
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            ServerPlayer newPlayer = (ServerPlayer) event.getEntity();
            ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();

            // Conceptually retrieve the saved class data
            String savedClass = ClassManager.playerClasses.get(oldPlayer.getUUID());
            boolean isKing = ClassManager.playerIsKing.get(oldPlayer.getUUID());
            newPlayer.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(newClasses -> {
                if (savedClass != null) {
                    ClassManager.setClass(newPlayer, savedClass, isKing, "", -1);

                    ClassManager.applyClassChanges(newPlayer);

                    ClassManager.playerClasses.remove(oldPlayer.getUUID());
                    ClassManager.playerIsKing.remove(oldPlayer.getUUID());
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                String playerClass = "";
                if(classes.isForcedClass()) playerClass = classes.getOriginalClass();
                else playerClass = classes.getClasses();
                boolean isKing = classes.getIsKing();

                ClassManager.playerClasses.put(player.getUUID(), playerClass);
                ClassManager.playerIsKing.put(player.getUUID(), isKing);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            if(classes.isForcedClass()) {
                classes.setRemainingForcedClassTicks(classes.getRemainingForcedClassTicks() - 1);

                if(classes.getRemainingForcedClassTicks() == 0) {
                    ClassManager.setClass((ServerPlayer) player, classes.getOriginalClass(), classes.getIsKing(), "", -1);

                    ClassManager.applyClassChanges(player);

                    ClassManager.sendClassMessages(player, classes.getClasses(), 4);
                }
            }
        });
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Level> event) {
        if (!(event.getObject() instanceof ServerLevel)) {
            return;
        }

        ClassSeasonsProvider provider = new ClassSeasonsProvider();
        event.addCapability(new ResourceLocation(MedievalClasses.MOD_ID, "class_seasons"), provider);
    }

    @SubscribeEvent
    public static void saveClassData(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            CompoundTag playerData = player.getPersistentData();
            classes.saveNBTData(playerData);
        });
    }

    @SubscribeEvent
    public static void loadClassData(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            CompoundTag playerData = player.getPersistentData();
            classes.loadNBTData(playerData);
        });
    }

    public static void onServerStop(ServerStoppingEvent event) {

    }
}
