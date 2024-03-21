package net.sunbuilder2020.medieval_classes.items.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import net.sunbuilder2020.medieval_classes.classes.PlayerClassesProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KingsCrown extends Item {
    public KingsCrown(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pPlayer.level().isClientSide) {
            pPlayer.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(playerClasses -> {
                ClassManager.setClass((ServerPlayer) pPlayer, playerClasses.getClasses(), true, playerClasses.getOriginalClass(), playerClasses.getRemainingForcedClassTicks());

                ClassManager.sendKingCrownedMessage(pPlayer, 0, null);

                pPlayer.getItemInHand(pUsedHand).setCount(pPlayer.getItemInHand(pUsedHand).getCount() - 1);
            });
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Right-Click to become the new King of the Server."));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
