package net.sunbuilder2020.midieval_classes.player_teams;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class EntityRelationshipTracker {
    private static final Map<EntityPair, Long> interactionTimes = new HashMap<>();

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity hitter = event.getSource().getEntity();
        LivingEntity hit = event.getEntity();

        if(hitter instanceof LivingEntity) {
            recordInteraction(hitter, hit);
        }
    }

    public static void recordInteraction(Entity hitter, Entity hit) {
        EntityPair pair = new EntityPair(hitter, hit);
        interactionTimes.put(pair, System.currentTimeMillis());
    }

    public static boolean isFriendly(Entity entityOne, Entity entityTwo) {
        long currentTime = System.currentTimeMillis();
        EntityPair pair = new EntityPair(entityOne, entityTwo);
        Long lastInteractionTime = interactionTimes.get(pair);

        if (lastInteractionTime == null) {
            return true;
        }

        return currentTime - lastInteractionTime > 20000;
    }
}

