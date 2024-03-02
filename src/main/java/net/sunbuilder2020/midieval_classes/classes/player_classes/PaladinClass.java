package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;

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

    @SubscribeEvent
    public static void LivingHurtEvent(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if(entity instanceof Player) {
            entity.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if(classes.isClass(classes.PaladinClassID)) {
                    event.setAmount((float) (event.getAmount() * 0.95));
                }
            });

            for (ItemStack itemStack : entity.getArmorSlots()) {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof ArmorItem) {
                    Float random = entity.getRandom().nextFloat();
                    if(random > 0.5) {
                        itemStack.hurtAndBreak(1, entity, (p) -> p.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, ((Player) entity).getInventory().armor.indexOf(itemStack))));
                    }
                }
            }
        }
    }
}
