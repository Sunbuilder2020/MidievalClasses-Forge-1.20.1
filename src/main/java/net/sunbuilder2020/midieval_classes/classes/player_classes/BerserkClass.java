package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class BerserkClass {
    /**
    Berserk Class Abilities:
        -Has +20% Speed
        -Takes +10% Damage
        -Deals +15% Damage with Axes
        -The Player heals for 8% of the damage dealt
    */

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if(speedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            speedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier speedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "berserk_class_speed_boost", 0.20, AttributeModifier.Operation.MULTIPLY_TOTAL);
        speedAttribute.addTransientModifier(speedModifier);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player hunter && event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            hunter.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (classes.isClass(ClassManager.BerserkClassID)) {
                    hunter.heal(event.getAmount() * 0.08F);

                    ItemStack stack = hunter.getMainHandItem();

                    if (stack.is(Tags.Items.TOOLS) && stack.getDisplayName().contains(Component.literal("Axe"))) {
                        event.setAmount(event.getAmount() * 1.15F);
                    }
                }
            });
        }

        if(event.getEntity() instanceof Player target) {
            target.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if(classes.isClass(ClassManager.BerserkClassID)) {
                    event.setAmount(event.getAmount() * 1.1F);
                }
            });
        }
    }
}
