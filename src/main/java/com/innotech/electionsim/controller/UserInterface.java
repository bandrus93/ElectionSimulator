package com.innotech.electionsim.controller;

import java.util.Scanner;

public class UserInterface {
    private static final Scanner inputReader = new Scanner(System.in);
    public static final String POPULATION_PROMPT = "Enter the total number of registered voters:";
    public static final String SETTINGS_PROMPT = "To change a setting, enter the number corresponding to the setting you wish to update. When finished, type 's' to save your changes and return to the main menu.";
    public static final String CANDIDATE_EDIT_PROMPT = "To update candidate's stats, type the letter in brackets [] and the number you wish to change the stat to (1 - 9) (ex. W6), and press 'Enter'" +
            "When you are finished editing, enter 'f';";
    public static final String CANDIDATE_NAME_PROMPT = "Enter candidate name:";
    public static final String CANDIDATE_ALIGNMENT_PROMPT = "Enter the number corresponding to the candidate's party alignment:";
    public static final String CANDIDATE_ENERGY_PROMPT = "Rate the candidate's energy level (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_INTELLIGENCE_PROMPT = "Rate the candidate's general level of intelligence (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_WIT_PROMPT = "Rate the candidate's wit (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_LEVEL_HEAD_PROMPT = "Rate the candidate's level-headedness (As a whole number on a scale from 1-9):";
    public static final String CANDIDATE_SPEAK_ABILITY_PROMPT = "Rate the candidate's public speaking ability (As a whole number on a scale from 1-9):";
    public static final String LOCALE_PROMPT = "Enter election locale:";
    public static final String RACE_PROMPT = "Enter the number corresponding to the race type:";
    public static final String ELECTION_PROMPT = "Enter the number corresponding to the election type:";
    public static final String DATE_PROMPT = "Enter the date the election is to be held (in DD-MM-YYYY format):";
    public static final String SAVE_PROMPT = "Would you like to save this election result? (y/n):";
    public static final String INVALID_NUMBER_INPUT = "Please enter a valid number";
    public static final String INVALID_COMMAND = "Invalid command";
    public static int getNumericInput(String prompt) {
        System.out.println(prompt);
        do {
            String input = inputReader.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(INVALID_NUMBER_INPUT);
            }
        } while (true);
    }

    public static String getStringInput(String prompt) {
        System.out.println(prompt);
        return inputReader.nextLine();
    }

    public static Object getMenuSelection(String prompt, Object[] options) {
        System.out.println(prompt + "\n");
        for (int i = 0; i < options.length; i++) {
            Object option = options[i];
            System.out.println((i + 1) + " - " + option + "\n");
        }
        do {
            String input = inputReader.nextLine();
            try {
                int selection = Integer.parseInt(input);
                return options[selection - 1];
            } catch (Exception e) {
                System.out.println(INVALID_NUMBER_INPUT);
            }
        } while (true);
    }
}
