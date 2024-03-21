package net.sunbuilder2020.medieval_classes.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sunbuilder2020.medieval_classes.MedievalClasses;
import net.sunbuilder2020.medieval_classes.items.custom.KingsCrown;
import net.sunbuilder2020.medieval_classes.items.custom.ReRollBook;

public class ModItems {
    public static final DeferredRegister<Item> Items =
            DeferredRegister.create(ForgeRegistries.ITEMS, MedievalClasses.MOD_ID);

    public static final RegistryObject<Item> KINGS_CROWN = Items.register("kings_crown",
            () -> new KingsCrown(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public static final RegistryObject<Item> RE_ROLL_BOOK = Items.register("re_roll_book",
            () -> new ReRollBook(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public static void register(IEventBus eventBus) {
        Items.register(eventBus);
    }
}
