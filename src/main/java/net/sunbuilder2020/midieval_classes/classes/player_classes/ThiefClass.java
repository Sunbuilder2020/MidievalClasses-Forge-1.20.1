package net.sunbuilder2020.midieval_classes.classes.player_classes;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;
import net.sunbuilder2020.midieval_classes.player_hostility.CombatTracker;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class ThiefClass {
    /**
    Thief Class Abilities:
        -Gets True Invisibility while sneaking and not in combat
        -Temporarily gets speed 1 after being hit
        -Has +0.3 attack speed
        -Has -20% armor
    */

    //To Fix: Particles of other Effects not hiding when Invisible

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
        AttributeInstance attackSpeedAttribute = player.getAttribute(Attributes.ATTACK_SPEED);

        if(armorAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            armorAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(attackSpeedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackSpeedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier armorModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "thief_class_armor_reduction", -0.8, AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeModifier attackSpeedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "thief_class_attack_speed_bonus", 0.3, AttributeModifier.Operation.ADDITION);
        armorAttribute.addTransientModifier(armorModifier);
        attackSpeedAttribute.addTransientModifier(attackSpeedModifier);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        event.player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            if (classes.isClass(ClassManager.ThiefClassID)) {
                if (event.player.isShiftKeyDown() && !CombatTracker.isInCombat(event.player)) {
                    event.player.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), 3, 0, false, false, true));
                }
            }
        });
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player && event.getSource().getEntity() != null) {
            event.getEntity().getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (classes.isClass(ClassManager.ThiefClassID)) {
                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100));
                }
            });
        }
    }
}
