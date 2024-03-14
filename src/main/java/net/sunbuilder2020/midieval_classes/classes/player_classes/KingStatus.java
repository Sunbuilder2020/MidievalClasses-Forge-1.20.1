package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class KingStatus {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof Player target && event.getSource().getEntity() instanceof Player hunter) {
            target.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(targetClasses -> {
                hunter.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(hunterClasses -> {
                    if (hunterClasses.getIsKing()) {
                        String originalClass = "";
                        String randomClass = ClassManager.getRandomValidClass((ServerLevel) event.getEntity().level());

                        if(targetClasses.getOriginalClass().isEmpty()) {
                            originalClass = targetClasses.getClasses();
                        } else {
                            originalClass = targetClasses.getOriginalClass();
                        }

                        ClassManager.setClass((ServerPlayer) target, randomClass, targetClasses.getIsKing(), originalClass, 40 * 60 * 20);

                        ClassManager.applyClassChanges(target);

                        ClassManager.sendClassMessages(target, targetClasses.getClasses(), 6);
                    }
                });
            });
        }
    }
}
