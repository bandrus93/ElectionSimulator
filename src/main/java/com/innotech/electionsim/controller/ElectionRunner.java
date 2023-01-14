package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Campaign;
import com.innotech.electionsim.model.Candidate;
import com.innotech.electionsim.view.DisplayManager;

import java.util.*;

public class ElectionRunner {
    private static final ElectionRunner instance = new ElectionRunner();
    private final Scanner uiController;

    private ElectionRunner() {
        uiController = UserInterface.getInputReader();
    }

    public static ElectionRunner getInstance() {
        return instance;
    }

    public void start() {
        boolean isActiveSession = true;
        System.out.println(DisplayManager.GREETING_MESSAGE + DisplayManager.MAIN_MENU);
        do {
            String input = uiController.nextLine();
            switch (input) {
                case "1" -> {
                    newElection();
                }
                case "q" -> {
                    isActiveSession = false;
                }
                default -> System.out.println("Invalid selection");
            }
            String message = isActiveSession ? DisplayManager.GREETING_MESSAGE + DisplayManager.MAIN_MENU : "See you soon!";
            System.out.println(message);
        } while (isActiveSession);
    }

    public void newElection() {
        Campaign campaign = new Campaign(uiController);
        campaign.setPopulation(UserInterface.getNumericInput(DisplayManager.POPULATION_PROMPT));
        campaign.addCandidate();
        DisplayManager.refresh(campaign.printCandidateList() + DisplayManager.CAMPAIGN_COMMAND_LIST);
        do {
            String input = uiController.nextLine();
            String[] command = input.split(" ");
            if (command.length == 2) {
                try {
                    if (command[0].equals("-")) {
                        Candidate removed = campaign.getCandidates().remove(Integer.parseInt(command[1]) - 1);
                        System.out.println(removed.getName() + " has dropped out of the race.");
                    } else if (command[0].equals("e")) {
                        campaign.getCandidates().get(Integer.parseInt(command[1]) - 1).edit(uiController);
                    } else {
                        System.out.println(DisplayManager.INVALID_COMMAND);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(DisplayManager.INVALID_NUMBER_INPUT);
                }
            } else {
                switch (input) {
                    case "+" -> {
                        campaign.addCandidate();
                    }
                    case "p" -> {
                        PriorityQueue<Candidate> pluralityResult = campaign.runElection(Campaign.ElectionType.PLURALITY);
                        DisplayManager.printElectionResult(pluralityResult, campaign.getPopulation().getTotalPopulation());
                        //Refactor 'clear' into save functionality
                        //Candidate.getCandidates().clear();
                        return;
                    }
                    case "r" -> {
                        List<Candidate> eliminated = new ArrayList<>();
                        do {
                            PriorityQueue<Candidate> preliminaryResult = campaign.runElection(Campaign.ElectionType.INSTANT_RUNOFF);
                            Object[] preliminaryArr = preliminaryResult.toArray();
                            Candidate leader = (Candidate) preliminaryArr[preliminaryArr.length - 1];
                            if (leader.getTotalVotes() / Double.parseDouble(campaign.getPopulation().getTotalPopulation().toString()) > 0.5) {
                                DisplayManager.printElectionResult(preliminaryResult, campaign.getPopulation().getTotalPopulation());
                                DisplayManager.appendResults(preliminaryResult, eliminated);
                                //Refactor 'clear' into save functionality
                                //Candidate.getCandidates().clear();
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
                    case "a" -> {
                        PriorityQueue<Candidate> approvalResult = campaign.runElection(Campaign.ElectionType.APPROVAL);
                        DisplayManager.printElectionResult(approvalResult, campaign.getPopulation().getTotalPopulation());
                        //Refactor 'clear' into save functionality
                        //Candidate.getCandidates().clear();
                        return;
                    }
                    default -> System.out.println(DisplayManager.INVALID_COMMAND);
                }
            }
            DisplayManager.refresh(campaign.printCandidateList() + DisplayManager.CAMPAIGN_COMMAND_LIST);
        } while (true);
    }
}
