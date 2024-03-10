package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class ArcherClass {
    /**
    Archer Class Abilities:
        -Has +30% Arrow Damage
        -Has +15% Speed
        -Inflicts Glowing for 15 seconds after hitting an entity with an arrow
        -All 5 bow shots he can shoot 3 arrows
    */

    private static final String SHOT_COUNTER_KEY = "archerShotCounter";

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

    @SubscribeEvent
    public static void onArrowShoot(ArrowLooseEvent event) {
        Player player = event.getEntity();
        Level world = player.level();
        if (!world.isClientSide) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (classes.isClass(ClassManager.ArcherClassID) && isFifthShot(player)) {
                    ItemStack bow = event.getBow();
                    float velocity = getArrowVelocity(event.getCharge());

                    // Original arrow is shot automatically by the event, so we only shoot the additional arrows here
                    shootAdditionalArrow(world, player, bow, velocity, 20);
                    shootAdditionalArrow(world, player, bow, velocity, -20);
                }

                resetShotCounter(player);
            });
        }
    }

    private static void shootAdditionalArrow(Level world, Player player, ItemStack bow, float velocity, float angleOffset) {
        Arrow arrow = new Arrow(world, player);
        arrow.shootFromRotation(player, player.getXRot(), player.getYRot() + angleOffset, 0.0F, velocity * 3.0F, 1.0F);
        if (velocity == 1.0F) {
            arrow.setCritArrow(true);
        }
        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow);
        if (powerLevel > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
        }
        int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bow);
        if (punchLevel > 0) {
            arrow.setKnockback(punchLevel);
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bow) > 0) {
            arrow.setSecondsOnFire(100);
        }

        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

        world.addFreshEntity(arrow);
    }

    private static float getArrowVelocity(int charge) {
        float velocity = (float) charge / 20.0F;
        velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
        if (velocity > 1.0F) {
            velocity = 1.0F;
        }
        return velocity;
    }

    private static boolean isFifthShot(Player player) {
        CompoundTag playerData = player.getPersistentData();
        int shots = playerData.getInt(SHOT_COUNTER_KEY);
        return shots >= 4; // Returns true for the fifth shot
    }

    // Example method to reset or update the shot counter
    private static void resetShotCounter(Player player) {
        CompoundTag playerData = player.getPersistentData();
        int shots = playerData.getInt(SHOT_COUNTER_KEY);

        if (shots >= 4) {
            playerData.putInt(SHOT_COUNTER_KEY, 0); // Reset counter after the fifth shot
        } else {
            playerData.putInt(SHOT_COUNTER_KEY, shots + 1); // Increment shot counter
        }
    }
}
