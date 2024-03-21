package net.sunbuilder2020.medieval_classes.classes.player_classes;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.medieval_classes.MedievalClasses;
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

@Mod.EventBusSubscriber(modid = MedievalClasses.MOD_ID)
public class GiantClass {
    /**
    Dwarf Class Abilities:
        -is 3 Block tall
        -has +6 Damage
        -has -50% Attack Speed
        -has +30% Speed
     */

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
        setPlayerSize(player, 1.65f);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (speedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            speedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if (attackSpeedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackSpeedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if (attackDamageAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackDamageAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier speedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "giant_class_speed_bonus", 0.3, AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeModifier attackSpeedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "giant_class_attack_speed_reduction", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeModifier attackDamageModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "giant_class_attack_damage_bonus", 6, AttributeModifier.Operation.ADDITION);
        speedAttribute.addTransientModifier(speedModifier);
        attackSpeedAttribute.addTransientModifier(attackSpeedModifier);
        attackDamageAttribute.addTransientModifier(attackDamageModifier);
    }

    public static void setPlayerSize(Entity entity, float scale) {
        ScaleData heightScaleData = ScaleTypes.HEIGHT.getScaleData(entity);
        ScaleData widthScaleData = ScaleTypes.WIDTH.getScaleData(entity);
        ScaleData reachScaleData = ScaleTypes.REACH.getScaleData(entity);
        ScaleData miningSpeedScaleData = ScaleTypes.MINING_SPEED.getScaleData(entity);
        //ScaleData dropsScaleData = ScaleTypes.DROPS.getScaleData(entity);
        //ScaleData heldItemScaleData = ScaleTypes.HELD_ITEM.getScaleData(entity);
        ScaleData projectilesScaleData = ScaleTypes.PROJECTILES.getScaleData(entity);
        //ScaleData thirdPersonScaleData = ScaleTypes.THIRD_PERSON.getScaleData(entity);
        ScaleData visibilityScaleData = ScaleTypes.VISIBILITY.getScaleData(entity);

        heightScaleData.setScale(scale);
        widthScaleData.setScale(scale);
        reachScaleData.setScale(1.3F);
        miningSpeedScaleData.setScale(1.3F);
        //dropsScaleData.setScale(scale);
        //heldItemScaleData.setScale(scale);
        projectilesScaleData.setScale(scale);
        //thirdPersonScaleData.setScale(scale);
        visibilityScaleData.setScale(scale);
    }
}
