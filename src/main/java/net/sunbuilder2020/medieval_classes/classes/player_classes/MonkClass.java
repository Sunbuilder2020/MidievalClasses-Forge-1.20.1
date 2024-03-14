package net.sunbuilder2020.medieval_classes.classes.player_classes;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.medieval_classes.MedievalClasses;
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import net.sunbuilder2020.medieval_classes.classes.PlayerClassesProvider;
import net.sunbuilder2020.medieval_classes.player_hostility.EntityRelationshipTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MedievalClasses.MOD_ID)
public class MonkClass {
    /**
    Monk Class Abilities:
        -has +40% Holy Spell Power
        -has -4 Armor Toughness
        -heals all friendly players in a 10 Block radius by 1 HP every second
    */

    private static final Map<UUID, TargetedAreaEntity> entityTrackingMap = new HashMap<>();
    private static int currentTick = 0;

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance holySpellPowerAttribute = player.getAttribute(AttributeRegistry.HOLY_SPELL_POWER.get());

        if (armorToughnessAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            armorToughnessAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }
        if (holySpellPowerAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            holySpellPowerAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier armorToughnessModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "monk_class_armor_toughness_reduction", -4, AttributeModifier.Operation.ADDITION);
        AttributeModifier holySpellPowerModifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "monk_class_holy_spell_power_boost", 0.40, AttributeModifier.Operation.MULTIPLY_BASE);
        armorToughnessAttribute.addTransientModifier(armorToughnessModifier);
        holySpellPowerAttribute.addTransientModifier(holySpellPowerModifier);
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
            if (classes.isClass(ClassManager.MonkClassID)) {
                int radius = 10;

                AABB area = new AABB(player.getOnPos()).inflate(radius);

                // Get all entities within the specified AABB area. You can replace Entity.class with a more specific class if you're looking for specific types of entities.
                List<Entity> entities = player.level().getEntitiesOfClass(Entity.class, area, entity -> true);

                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity && EntityRelationshipTracker.isFriendly(entity, player) && currentTick % 20 == 0) {
                        ((LivingEntity) entity).heal(1.0F);

                        handleTargetedAreaEntityForMonk(player);
                    }
                }

                handleTargetedAreaEntityForMonk(player);
            }

            currentTick++;
        });
    }

    private static void handleTargetedAreaEntityForMonk(Player player) {
        Level level = player.level();
        UUID playerUUID = player.getUUID();

        TargetedAreaEntity existingEntity = getExistingEntityForPlayer(playerUUID);
        if (existingEntity != null) {
            existingEntity.discard();
        }

        Vec3 center = player.position();
        float radius = 10.0F;
        int color = 65407;
        TargetedAreaEntity newEntity = TargetedAreaEntity.createTargetAreaEntity(level, center, radius, color, player);
        newEntity.setDuration(2);
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
