package net.sunbuilder2020.midieval_classes.classes;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.player_classes.*;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
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
    public static final UUID CLASS_ATTRIBUTE_MODIFIER_ID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");

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
}
