package com.innotech.electionsim.data;

import com.google.gson.Gson;
import com.innotech.electionsim.view.DisplayManager;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ElectionSettings {
    private String locale;
    private String race;
    private String type;
    private String electionDay;

    public enum RaceType {
        PRESIDENTIAL,
        GUBERNATORIAL,
        LOCAL,
        MUNICIPAL,
        MAYORAL,
        PROVINCIAL,
        NATIONAL,
        SENATORIAL,
        CONGRESSIONAL,
        PARLIAMENTARY,
        GENERAL
    }

    public enum ElectionType {
        PLURALITY,
        INSTANT_RUNOFF,
        APPROVAL
    }

    public static ElectionSettings load() throws IOException {
        Scanner settingsData = new Scanner(Paths.get("src/main/resources/election_settings.txt"));
        Gson gson = new Gson();
        return gson.fromJson(settingsData.nextLine(), ElectionSettings.class);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getElectionDay() {
        return electionDay;
    }

    public void setElectionDay(String electionDay) {
        this.electionDay = electionDay;
    }

    private String getRaceMenu() {
        StringBuilder menuBuilder = new StringBuilder();
        int counter = 1;
        for (RaceType val : RaceType.values()) {
            menuBuilder.append(counter).append(" - ").append(val.toString()).append("\n");
            counter++;
        }
        return menuBuilder.toString();
    }

    private String getElectionMenu() {
        StringBuilder menuBuilder = new StringBuilder();
        int counter = 1;
        for (ElectionType val : ElectionType.values()) {
            menuBuilder.append(counter).append(" - ").append(val.toString()).append("\n");
            counter++;
        }
        return menuBuilder.toString();
    }

    public String findRaceTypeByValue(String val) {
        int locator = Integer.parseInt(val) - 1;
        RaceType[] arr = RaceType.values();
        return locator >= 0 && locator < arr.length ? arr[locator].toString() : "GENERAL";
    }

    public String findElectionTypeByValue(String val) {
        int locator = Integer.parseInt(val) - 1;
        ElectionType[] arr = ElectionType.values();
        return locator >= 0 && locator < arr.length ? arr[locator].toString() : "PLURALITY";
    }

    public void update(Scanner uiController) {
        System.out.println(DisplayManager.getCurrentSettingsMenu() + DisplayManager.SETTINGS_PROMPT);
        boolean updating = true;
        do {
            switch (uiController.nextLine()) {
                case "1" -> {
                    System.out.println(DisplayManager.LOCALE_PROMPT);
                    setLocale(uiController.nextLine());
                }
                case "2" -> {
                    System.out.println(getRaceMenu() + DisplayManager.RACE_PROMPT);
                    setRace(findRaceTypeByValue(uiController.nextLine()));
                }
                case "3" -> {
                    System.out.println(getElectionMenu() + DisplayManager.ELECTION_PROMPT);
                    setType(findElectionTypeByValue(uiController.nextLine()));
                }
                case "4" -> {
                    System.out.println(DisplayManager.DATE_PROMPT);
                    setElectionDay(uiController.nextLine());
                }
                case "s" -> {
                    save();
                    updating = false;
                }
                default -> {
                    System.out.println(DisplayManager.INVALID_COMMAND);
                }
            }
            if (updating) {
                System.out.println(DisplayManager.getCurrentSettingsMenu() + DisplayManager.SETTINGS_PROMPT);
            }
        } while (updating);
    }

    private void save() {
        try (FileWriter settings = new FileWriter("src/main/resources/election_settings.txt")) {
            Gson gson = new Gson();
            String updatedJson = gson.toJson(this);
            settings.write(updatedJson);
        } catch (IOException e) {
            System.out.println("Save Error: Unable to save settings");
        }
    }
}
