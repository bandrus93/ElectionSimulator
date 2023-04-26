package com.innotech.electionsim.view;

public class DisplayManager {

    public static String getGreetingMessage(String locale) {
        return "Welcome to " +
                locale +
                "! What would you like to do?\n(Type a number and press 'Enter' to make a selection, or type 'q' to quit):\n";
    }

    public static String[] getMainMenuOptions() {
        return new String[] {"Start New Election","Review Past Elections","Settings"};
    }

    public static void refresh(String reprint) {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        System.out.println(reprint);
    }

}
