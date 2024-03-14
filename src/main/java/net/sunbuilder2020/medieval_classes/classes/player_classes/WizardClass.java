package net.sunbuilder2020.medieval_classes.classes.player_classes;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sunbuilder2020.medieval_classes.MedievalClasses;
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import net.sunbuilder2020.medieval_classes.classes.PlayerClassesProvider;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MedievalClasses.MOD_ID)
public class WizardClass {
    /**
    Wizard Class Abilities:
        -has +15% spell Power
        -has -20% armor
        -when hitting someone with a melee attack you have a 30% chance to apply a negative effect for 5 seconds to the target
    */

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
        AttributeInstance spellPowerAttribute = player.getAttribute(AttributeRegistry.SPELL_POWER.get());

        if(armorAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            armorAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(spellPowerAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            spellPowerAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier armorModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "wizard_class_armor_reduction", -0.20, AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeModifier spellPowerModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "wizard_class_spell_power_bonus", 0.15, AttributeModifier.Operation.MULTIPLY_BASE);
        armorAttribute.addTransientModifier(armorModifier);
        spellPowerAttribute.addTransientModifier(spellPowerModifier);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player && event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (classes.isClass(ClassManager.WizardClassID) && player.getRandom().nextFloat() <= 0.30) {
                    LivingEntity entity = event.getEntity();

                    addRandomEffect(entity);
                }
            });
        }
    }

    public static void addRandomEffect(LivingEntity entity) {
        List<MobEffect> effects = negativeEffects();

        entity.sendSystemMessage(Component.literal(effects.toString()));

        MobEffect effect = effects.get(entity.getRandom().nextInt(effects.size()));
        if(effect != null) {
            entity.addEffect(new MobEffectInstance(effect, 100));
        }
    }

    public static List<MobEffect> negativeEffects() {
        List<MobEffect> effects = new ArrayList<>();

        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            if(!effect.isBeneficial() && !effect.isInstantenous()) {
                effects.add(effect);
            }
        }

        return effects;
    }
}
