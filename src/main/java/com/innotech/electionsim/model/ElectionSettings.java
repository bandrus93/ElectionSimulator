package com.innotech.electionsim.model;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ElectionSettings {
    private String locale;
    private RaceType race;
    private ElectionType type;
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

    public RaceType getRace() {
        return race;
    }

    public void setRace(RaceType race) {
        this.race = race;
    }

    public ElectionType getType() {
        return type;
    }

    public void setType(ElectionType type) {
        this.type = type;
    }

    public String getElectionDay() {
        return electionDay;
    }

    public void setElectionDay(String electionDay) {
        this.electionDay = electionDay;
    }

    public void save() {
        try (FileWriter settings = new FileWriter("src/main/resources/election_settings.txt")) {
            Gson gson = new Gson();
            String updatedJson = gson.toJson(this);
            settings.write(updatedJson);
        } catch (IOException e) {
            System.out.println("Save Error: Unable to save settings");
        }
    }
}
