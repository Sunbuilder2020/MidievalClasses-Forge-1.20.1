package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class PlayerClassesProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerClasses> PLAYER_CLASSES = CapabilityManager.get(new CapabilityToken<PlayerClasses>() {});

    private PlayerClasses playerClasses = null;
    private final LazyOptional<PlayerClasses> optional = LazyOptional.of(this::createPlayerClasses);

    private PlayerClasses createPlayerClasses() {
        if (this.playerClasses == null) {
            this.playerClasses = new PlayerClasses();
        }
        return this.playerClasses;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == PLAYER_CLASSES ? optional.cast() : LazyOptional.empty();
    }



    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerClasses().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerClasses().loadNBTData(nbt);
    }
}
