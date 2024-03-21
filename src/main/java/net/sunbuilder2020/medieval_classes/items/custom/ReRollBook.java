package net.sunbuilder2020.medieval_classes.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

public class ReRollBook extends Item {
    public ReRollBook(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(playerClasses -> {
                String currentClass = playerClasses.getClasses();
                String newClass = ClassManager.getRandomValidClass((ServerLevel) level);

                int attempts = 0;
                while (newClass.equals(currentClass) && attempts < 100) {
                    newClass = ClassManager.getRandomValidClass((ServerLevel) level);
                    attempts++;
                }

                if (!newClass.equals(currentClass)) {
                    ClassManager.setClass((ServerPlayer) player, newClass, playerClasses.getIsKing(), "", -1);
                    ClassManager.sendClassMessages(player, newClass, 7);
                    player.getItemInHand(usedHand).shrink(1);
                } else {
                    player.sendSystemMessage(Component.literal("Couldn't assign a new Class. Please try again.").withStyle(ChatFormatting.RED));
                }
            });
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Right-Click to gain the knowledge of another random Class."));
        pTooltipComponents.add(Component.literal("But you loose your current Class."));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
