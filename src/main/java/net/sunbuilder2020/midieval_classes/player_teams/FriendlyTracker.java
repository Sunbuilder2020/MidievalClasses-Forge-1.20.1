package net.sunbuilder2020.midieval_classes.player_teams;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FriendlyTracker {

    private static final Map<Entity, EntityDamageRecord> lastDamageRecords = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player || event.getSource().getEntity() instanceof Player) {
            Entity target = event.getEntity();
            Entity source = event.getSource().getEntity();

            long currentTime = System.currentTimeMillis();
            lastDamageRecords.put(target, new EntityDamageRecord(source, currentTime));
            lastDamageRecords.put(source, new EntityDamageRecord(target, currentTime));
        }
    }

    public static boolean isFriendly(Entity player, Entity entity) {
        EntityDamageRecord record = lastDamageRecords.get(player);
        if (record != null && record.otherEntity.equals(entity)) {
            return (System.currentTimeMillis() - record.lastDamageTime) > 30000;
        }
        return true;
    }

    private static class EntityDamageRecord {
        private final Entity otherEntity;
        private final long lastDamageTime;

        private EntityDamageRecord(Entity otherEntity, long lastDamageTime) {
            this.otherEntity = otherEntity;
            this.lastDamageTime = lastDamageTime;
        }
    }
}

