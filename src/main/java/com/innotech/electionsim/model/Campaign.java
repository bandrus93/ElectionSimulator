package com.innotech.electionsim.model;

import com.innotech.electionsim.controller.ApprovalComparator;
import com.innotech.electionsim.controller.ElectionComparator;
import com.innotech.electionsim.controller.UserInterface;
import com.innotech.electionsim.view.DisplayManager;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Campaign {
    public enum ElectionType {
        PLURALITY,
        INSTANT_RUNOFF,
        APPROVAL
    }
    private Population population;
    private final List<Candidate> candidates;
    private final Scanner uiController;
    private final Candidate.Builder ballotRegistrar;

    public Campaign(Scanner uiController) {
        this.uiController = uiController;
        this.candidates = new ArrayList<>();
        this.ballotRegistrar = new Candidate.Builder();
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(long totalVoters) {
        this.population = Population.getInstance(totalVoters);
        population.edit(uiController);
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void addCandidate() {
        DisplayManager.refresh(DisplayManager.CANDIDATE_NAME_PROMPT);
        candidates.add(ballotRegistrar
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

    public String printCandidateList() {
        StringBuilder sb = new StringBuilder(DisplayManager.CANDIDATE_LIST_HEADING);
        for (int i = 0; i < candidates.size(); i++) {
            sb.append(i + 1).append(" - ").append(candidates.get(i).getName()).append("\n");
            if (i == candidates.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public PriorityQueue<Candidate> runElection(ElectionType electionType) {
        PriorityQueue<Candidate> electionResult = new PriorityQueue<>(new ElectionComparator());
        for (PopulationSegment segment : population.getSegments()) {
            int segmentPosition = population.getSegments().indexOf(segment);
            PriorityQueue<Candidate> eligibles = new PriorityQueue<>(new ApprovalComparator());
            for (Candidate candidate : candidates) {
                int candidatePosition = candidate.getPositionIndex();
                if (candidate.isApproved(segmentPosition, candidatePosition, segment.getMaxDiff())) {
                    eligibles.add(candidate);
                }
            }
            if ((electionType.equals(ElectionType.PLURALITY) || electionType.equals(ElectionType.INSTANT_RUNOFF)) && !eligibles.isEmpty()) {
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
        electionResult.addAll(candidates);
        return electionResult;
    }
}
