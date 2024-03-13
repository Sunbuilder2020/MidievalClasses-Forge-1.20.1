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

public class ClassSeasonsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<ClassSeasons> CLASS_SEASONS = CapabilityManager.get(new CapabilityToken<ClassSeasons>() {});
    private ClassSeasons classSeasons = null;
    private final LazyOptional<ClassSeasons> optional = LazyOptional.of(this::createClassSeasons);

    private ClassSeasons createClassSeasons() {
        if (this.classSeasons == null) {
            this.classSeasons = new ClassSeasons();
        }
        return this.classSeasons;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == CLASS_SEASONS ? optional.cast() : LazyOptional.empty();
    }



    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createClassSeasons().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createClassSeasons().loadNBTData(nbt);
    }
}
