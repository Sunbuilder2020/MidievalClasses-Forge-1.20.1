package net.sunbuilder2020.medieval_classes.classes.player_classes;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sunbuilder2020.medieval_classes.MedievalClasses;
import net.sunbuilder2020.medieval_classes.classes.ClassManager;
import net.sunbuilder2020.medieval_classes.classes.PlayerClassesProvider;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.*;

@Mod.EventBusSubscriber(modid = MedievalClasses.MOD_ID)
public class DwarfClass {
    /**
    Dwarf Class Abilities:
        -is 1 Block tall
        -mines blocks 30% faster
        -has a 5% chance to get ores when mining stone-like blocks
     */

    public static void applyClassChanges(Player player) {
        setPlayerAttributes(player);
        setPlayerSize(player, 0.55f);
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speedAttribute.getModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID) != null) {
            speedAttribute.removeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID);
        }

        AttributeModifier modifier = new AttributeModifier(ClassManager.CLASS_ATTRIBUTE_MODIFIER_ID, "dwarf_class_speed_reduction", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        speedAttribute.addTransientModifier(modifier);
    }

    public static void setPlayerSize(Entity entity, float scale) {
        ScaleData heightScaleData = ScaleTypes.HEIGHT.getScaleData(entity);
        ScaleData widthScaleData = ScaleTypes.WIDTH.getScaleData(entity);
        ScaleData reachScaleData = ScaleTypes.REACH.getScaleData(entity);
        ScaleData miningSpeedScaleData = ScaleTypes.MINING_SPEED.getScaleData(entity);
        ScaleData dropsScaleData = ScaleTypes.DROPS.getScaleData(entity);
        ScaleData heldItemScaleData = ScaleTypes.HELD_ITEM.getScaleData(entity);
        ScaleData projectilesScaleData = ScaleTypes.PROJECTILES.getScaleData(entity);
        ScaleData thirdPersonScaleData = ScaleTypes.THIRD_PERSON.getScaleData(entity);
        ScaleData visibilityScaleData = ScaleTypes.VISIBILITY.getScaleData(entity);

        heightScaleData.setScale(scale);
        widthScaleData.setScale(scale);
        reachScaleData.setScale(0.8F);
        miningSpeedScaleData.setScale(1.3F);
        dropsScaleData.setScale(scale);
        heldItemScaleData.setScale(scale);
        projectilesScaleData.setScale(scale);
        thirdPersonScaleData.setScale(scale);
        visibilityScaleData.setScale(scale);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level world = (Level) event.getLevel();
        BlockState state = event.getState();

        player.getCapability(PlayerClassesProvider.PLAYER_CLASSES).ifPresent(classes -> {
            if (!player.isCreative() && state.is(BlockTags.BASE_STONE_OVERWORLD) && classes.isClass(ClassManager.DwarfClassID)) {
                tryDropOre(world, event.getPos(), player);
            }
        });
    }

    private static void tryDropOre(Level world, BlockPos pos, Player player) {
        // Define the chance of dropping an ore
        double chance = 0.05D; // 1% chance, for example

        if (!world.isClientSide && player.getRandom().nextDouble() <= chance) { // Ensure this only runs on the server and checks the chance
            Item oreToDrop = getAnyRawOreItem();

            if (oreToDrop != null) {
                // Correctly create and spawn the item entity in the world
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(oreToDrop, 1));
                world.addFreshEntity(itemEntity);
            }
        }
    }

    private static Item getAnyRawOreItem() {
        List<Item> ores = new ArrayList<>();

        for (Item item : ForgeRegistries.ITEMS) {
            ItemStack stack = new ItemStack(item);
            if (stack.is(Tags.Items.RAW_MATERIALS) || stack.is(Tags.Items.GEMS) && !stack.is(Tags.Items.GEMS_PRISMARINE)) {
                ores.add(item);
            }
        }

        if (!ores.isEmpty()) {
            return ores.get(new Random().nextInt(ores.size()));
        }
        return null;
    }
}
