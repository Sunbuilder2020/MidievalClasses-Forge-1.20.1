package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;

import java.util.List;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class ArcherClass {
    /**
    Archer Class Abilities:
        -Has +30% Arrow Damage
        -Has +15% Speed
        -Inflicts Glowing for 15 seconds after hitting an entity with an arrow
    */

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if(speedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            speedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier speedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "archer_class_speed_boost", 0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
        speedAttribute.addTransientModifier(speedModifier);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player && event.getSource().is(DamageTypes.ARROW)) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (classes.isClass(ClassManager.ArcherClassID)) {
                    LivingEntity entity = event.getEntity();

                    event.setAmount(event.getAmount() * 1.30F);
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300));
                }
            });
        }
    }
}
