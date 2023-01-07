package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Candidate;
import com.innotech.electionsim.model.Population;
import com.innotech.electionsim.model.PopulationSegment;
import com.innotech.electionsim.view.DisplayManager;

import java.util.*;

public class Campaign {
    private static final Campaign instance = new Campaign();
    private final Scanner uiController;

    private Campaign() {
        uiController = UserInterface.getInputReader();
    }

    public static Campaign getInstance() {
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
                    System.out.println(DisplayManager.GREETING_MESSAGE + DisplayManager.MAIN_MENU);
                }
                case "q" -> {
                    isActiveSession = false;
                    System.out.println("See you soon!");
                }
                default -> System.out.println("Invalid selection");
            }
        } while (isActiveSession);
    }

    public void newElection() {
        long totalVoters = UserInterface.getNumericInput("Enter the total number of registered voters:");
        Population population = new Population(totalVoters);
        population.edit(uiController);
        DisplayManager.refresh(DisplayManager.CANDIDATE_NAME_PROMPT);
        Candidate.getCandidates().add(new Candidate.Builder()
                .name(uiController.nextLine())
                .platform(UserInterface.getAlignment(DisplayManager.CANDIDATE_ALIGNMENT_PROMPT, Population.getSegmentArray()))
                .energyLevel(UserInterface.getNumericInput(DisplayManager.CANDIDATE_ENERGY_PROMPT))
                .intelligence(UserInterface.getNumericInput(DisplayManager.CANDIDATE_INTELLIGENCE_PROMPT))
                .wit(UserInterface.getNumericInput(DisplayManager.CANDIDATE_WIT_PROMPT))
                .levelHeadedness(UserInterface.getNumericInput(DisplayManager.CANDIDATE_LEVEL_HEAD_PROMPT))
                .speakingAbility(UserInterface.getNumericInput(DisplayManager.CANDIDATE_SPEAK_ABILITY_PROMPT))
                .register()
        );
        DisplayManager.refresh(Candidate.printCandidateList() + DisplayManager.CAMPAIGN_COMMAND_LIST);
        do {
            String input = uiController.nextLine();
            String[] command = input.split(" ");
            if (command.length == 2) {
                try {
                    if (command[0].equals("-")) {
                        Candidate removed = Candidate.getCandidates().remove(Integer.parseInt(command[1]) - 1);
                        System.out.println(removed.getName() + " has dropped out of the race.");
                    } else if (command[0].equals("e")) {
                        Candidate.getCandidates().get(Integer.parseInt(command[1]) - 1).edit(uiController);
                    } else {
                        System.out.println(DisplayManager.INVALID_COMMAND);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(DisplayManager.INVALID_NUMBER_INPUT);
                }
            } else {
                switch (input) {
                    case "+" -> {
                        System.out.println(DisplayManager.CANDIDATE_NAME_PROMPT);
                        Candidate.getCandidates().add(new Candidate.Builder()
                                .name(uiController.nextLine())
                                .platform(UserInterface.getAlignment(DisplayManager.CANDIDATE_ALIGNMENT_PROMPT, Population.getSegmentArray()))
                                .energyLevel(UserInterface.getNumericInput(DisplayManager.CANDIDATE_ENERGY_PROMPT))
                                .intelligence(UserInterface.getNumericInput(DisplayManager.CANDIDATE_INTELLIGENCE_PROMPT))
                                .wit(UserInterface.getNumericInput(DisplayManager.CANDIDATE_WIT_PROMPT))
                                .levelHeadedness(UserInterface.getNumericInput(DisplayManager.CANDIDATE_LEVEL_HEAD_PROMPT))
                                .speakingAbility(UserInterface.getNumericInput(DisplayManager.CANDIDATE_SPEAK_ABILITY_PROMPT))
                                .register()
                        );
                    }
                    case "p" -> {
                        PriorityQueue<Candidate> pluralityResult = runElection(population, "p");
                        DisplayManager.printElectionResult(pluralityResult, population.getTotalPopulation());
                        Candidate.getCandidates().clear();
                        return;
                    }
                    case "r" -> {
                        List<Candidate> eliminated = new ArrayList<>();
                        do {
                            PriorityQueue<Candidate> preliminaryResult = runElection(population, "r");
                            Object[] preliminaryArr = preliminaryResult.toArray();
                            Candidate leader = (Candidate) preliminaryArr[preliminaryArr.length - 1];
                            if (leader.getTotalVotes() / Double.parseDouble(population.getTotalPopulation().toString()) > 0.5) {
                                DisplayManager.printElectionResult(preliminaryResult, population.getTotalPopulation());
                                DisplayManager.appendResults(preliminaryResult, eliminated);
                                Candidate.getCandidates().clear();
                                return;
                            } else {
                                Candidate loser = (Candidate) preliminaryArr[0];
                                Candidate.getCandidates().remove(loser);
                                eliminated.add(loser);
                                for (Candidate candidate : Candidate.getCandidates()) {
                                    candidate.resetVotes();
                                }
                            }
                        } while (true);
                    }
                    case "a" -> {
                        PriorityQueue<Candidate> approvalResult = runElection(population, "a");
                        DisplayManager.printElectionResult(approvalResult, population.getTotalPopulation());
                        Candidate.getCandidates().clear();
                        return;
                    }
                    default -> System.out.println(DisplayManager.INVALID_COMMAND);
                }
            }
            DisplayManager.refresh(Candidate.printCandidateList() + DisplayManager.CAMPAIGN_COMMAND_LIST);
        } while (true);
    }

    public PriorityQueue<Candidate> runElection(Population population, String electionType) {
        PriorityQueue<Candidate> electionResult = new PriorityQueue<>(new ElectionComparator());
        for (PopulationSegment segment : population.getSegments()) {
            int segmentPosition = population.getSegments().indexOf(segment);
            PriorityQueue<Candidate> eligibles = new PriorityQueue<>(new ApprovalComparator());
            for (Candidate candidate : Candidate.getCandidates()) {
                int candidatePosition = candidate.getPositionIndex();
                if (candidate.isApproved(segmentPosition, candidatePosition, segment.getMaxDiff())) {
                    eligibles.add(candidate);
                }
            }
            if ((electionType.equals("p") || electionType.equals("r")) && !eligibles.isEmpty()) {
                Integer totalSway = 0;
                for (Candidate eligible : eligibles) {
                    totalSway += eligible.getSwayScore();
                }
                for (Candidate toRank : eligibles) {
                    long totalVotesReceivable = segment.getBlockBase();
                    long votesGained = Math.round(totalVotesReceivable * (toRank.getSwayScore() / Double.parseDouble(totalSway.toString())));
                    toRank.incrementVotes(votesGained);
                }
            } else {
                Object[] approval = eligibles.toArray();
                if (approval.length > 1) {
                    for (int i = 0; i < approval.length - 1; i++) {
                        Candidate approved = (Candidate) approval[i];
                        approved.incrementVotes(segment.getBlockBase());
                    }
                } else {
                    if (approval.length == 1) {
                        Candidate selected = (Candidate) approval[0];
                        selected.incrementVotes(segment.getBlockBase());
                    }
                }
            }
        }
        electionResult.addAll(Candidate.getCandidates());
        return electionResult;
    }
}
