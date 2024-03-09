package net.sunbuilder2020.midieval_classes.classes.player_classes;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;
import net.sunbuilder2020.midieval_classes.classes.PlayerClassesProvider;

import java.util.List;

import static net.minecraft.world.damagesource.DamageTypes.INDIRECT_MAGIC;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class ExecutionerClass {
    /**
    Executioner Class Abilities:
     -hitting someone reduces their hunger by 2
     -when you hit someone you have a 10% chance to inflict Darkness and Wither 2 for 5 seconds
     -You deal more damage the more players are around you, 20% if all online Players are around you in a 30 Block radius
    */

    //To Test: More damage the more players are around him, hunger reduction

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player && event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            Player player = (Player) event.getSource().getEntity();
            LivingEntity entity = event.getEntity();

            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (classes.isClass(ClassManager.ExecutionerClassID)) {
                    if(entity instanceof Player) {
                        Player targetPlayer = (Player) entity;

                        FoodData foodData = targetPlayer.getFoodData();

                        int currentFoodLevel = foodData.getFoodLevel();
                        int newFoodLevel = Math.max(currentFoodLevel - 2, 0);

                        foodData.setFoodLevel(newFoodLevel);
                    }

                    if(entity.getRandom().nextFloat() <= 0.30) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100));
                        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
                    }

                    event.setAmount(event.getAmount() * (calculateNearbyPlayerPercentage((ServerPlayer) player, 30) * 20 + 1));
                }
            });
        }
    }

    public static float calculateNearbyPlayerPercentage(ServerPlayer targetPlayer, double radius) {
        MinecraftServer server = targetPlayer.getServer();
        if (server == null) return 0.0F;

        List<ServerPlayer> allPlayers = server.getPlayerList().getPlayers();

        int totalPlayers = Math.max(allPlayers.size() - 1, 1);

        Vec3 targetPos = targetPlayer.position();

        long nearbyPlayersCount = allPlayers.stream()
                .filter(player -> player != targetPlayer)
                .filter(player -> player.position().distanceToSqr(targetPos) <= radius * radius)
                .count();

        float percentage = (float) nearbyPlayersCount / totalPlayers * 100;

        return percentage;
    }
}
