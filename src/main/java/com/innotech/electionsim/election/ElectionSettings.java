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
    private PopulationBias bias;
    private int voterApathy;
    private int voterSuppression;

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

    public enum PopulationBias {
        LEFT,
        RIGHT
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
    public PopulationBias getBias() { return bias; }

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
                case "5" ->
                        bias = UserInterface.getStringInput(UserInterface.BIAS_PROMPT).equals("l".toUpperCase()) ? PopulationBias.LEFT : PopulationBias.RIGHT;
                case "6" ->
                        voterApathy = UserInterface.getNumericInput(UserInterface.APATHY_PROMPT);
                case "7" ->
                        voterSuppression = UserInterface.getNumericInput(UserInterface.SUPPRESSION_PROMPT);
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
                "\n5 - Population Bias: " +
                this.bias +
                "\n6 - Voter Apathy: " +
                this.voterApathy + "%" +
                "\n7 - Voter Suppression: " +
                this.voterSuppression + "%" +
                "\n";
    }
}
