package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.nbt.CompoundTag;

public class PlayerClasses {
    private String classes = "";

    public String getClasses() {
        return classes;
    }

    public void setClass(String classes) {
        this.classes = classes;
    }

    public boolean isClass(String classes) {
        return this.classes.equals(classes);
    }

    public void copyFrom(PlayerClasses source) {
        this.classes = source.getClasses();
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putString("classes", this.classes);
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("classes", 8)) {
            this.classes = nbt.getString("classes");
        } else {
            this.classes = "";
        }
    }
}
