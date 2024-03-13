package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.nbt.CompoundTag;

public class PlayerClasses {
    private String classes = "";
    private int lastSeasonOnline = 0;

    public String getClasses() {
        return classes;
    }

    public void setClass(String classes) {
        this.classes = classes;
    }

    public int getLastSeasonOnline() {
        return lastSeasonOnline;
    }

    public void setLastSeasonOnline(int lastSeasonOnline) {
        this.lastSeasonOnline = lastSeasonOnline;
    }

    public boolean isClass(String classes) {
        return this.classes.equals(classes);
    }

    public boolean isLastSeasonOnline(int lastSeasonOnline) {
        return this.lastSeasonOnline == lastSeasonOnline;
    }

    public void copyFrom(PlayerClasses source) {
        this.classes = source.getClasses();
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putString("classes", this.classes);
        nbt.putInt("lastSeasonOnline", this.lastSeasonOnline);
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("classes", 8)) {
            this.classes = nbt.getString("classes");
        } else {
            this.classes = "";
        }

        this.lastSeasonOnline = nbt.getInt("lastSeasonOnline");
    }
}
