package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class PaladinClass {
    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (attackDamageAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackDamageAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier modifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "class_damage_bonus", 2, AttributeModifier.Operation.ADDITION);
        attackDamageAttribute.addTransientModifier(modifier);
    }
}
