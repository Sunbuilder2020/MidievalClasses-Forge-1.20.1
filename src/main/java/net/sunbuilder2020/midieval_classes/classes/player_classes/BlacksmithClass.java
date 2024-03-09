package net.sunbuilder2020.midieval_classes.classes.player_classes;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;
import net.sunbuilder2020.midieval_classes.networking.ModMessages;
import net.sunbuilder2020.midieval_classes.networking.packet.ClassDataSyncS2CPacket;
import net.sunbuilder2020.midieval_classes.networking.packet.SetClassC2SPacket;
import net.sunbuilder2020.midieval_classes.networking.packet.SpawnFireExplosionS2CPacket;
import net.sunbuilder2020.midieval_classes.player_hostility.CombatTracker;
import net.sunbuilder2020.midieval_classes.player_hostility.EntityRelationshipTracker;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class BlacksmithClass {
    /**
    Blacksmith Class Abilities:
        -May light the target and every nearby entity on fire and deal +4 Damage when hitting
        -Has +5 armor toughness
        -Has +6 Health
        -Has -15% % Speed
    */

    //To Fix: Particles not spawning

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if(armorToughnessAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            armorToughnessAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(healthAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            healthAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if(speedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            speedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier armorToughnessModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "blacksmith_class_armor_toughness_bonus", 5.0, AttributeModifier.Operation.ADDITION);
        AttributeModifier healthModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "blacksmith_class_health_bonus", 6.0, AttributeModifier.Operation.ADDITION);
        AttributeModifier speedModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "blacksmith_class_speed_reduction", -0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        armorToughnessAttribute.addTransientModifier(armorToughnessModifier);
        healthAttribute.addTransientModifier(healthModifier);
        speedAttribute.addTransientModifier(speedModifier);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player && event.getEntity().getRandom().nextDouble() <= 0.50D && event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (classes.isClass(ClassManager.BlacksmithClassID)) {
                    Entity entity = event.getEntity();

                    event.getEntity().setRemainingFireTicks(100);
                    event.setAmount(event.getAmount() + 4);

                    AABB searchArea = new AABB(
                            entity.getX() - 3.0, entity.getY() - 3.0, entity.getZ() - 3.0,
                            entity.getX() + 3.0, entity.getY() + 3.0, entity.getZ() + 3.0);

                    List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, searchArea);
                    for (LivingEntity target : nearbyEntities) {
                        if (target != player) {
                            target.setRemainingFireTicks(100);
                        }
                    }
                }
            });
        }
    }
}
