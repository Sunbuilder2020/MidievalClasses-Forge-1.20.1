package net.sunbuilder2020.midieval_classes.client;

public class ClientClassData {
    public static String playerClass;

    public static void set(String classes) {
        ClientClassData.playerClass = classes;
    }

    public static String getPlayerClass() {
        return playerClass;
    }
}
