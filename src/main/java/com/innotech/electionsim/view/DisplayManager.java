package com.innotech.electionsim.view;

import com.innotech.electionsim.data.ElectionSettings;
import com.innotech.electionsim.model.Candidate;

import java.util.Calendar;
import java.util.List;
import java.util.PriorityQueue;

public class DisplayManager {
    private static ElectionSettings userSettings;
    public static final String MAIN_MENU = """

            1 - Start New Election
            2 - Review Past Elections
            3 - Settings""";
    public static final String INVALID_NUMBER_INPUT = "Please enter a valid number";
    public static final String INVALID_COMMAND = "Invalid command";
    public static final String POPULATION_PROMPT = "Enter the total number of registered voters:";
    public static final String SETTINGS_PROMPT = "To change a setting, enter the number corresponding to the setting you wish to update. When finished, type 's' to save your changes and return to the main menu.";
    public static final String POPULATION_COMMAND_LIST = """
            You may adjust population metrics by entering the following commands:
            To slightly shift alignment left or right, type 'l' or 'r', respectively;
            To majorly shift alignment left or right, type 'L' or 'R', respectively;
            To slightly increase or decrease polarity, type 'p+' or 'p-' respectively;
            To majorly increase or decrease polarity, type 'P+' or 'P-', respectively;
            To slightly divide the population, type 'd'; or to majorly divide the population type 'D'.
            When finished, enter 'f'.""";
    public static final String CAMPAIGN_COMMAND_LIST = """
            To add a new candidate, enter '+';
            To remove a candidate, enter '-' and the number corresponding to the candidate you wish to remove, separated by a space(' ');
            To edit a candidate, enter 'e' and the number corresponding to the candidate you wish to edit, separated by a space(' ').
            When you are finished making changes to the ballot roster, you may run the election by entering 'r'.
            """;
    public static final String CANDIDATE_EDIT_PROMPT = "To update candidate's stats, type the letter in brackets [] and the number you wish to change the stat to (1 - 9) separated by a space ' ', and press 'Enter'" +
            "When you are finished editing, enter 'f';";
    public static final String CANDIDATE_NAME_PROMPT = "Enter candidate name:";
    public static final String CANDIDTAE_NAME_LABEL = "[N]ame: ";
    public static final String CANDIDATE_ALIGNMENT_PROMPT = "Enter the number corresponding to the candidate's party alignment:";
    public static final String CANDIDATE_ALIGNMENT_LABEL = "[A]lignment: ";
    public static final String CANDIDATE_ENERGY_PROMPT = "Rate the candidate's energy level (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_ENERGY_LABEL = "[E]nergy          | ";
    public static final String CANDIDATE_INTELLIGENCE_PROMPT = "Rate the candidate's general level of intelligence (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_INTELLIGENCE_LABEL = "[I]ntelligence    | ";
    public static final String CANDIDATE_WIT_PROMPT = "Rate the candidate's wit (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_WIT_LABEL = "[W]it             | ";
    public static final String CANDIDATE_LEVEL_HEAD_PROMPT = "Rate the candidate's level-headedness (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_LEVEL_HEAD_LABEL = "[L]evel-headedness| ";
    public static final String CANDIDATE_SPEAK_ABILITY_PROMPT = "Rate the candidate's public speaking ability (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_SPEAK_ABILITY_LABEL = "[S]peaking ability| ";
    public static final String CANDIDATE_LIST_HEADING = "Active Candidates:\n";
    public static final String LOCALE_PROMPT = "Enter election locale:";
    public static final String RACE_PROMPT = "Enter the number corresponding to the race type:";
    public static final String ELECTION_PROMPT = "Enter the number corresponding to the election type:";
    public static final String DATE_PROMPT = "Enter the date the election is to be held (in DD-MM-YYYY format):";

    public static void setUserSettings(ElectionSettings settings) {
        userSettings = settings;
    }

    public static String getGreetingMessage() {
        return "Welcome to " + userSettings.getLocale() + "! What would you like to do?" +
                "\n(Type a number and press 'Enter' to make a selection, or type 'q' to quit):";
    }

    public static String getCurrentSettingsMenu() {
        String electionDay = userSettings.getElectionDay().equals("NULL") ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.YEAR) : userSettings.getElectionDay();
        return "\n1 - Locale: " + userSettings.getLocale() +
                "\n2 - Race Type: " + userSettings.getRace() +
                "\n3 - Election Type: " + userSettings.getType() +
                "\n4 - Election Day: " + electionDay + "\n";
    }

    public static void refresh(String reprint) {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        System.out.println(reprint);
    }

    public static void printElectionResult(PriorityQueue<Candidate> result, Long totalPop) {
        Object[] resultArr = result.toArray();
        System.out.println("Election Results:\n");
        System.out.printf("%-30.30s %-30.30s %-30.30s\n", "Candidate", "Popular Votes", "Percentage of Total");
        for (int i = resultArr.length - 1; i >= 0; i--) {
            Candidate next = (Candidate) resultArr[i];
            String candidateId = (resultArr.length - i) + " - " + next.getName();
            String popularVotes = next.getTotalVotes().toString();
            double percentage = (next.getTotalVotes() / Double.parseDouble(totalPop.toString())) * 100;
            System.out.printf("%-30.30s %-30.30s %-30.30s\n", candidateId, popularVotes, percentage);
        }
    }

    public static void appendResults(PriorityQueue<Candidate> result, List<Candidate> eliminated) {
        for (int i = 1; i <= eliminated.size(); i++) {
            Candidate loser = eliminated.get(i - 1);
            String candidateId = (result.size() + i) + " - " + loser.getName();
            String popularVotes = loser.getTotalVotes().toString();
            System.out.printf("%-30.30s %-30.30s %-30.30s\n", candidateId, popularVotes, "N/A");
        }
    }

}
