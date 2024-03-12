package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;

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
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(playerClasses -> {
                if (playerClasses.getClasses().isEmpty()) {
                    String randomClass = ClassManager.getRandomClass();
                    playerClasses.setClass(randomClass);

                    ClassManager.applyClassChanges(player);
                    ModMessages.sendToClient(new ClassDataSyncS2CPacket(randomClass), player);

                    ClassManager.sendClassAssignedMessage(player, randomClass);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();
            ServerPlayer newPlayer = (ServerPlayer) event.getEntity();

            newPlayer.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(newClasses -> {
                String playerClass = ClassManager.PLAYER_CLASSES.get(oldPlayer.getUUID());

                newClasses.setClass(playerClass);
                ClassManager.applyClassChanges(newPlayer);
                ModMessages.sendToClient(new ClassDataSyncS2CPacket(newClasses.getClasses()), newPlayer);
            });
        }
    }
}
