package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.ElectionSettings;
import com.innotech.electionsim.view.SettingsView;

import java.io.IOException;

public class SettingsController {
    private final ElectionSettings settings;

    private SettingsController() throws IOException {
        settings = ElectionSettings.load();
    }

    public static SettingsController getInstance() {
        try {
            return new SettingsController();
        } catch (IOException e) {
            return null;
        }
    }

    public ElectionSettings getSettings() {
        return settings;
    }

    public void update() {
        boolean updating = true;
        do {
            switch (UserInterface.getStringInput(SettingsView.getInstance(settings).getView() + UserInterface.SETTINGS_PROMPT)) {
                case "1" ->
                        settings.setLocale(UserInterface.getStringInput(UserInterface.LOCALE_PROMPT));
                case "2" ->
                        settings.setRace((ElectionSettings.RaceType)(UserInterface.getMenuSelection(UserInterface.RACE_PROMPT, ElectionSettings.RaceType.values())));
                case "3" ->
                        settings.setType((ElectionSettings.ElectionType)(UserInterface.getMenuSelection(UserInterface.ELECTION_PROMPT, ElectionSettings.ElectionType.values())));
                case "4" ->
                        settings.setElectionDay(UserInterface.getStringInput(UserInterface.DATE_PROMPT));
                case "s" -> {
                    settings.save();
                    updating = false;
                }
                default ->
                        System.out.println(UserInterface.INVALID_COMMAND);
            }
        } while (updating);
    }
}
