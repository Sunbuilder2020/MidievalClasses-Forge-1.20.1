package net.sunbuilder2020.midieval_classes.classes.player_classes;

import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;
import net.sunbuilder2020.midieval_classes.player_teams.EntityRelationshipTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class PaladinClass {
    /*
    Paladin Class Abilities:
        -deals +2 Damage
        -takes -5% Damage
        -reduces Damage dealt to all friendly players in a 10 Block radius
        -makes each of his armor pieces have a 50% change to take 1 Durability damage more when hit
    */

    //Not Working: Attribute

    private static final Map<UUID, TargetedAreaEntity> entityTrackingMap = new HashMap<>();

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (attackDamageAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            attackDamageAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier modifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "paladin_class_damage_bonus", 2, AttributeModifier.Operation.ADDITION);
        attackDamageAttribute.addTransientModifier(modifier);
    }

    @SubscribeEvent
    public static void LivingHurtEvent(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        Entity hunter = event.getSource().getEntity();

        if(target instanceof Player) {
            target.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if(classes.isClass(classes.PaladinClassID)) {
                    event.setAmount((float) (event.getAmount() * 0.95));
                }
            });

            for (ItemStack itemStack : target.getArmorSlots()) {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof ArmorItem) {
                    Float random = target.getRandom().nextFloat();
                    if(random > 0.5) {
                        itemStack.hurtAndBreak(1, target, (p) -> p.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, ((Player) target).getInventory().armor.indexOf(itemStack))));
                    }
                }
            }
        }

        if(hunter instanceof LivingEntity && target != null) {
            Level world = target.level();
            int radius = 10;

            AABB boundingBox = new AABB(
                    target.getX() - radius, target.getY() - radius, target.getZ() - radius,
                    target.getX() + radius, target.getY() + radius, target.getZ() + radius
            );

            List<Player> playersWithinBox = world.getEntitiesOfClass(Player.class, boundingBox);

            for (Player player : playersWithinBox) {
                if (player.distanceToSqr(target.getX(), target.getY(), target.getZ()) <= radius * radius) {
                    player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(playerClasses -> {
                        if(playerClasses.isClass(playerClasses.PaladinClassID)) {
                            if (EntityRelationshipTracker.isFriendly(player, target) && !player.equals(target)) {
                                event.setAmount((float) (event.getAmount() * 0.5));
                            }
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (player.level().isClientSide) {
            return;
        }

        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            if (classes.isClass(classes.PaladinClassID)) {
                handleTargetedAreaEntityForPaladin(player);
            }
        });
    }

    private static void handleTargetedAreaEntityForPaladin(Player player) {
        Level level = player.level();
        UUID playerUUID = player.getUUID();

        TargetedAreaEntity existingEntity = getExistingEntityForPlayer(playerUUID);
        if (existingEntity != null) {
            existingEntity.discard();
        }

        Vec3 center = player.position();
        float radius = 10.0F;
        int color = 16239960;
        TargetedAreaEntity newEntity = TargetedAreaEntity.createTargetAreaEntity(level, center, radius, color, player);

        updateEntityTrackingForPlayer(playerUUID, newEntity);
    }

    private static TargetedAreaEntity getExistingEntityForPlayer(UUID playerUUID) {
        return entityTrackingMap.get(playerUUID);
    }

    private static void updateEntityTrackingForPlayer(UUID playerUUID, TargetedAreaEntity newEntity) {
        if (newEntity == null) {
            entityTrackingMap.remove(playerUUID);
        } else {
            entityTrackingMap.put(playerUUID, newEntity);
        }
    }
}
