package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.*;
import com.innotech.electionsim.view.MainView;
import com.innotech.electionsim.view.ResultView;

public class ElectionRunner {
    private static final ElectionRunner instance = new ElectionRunner();
    private final DataController dataController;
    private final Election election;

    private ElectionRunner() {
        dataController = DataController.getInstance();
        election = Election.getInstance();
    }

    public static ElectionRunner getInstance() {
        return instance;
    }

    public void start() {
        boolean isActiveSession = true;
        do {
            switch (UserInterface.getStringInput(MainView.getInstance(election.getElectionSettings().getLocale()).getView())) {
                case "1" -> {
                    ElectionResult result = election.run();
                    ResultView.getInstance(result).display();
                    if (UserInterface.getStringInput(UserInterface.SAVE_PROMPT).equals("y") && dataController != null) {
                        dataController.saveElectionResult(result);
                    }
                }
                case "2" -> {
                    if(dataController != null) {
                        dataController.readResultList();
                    } else {
                        System.out.println("Unable to load save data.");
                    }
                }
                case "3" -> election.updateSettings();
                case "q" -> {
                    System.out.println("See you soon!");
                    isActiveSession = false;
                }
                default -> System.out.println("Invalid selection");
            }
        } while (isActiveSession);
    }
}
