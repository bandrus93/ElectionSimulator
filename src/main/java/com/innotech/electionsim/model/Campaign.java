package com.innotech.electionsim.model;

import com.innotech.electionsim.controller.*;
import com.innotech.electionsim.population.PopulationController;
import com.innotech.electionsim.population.PopulationSegment;
import com.innotech.electionsim.view.DisplayManager;

import java.util.List;
import java.util.PriorityQueue;

public class Campaign {
    private final PopulationController populationController;
    private final CandidateController candidateController;
    private final ElectionSettings context;

    public Campaign(PopulationController populationController, CandidateController candidateController, ElectionSettings context) {
        this.populationController = populationController;
        this.candidateController = candidateController;
        this.context = context;
    }

    public List<Candidate> getCandidates() {
        return candidateController.getCandidates();
    }

    public PopulationController getPopulationController() { return populationController; }

    public void edit(String command) {
        int selection = command.length() == 2 ? Integer.parseInt(String.valueOf(command.charAt(1))) - 1 : 0;
        switch (command.charAt(0)) {
            case '+' -> candidateController.addCandidate();
            case '-' -> DisplayManager.refresh(candidateController.remove(selection).getName() + " has dropped out of the race.");
            case 'e' -> candidateController.edit(selection);
            default -> DisplayManager.refresh(UserInterface.INVALID_COMMAND);
        }
    }

    public ElectionResult cycle() {
        PriorityQueue<Candidate> resultRank = new PriorityQueue<>(new ElectionComparator());
        for (PopulationSegment segment : populationController.getPopulation().getSegments()) {
            PriorityQueue<Candidate> eligibles = getApprovedCandidateRanking(segment);
            segment.castBallots(eligibles, context.getType());
        }
        resultRank.addAll(candidateController.getCandidates());
        return new ElectionResult(resultRank, populationController.getPopulation().getTotalPopulation(), context);
    }

    public void resetCandidateVotes() {
        for (Candidate candidate : candidateController.getCandidates()) {
            candidate.resetVotes();
        }
    }

    private PriorityQueue<Candidate> getApprovedCandidateRanking(PopulationSegment segment) {
        int segmentPosition = populationController.getPopulation().getSegments().indexOf(segment);
        PriorityQueue<Candidate> eligibles = new PriorityQueue<>(new ApprovalComparator());
        for (Candidate candidate : candidateController.getCandidates()) {
            int candidatePosition = candidate.getPositionIndex();
            if (candidate.isApproved(segmentPosition, candidatePosition, segment.getMaxDiff())) {
                eligibles.add(candidate);
            }
        }
        return eligibles;
    }
}
