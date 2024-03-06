package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.player_classes.BlacksmithClass;
import net.sunbuilder2020.midieval_classes.classes.player_classes.PaladinClass;
import net.sunbuilder2020.midieval_classes.classes.player_classes.ThiefClass;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class ClassManager {
    public static String PaladinClassID = "PaladinClass";
    public static String ThiefClassID = "ThiefClass";
    public static String BlacksmithClassID = "BlacksmithClass";
    public static final UUID CLASS_ATTRIBUTE_MODIFIER_ID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");

    public static void applyClassChanges(Player player) {
        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {

            clearPlayerClassAttributes(player);

            if(classes.isClass(PaladinClassID)) {
                PaladinClass.applyClassChanges(player);
            } else if(classes.isClass(ThiefClassID)) {
                ThiefClass.applyClassChanges(player);
            } else if(classes.isClass(BlacksmithClassID)) {
                BlacksmithClass.applyClassChanges(player);
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
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
        AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if(attackDamageAttribute.getModifier(CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackDamageAttribute.removeModifier(CLASS_ATTRIBUTE_MODIFIER_ID);
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
    }
}
