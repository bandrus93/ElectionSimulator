package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Population;
import com.innotech.electionsim.view.DisplayManager;

import java.util.Scanner;

public class UserInterface {
    private static final Scanner inputReader = new Scanner(System.in);

    public static Scanner getInputReader() {
        return inputReader;
    }
    public static int getNumericInput(String prompt) {
        System.out.println(prompt);
        do {
            String input = inputReader.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(DisplayManager.INVALID_NUMBER_INPUT);
            }
        } while (true);
    }

    public static Population.Segment getAlignment(String prompt, Population.Segment[] options) {
        System.out.println(prompt + "\n");
        for (int i = 0; i < options.length; i++) {
            Population.Segment segment = options[i];
            System.out.println((i + 1) + " - " + segment + "\n");
        }
        do {
            String input = inputReader.nextLine();
            try {
                int selection = Integer.parseInt(input);
                return options[selection - 1];
            } catch (Exception e) {
                System.out.println(DisplayManager.INVALID_NUMBER_INPUT);
            }
        } while (true);
    }
}
