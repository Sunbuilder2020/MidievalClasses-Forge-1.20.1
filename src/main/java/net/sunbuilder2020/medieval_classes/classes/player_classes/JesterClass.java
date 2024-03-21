package net.sunbuilder2020.medieval_classes.classes.player_classes;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.medieval_classes.MedievalClasses;
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import net.sunbuilder2020.medieval_classes.classes.PlayerClassesProvider;

@Mod.EventBusSubscriber(modid = MedievalClasses.MOD_ID)
public class JesterClass {
    /**
    Jester Class Abilities:
        -Dealt damage is multiplied with a random number between 0 and 2
        -Received damage is multiplied with a random number between 0 and 2
        -When you get hit, you have a 5% chance teleport a few blocks away and get invisibility for 5 seconds
        -when you die you have a 20% chance to activate the effects of a totem of undying without having one active
     */

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if(classes.isClass(ClassManager.JesterClassID)) {
                    float dealtMultiplier = player.getRandom().nextFloat() * 2;
                    event.setAmount(event.getAmount() * dealtMultiplier);
                }
            });
        }

        if (event.getEntity() instanceof Player player) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if(classes.isClass(ClassManager.JesterClassID)) {
                    float receivedMultiplier = player.getRandom().nextFloat() * 2.0f;
                    event.setAmount(event.getAmount() * receivedMultiplier);

                    if (player.getRandom().nextFloat() <= 0.05) {
                        player.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), 100));
                        teleportToSafeLocation((ServerPlayer) player);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
                if (player.getRandom().nextFloat() <= 0.10 && classes.isClass(ClassManager.JesterClassID)) {
                    event.setCanceled(true);
                    player.setHealth(1.0F);
                    player.removeAllEffects();
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

                    player.level().playSound(null, new BlockPos(player.getOnPos()), SoundEvents.TOTEM_USE, SoundSource.PLAYERS);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onTotemUse(LivingUseTotemEvent event) {
        event.getEntity().getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            if (event.getEntity().getRandom().nextFloat() <= 0.40 && classes.isClass(ClassManager.JesterClassID)) {
                event.setCanceled(true);
            }
        });
    }

    public static void teleportToSafeLocation(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        BlockPos playerPos = player.getOnPos();

        for (int i = 0; i < 1000; i++) {
            int x = playerPos.getX() + player.getRandom().nextInt(21) - 10;
            int z = playerPos.getZ() + player.getRandom().nextInt(21) - 10;
            int y = playerPos.getY() + player.getRandom().nextInt(21) - 10;

            BlockPos pos = new BlockPos(x, y, z);
            BlockPos posUp = new BlockPos(x, y + 1, z);

            if(level.getBlockState(pos).isSolid() && !level.getBlockState(posUp).isSolid()) {
                player.teleportTo(posUp.getX(), posUp.getY(), posUp.getZ());
            }
        }
    }
}
