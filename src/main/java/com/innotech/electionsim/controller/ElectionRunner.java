package com.innotech.electionsim.controller;

import com.innotech.electionsim.data.ElectionSettings;
import com.innotech.electionsim.data.ResultDao;
import com.innotech.electionsim.model.Campaign;
import com.innotech.electionsim.model.Candidate;
import com.innotech.electionsim.model.ElectionResult;
import com.innotech.electionsim.view.DisplayManager;

import java.io.IOException;
import java.util.*;

public class ElectionRunner {
    private static final ElectionRunner instance = new ElectionRunner();
    private ElectionSettings settings;
    private final ResultDao electionRepo;

    private ElectionRunner() {
        electionRepo = ResultDao.getInstance();
    }

    public static ElectionRunner getInstance(ElectionSettings settings) {
        instance.settings = settings;
        DisplayManager.setUserSettings(settings);
        return instance;
    }

    public void start() {
        boolean isActiveSession = true;
        do {
            switch (UserInterface.getStringInput(DisplayManager.getGreetingMessage() + DisplayManager.MAIN_MENU)) {
                case "1" -> newElection();
                case "2" -> {
                    try {
                        List<ElectionResult> saved = electionRepo.load();
                        boolean viewing = true;
                        do {
                            DisplayManager.printSavedResults(saved);
                            String selection = UserInterface.getStringInput(DisplayManager.VIEW_RESULT_PROMPT);
                            if ("x".equals(selection)) {
                                viewing = false;
                            } else {
                                DisplayManager.printElectionResult(saved.get(Integer.parseInt(selection) - 1));
                            }
                        } while (viewing);
                    } catch (IOException e) {
                        System.out.println("Failed to load save data.");
                    }
                }
                case "3" -> settings.update();
                case "q" -> {
                    System.out.println("See you soon!");
                    isActiveSession = false;
                }
                default -> System.out.println("Invalid selection");
            }
        } while (isActiveSession);
    }

    public void newElection() {
        Campaign campaign = new Campaign();
        campaign.setPopulation(UserInterface.getNumericInput(DisplayManager.POPULATION_PROMPT));
        campaign.addCandidate();
        do {
            String input = UserInterface.getStringInput(DisplayManager.printCandidateList(campaign) + DisplayManager.CAMPAIGN_COMMAND_LIST);
            String[] command = input.split(" ");
            if (command.length == 2) {
                try {
                    if (command[0].equals("-")) {
                        Candidate removed = campaign.getCandidates().remove(Integer.parseInt(command[1]) - 1);
                        System.out.println(removed.getName() + " has dropped out of the race.");
                    } else if (command[0].equals("e")) {
                        campaign.getCandidates().get(Integer.parseInt(command[1]) - 1).edit();
                    } else {
                        System.out.println(DisplayManager.INVALID_COMMAND);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(DisplayManager.INVALID_NUMBER_INPUT);
                }
            } else {
                switch (input) {
                    case "+" -> campaign.addCandidate();
                    case "r" -> {
                        ElectionResult result;
                        String electionType = settings.getType();
                        if (!electionType.equals(ElectionSettings.ElectionType.INSTANT_RUNOFF.toString())) {
                            result = campaign.runElection(electionType);
                            DisplayManager.printElectionResult(result);
                            if (UserInterface.getStringInput(DisplayManager.SAVE_PROMPT).equals("y")) {
                                result.setMetaData(settings);
                                electionRepo.save(result);
                            }
                            campaign.getCandidates().clear();
                            return;
                        } else {
                            List<Candidate> eliminated = new ArrayList<>();
                            do {
                                ElectionResult preliminaryResult = campaign.runElection(electionType);
                                Object[] preliminaryArr = preliminaryResult.getCandidateRanking().toArray();
                                Candidate leader = (Candidate) preliminaryArr[preliminaryArr.length - 1];
                                if (leader.getTotalVotes() / Double.parseDouble(campaign.getPopulation().getTotalPopulation().toString()) > 0.5) {
                                    result = preliminaryResult.append(eliminated);
                                    DisplayManager.printElectionResult(result);
                                    System.out.println(DisplayManager.SAVE_PROMPT);
                                    if (UserInterface.getStringInput(DisplayManager.SAVE_PROMPT).equals("y")) {
                                        result.setMetaData(settings);
                                        electionRepo.save(result);
                                    }
                                    campaign.getCandidates().clear();
                                    return;
                                } else {
                                    Candidate loser = (Candidate) preliminaryArr[0];
                                    campaign.getCandidates().remove(loser);
                                    eliminated.add(loser);
                                    for (Candidate candidate : campaign.getCandidates()) {
                                        candidate.resetVotes();
                                    }
                                }
                            } while (true);
                        }
                    }
                    default -> System.out.println(DisplayManager.INVALID_COMMAND);
                }
            }
            DisplayManager.refresh(DisplayManager.printCandidateList(campaign) + DisplayManager.CAMPAIGN_COMMAND_LIST);
        } while (true);
    }
}
