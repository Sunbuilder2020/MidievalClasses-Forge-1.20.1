package net.sunbuilder2020.midieval_classes.player_hostility;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class CombatTracker {
    private static final Map<Player, Long> lastCombatTimes = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof Player) {
            Player player = (Player) event.getSource().getEntity();
            CombatTracker.recordCombatInteraction(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player && event.getSource().getEntity() != null) {
            Player player = (Player) event.getEntity();
            CombatTracker.recordCombatInteraction(player);
        }
    }

    public static void recordCombatInteraction(Player player) {
        lastCombatTimes.put(player, System.currentTimeMillis());
    }

    public static boolean isInCombat(Player player) {
        Long lastInteractionTime = lastCombatTimes.get(player);
        if (lastInteractionTime == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        return (currentTime - lastInteractionTime) <= 20000;
    }
}

