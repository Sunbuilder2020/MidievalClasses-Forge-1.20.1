package net.sunbuilder2020.midieval_classes.ModpackUtils;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sunbuilder2020.midieval_classes.MidievalClasses;
import net.sunbuilder2020.midieval_classes.classes.ClassManager;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MidievalClasses.MOD_ID)
public class NerfIronsSpells {
    public static final UUID IRONS_SPELLS_ATTRIBUTE_ID = UUID.fromString("2a2b5c4d-5e6f-7a8b-3c0d-1e2f3a4b5c6d");

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            setPlayerAttributes(player);
        }
    }

    public static void setPlayerAttributes(Player player) {
        AttributeInstance spellPowerAttribute = player.getAttribute(AttributeRegistry.SPELL_POWER.get());

        if (spellPowerAttribute.getModifier(IRONS_SPELLS_ATTRIBUTE_ID) != null) {
            spellPowerAttribute.removeModifier(IRONS_SPELLS_ATTRIBUTE_ID);
        }

        AttributeModifier modifier = new AttributeModifier(IRONS_SPELLS_ATTRIBUTE_ID, "spell_power_attribute_reduction", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
        spellPowerAttribute.addTransientModifier(modifier);
    }
}
