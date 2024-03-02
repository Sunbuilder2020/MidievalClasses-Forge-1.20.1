package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.player_classes.PaladinClass;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class ClassManager {
    public static final UUID CLASS_ATTRIBUTE_MODIFIER_ID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");

    public static void changePlayerClass(ServerPlayer player, String newClass) {
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(playerClasses -> {
            playerClasses.setClass(newClass);
            applyClassChanges(player);
            ModMessages.sendToClient(new ClassDataSyncS2CPacket(newClass), player);
        });
    }

    public static void applyClassChanges(Player player) {
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            String playerClass = classes.getClasses();

            clearPlayerClassAttributes(player);

            if(classes.isClass(classes.PaladinClassID)) {
                PaladinClass.applyClassChanges(player);
            } else if(classes.isClass(classes.JesterClassID)) {

            }
        });
    }

    public static void clearPlayerClassAttributes(Player player) {
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);

        if(attackDamageAttribute.getModifier(CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackDamageAttribute.removeModifier(CLASS_ATTRIBUTE_MODIFIER_ID);
        }
    }
}