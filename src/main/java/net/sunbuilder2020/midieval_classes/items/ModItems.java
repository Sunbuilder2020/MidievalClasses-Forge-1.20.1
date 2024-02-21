package net.sunbuilder2020.midieval_classes.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.sunbuilder2020.midieval_classes.MidievalClasses;

public class ModItems {
    public static final DeferredRegister<Item> Items =
            DeferredRegister.create(ForgeRegistries.ITEMS, MidievalClasses.MOD_ID);

    public static void register(IEventBus eventBus) {
        Items.register(eventBus);
    }
}
