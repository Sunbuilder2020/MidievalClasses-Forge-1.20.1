package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class ElveClass {
    /**
    Elve Class Abilities:
        -has 1 Block Step Height
        -has +10% speed
        -has +0.2 Attack Speed
        -has a 20% chance to heal +2 HP, when healing
     */

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);

        if (speedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            speedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if (attackSpeedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackSpeedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier speedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "elve_class_speed_bonus", 0.10, AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeModifier attackSpeedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "elve_class_attack_speed_bonus", -0.2, AttributeModifier.Operation.ADDITION);

        speedAttribute.addTransientModifier(speedModifier);
        attackSpeedAttribute.addTransientModifier(attackSpeedModifier);

        ScaleData stepHeightScaleData = ScaleTypes.STEP_HEIGHT.getScaleData(player);

        stepHeightScaleData.setScale(1.5F);
    }

    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) {
            entity.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if(classes.isClass(ClassManager.ElveClassID) && entity.getRandom().nextDouble() <= 0.20D) {
                    entity.heal(2.0F);
                }
            });
        }
    }
}
