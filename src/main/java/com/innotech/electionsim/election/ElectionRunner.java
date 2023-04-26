package com.innotech.electionsim.election;

import com.innotech.electionsim.view.UserInterface;
import com.innotech.electionsim.services.DataService;
import com.innotech.electionsim.view.DisplayManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ElectionRunner {
    private static final ElectionRunner instance;

    static {
        try {
            instance = new ElectionRunner();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private final DataService data;
    private final ElectionSettings settings;

    private ElectionRunner() throws IOException {
        data = new DataService();
        settings = data.getSavedSettings();
    }

    public static ElectionRunner getInstance() {
        return instance;
    }

    public void start() {
        boolean isActiveSession = true;
        do {
            switch (UserInterface.getMenuSelection(DisplayManager.getGreetingMessage(settings.getLocale()), DisplayManager.getMainMenuOptions(), List.of("q")).toString()) {
                case "Start New Election" -> {
                    Campaign campaign = new Campaign(settings);
                    campaign.edit();
                    ElectionResult result = campaign.runElection();
                    DisplayManager.refresh(result.getResultTable());
                    if (UserInterface.getStringInput(UserInterface.SAVE_PROMPT).equals("y")) {
                        data.saveElectionResult(result);
                    }
                }
                case "Review Past Elections" -> {
                    boolean viewing = true;
                    do {
                        try {
                            List<ElectionResult> savedResults = data.getSavedResults();
                            if (savedResults.isEmpty()) {
                                DisplayManager.refresh(UserInterface.EMPTY_SAVE_MESSAGE);
                                viewing = false;
                            } else {
                                ElectionResult selected = (ElectionResult) UserInterface.getMenuSelection(UserInterface.RESULT_LIST_PROMPT, savedResults.toArray(), Arrays.asList("f","x"));
                                DisplayManager.refresh(selected.getResultTable());
                                String escape = UserInterface.getStringInput(UserInterface.EXIT_REVIEW_MODE_PROMPT);
                                if ("x".equals(escape)) viewing = false;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } while (viewing);
                }
                case "Settings" -> {
                    settings.edit();
                    try {
                        data.saveCurrentSettings(settings);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "q" -> {
                    System.out.println("See you soon!");
                    isActiveSession = false;
                }
                default -> System.out.println("Invalid selection");
            }
        } while (isActiveSession);
    }
}
