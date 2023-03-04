package com.innotech.electionsim.view;

import com.innotech.electionsim.controller.UserInterface;
import com.innotech.electionsim.model.ElectionSettings;

import java.util.Calendar;

public class SettingsView {
    private final String view;

    private SettingsView(String toDisplay) {
        view = toDisplay;
    }

    public static SettingsView getInstance(ElectionSettings userSettings) {
        String electionDay = userSettings.getElectionDay().equals("NULL")
                ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    + "-"
                    + (Calendar.getInstance().get(Calendar.MONTH) + 1)
                    + "-"
                    + Calendar.getInstance().get(Calendar.YEAR)
                : userSettings.getElectionDay();
        return new SettingsView("\nCurrent Campaign:" +
                "\n1 - Locale: " + userSettings.getLocale() +
                "\n2 - Race Type: " + userSettings.getRace() +
                "\n3 - Election Type: " + userSettings.getType() +
                "\n4 - Election Day: " + electionDay + "\n");
    }

    public String getView() {
        return view;
    }

    public void display() {
        System.out.println(view + UserInterface.SETTINGS_PROMPT);
    }
}
