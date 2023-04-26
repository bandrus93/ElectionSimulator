package com.innotech.electionsim.election;

import com.innotech.electionsim.interfaces.Editable;
import com.innotech.electionsim.view.UserInterface;

import java.util.Calendar;
import java.util.List;

public class ElectionSettings implements Editable {
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

    public String getLocale() {
        return locale;
    }

    public RaceType getRace() {
        return race;
    }

    public ElectionType getType() {
        return type;
    }

    public String getElectionDay() {
        return electionDay;
    }

    @Override
    public void edit() {
        boolean updating = true;
        do {
            switch (UserInterface.getStringInput(this + UserInterface.SETTINGS_PROMPT)) {
                case "1" ->
                        locale = UserInterface.getStringInput(UserInterface.LOCALE_PROMPT);
                case "2" ->
                        race = (ElectionSettings.RaceType)(UserInterface.getMenuSelection(UserInterface.RACE_PROMPT, ElectionSettings.RaceType.values(), List.of("")));
                case "3" ->
                        type = (ElectionSettings.ElectionType)(UserInterface.getMenuSelection(UserInterface.ELECTION_PROMPT, ElectionSettings.ElectionType.values(), List.of("")));
                case "4" ->
                        electionDay = UserInterface.getStringInput(UserInterface.DATE_PROMPT);
                case "s" -> updating = false;
                default ->
                        System.out.println(UserInterface.INVALID_COMMAND);
            }
        } while (updating);
    }

    @Override
    public String toString() {
        String electionDay = "NULL".equals(this.electionDay)
                ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                + "-"
                + (Calendar.getInstance().get(Calendar.MONTH) + 1)
                + "-"
                + Calendar.getInstance().get(Calendar.YEAR)
                : this.electionDay;
        return "\nCurrent Campaign:\n1 - Locale: " +
                this.locale +
                "\n2 - Race Type: " +
                this.race +
                "\n3 - Election Type: " +
                this.type +
                "\n4 - Election Day: " +
                electionDay +
                "\n";
    }
}
