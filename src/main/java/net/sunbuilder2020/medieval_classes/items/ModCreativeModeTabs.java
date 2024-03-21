package net.sunbuilder2020.medieval_classes.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sunbuilder2020.medieval_classes.MedievalClasses;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MedievalClasses.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MEDIEVAL_CLASSES_TAB = CREATIVE_MODE_TABS.register("medieval_classes_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.KINGS_CROWN.get()))
                    .title(Component.translatable("creative_tab.medieval_classes_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.KINGS_CROWN.get());
                        pOutput.accept(ModItems.RE_ROLL_BOOK.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}