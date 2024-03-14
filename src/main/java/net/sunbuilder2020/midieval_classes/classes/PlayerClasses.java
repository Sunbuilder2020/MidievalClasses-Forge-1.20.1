package net.sunbuilder2020.midieval_classes.classes;

import net.minecraft.nbt.CompoundTag;

public class PlayerClasses {
    private String classes = "";
    private boolean isKing = false;
    private int lastSeasonOnline = 0;
    private String originalClass = "";
    private int remainingForcedClassTicks = -1;

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

    public boolean getIsKing() {
        return isKing;
    }

    public void setIsKing(boolean isKing) {
        this.isKing = isKing;
    }

    public String getOriginalClass() {
        return originalClass;
    }

    public void setOriginalClass(String originalClass) {
        this.originalClass = originalClass;
    }

    public int getRemainingForcedClassTicks() {
        return remainingForcedClassTicks;
    }

    public void setRemainingForcedClassTicks(int remainingForcedClassTicks) {
        this.remainingForcedClassTicks = remainingForcedClassTicks;
    }

    public boolean isClass(String classes) {
        return this.classes.equals(classes);
    }

    public boolean isForcedClass() {
        return remainingForcedClassTicks >= 0;
    }

    public boolean isLastSeasonOnline(int lastSeasonOnline) {
        return this.lastSeasonOnline == lastSeasonOnline;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putString("classes", this.classes);
        nbt.putInt("lastSeasonOnline", this.lastSeasonOnline);
        nbt.putBoolean("isKing", this.isKing);
        nbt.putString("originalClass", this.originalClass);
        nbt.putInt("remainingForcedClassTicks", this.remainingForcedClassTicks);
    }

    public void loadNBTData(CompoundTag nbt) {
        this.classes = nbt.getString("classes");
        this.lastSeasonOnline = nbt.getInt("lastSeasonOnline");
        this.isKing = nbt.getBoolean("isKing");
        this.originalClass = nbt.getString("originalClass");
        this.remainingForcedClassTicks = nbt.getInt("remainingForcedClassTicks");
    }
}
