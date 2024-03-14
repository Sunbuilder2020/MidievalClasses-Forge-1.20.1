package net.sunbuilder2020.medieval_classes.classes;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassSeasons {
    private int currentSeason = 0;
    private List<String> availableClasses = new ArrayList<>();

    public int getCurrentSeason() {
        return this.currentSeason;
    }

    public List<String> getAvailableClasses() {
        return this.availableClasses;
    }

    public boolean isAvailableClass(String playerClass) {
        return this.availableClasses.contains(playerClass);
    }

    public void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public void setAvailableClasses(List<String> availableClasses) {
        this.availableClasses = availableClasses;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("current_season", this.currentSeason);

        String classesAsString = String.join(";", this.availableClasses);
        nbt.putString("season_available_classes", classesAsString);
    }

    public void loadNBTData(CompoundTag nbt) {
        this.currentSeason = nbt.getInt("current_season");

        String classesAsString = nbt.getString("season_available_classes");
        this.availableClasses = new ArrayList<>(Arrays.asList(classesAsString.split(";")));
    }
}
