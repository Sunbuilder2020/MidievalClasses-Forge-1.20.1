package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClassesEvents {
    @SubscribeEvent
    public static void attachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerClassesProvider.PLAYER_CLASSES).isPresent()) {
                event.addCapability(new ResourceLocation(MidievalClasses.MOD_ID, "player_classes"), new PlayerClassesProvider());
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
                    if (playerClasses.getClasses().isEmpty() || playerClasses.getLastSeasonOnline() < seasons.getCurrentSeason()) {
                        ClassManager.sendNewSeasonStartedMessage(player, seasons.getAvailableClasses());

                        String randomClass = ClassManager.getRandomValidClass((ServerLevel) event.getEntity().level());
                        playerClasses.setClass(randomClass);

                        ClassManager.applyClassChanges(player);
                        ModMessages.sendToClient(new ClassDataSyncS2CPacket(randomClass), player);

                        ClassManager.sendClassAssignedMessage(player, randomClass);
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
            if (savedClass != null) {
                newPlayer.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(newClasses -> {
                    newClasses.setClass(savedClass);

                    ClassManager.applyClassChanges(newPlayer);
                    ModMessages.sendToClient(new ClassDataSyncS2CPacket(savedClass), newPlayer);
                });

                ClassManager.playerClasses.remove(oldPlayer.getUUID());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                String playerClass = classes.getClasses();

                ClassManager.playerClasses.put(player.getUUID(), playerClass);
            });
        }
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Level> event) {
        if (!(event.getObject() instanceof ServerLevel)) {
            return;
        }

        ClassSeasonsProvider provider = new ClassSeasonsProvider();
        event.addCapability(new ResourceLocation(MidievalClasses.MOD_ID, "class_seasons"), provider);
    }
}
