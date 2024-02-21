package net.sunbuilder2020.midieval_classes.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sunbuilder2020.midieval_classes.MidievalClasses;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MidievalClasses.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MIDIEVAL_CLASSES_TAB = CREATIVE_MODE_TABS.register("midieval_classes_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.OAK_LOG))
                    .title(Component.translatable("creativetab.midieval_classes_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        //pOutput.accept();
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
