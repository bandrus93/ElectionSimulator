package com.innotech.electionsim.model;

import com.innotech.electionsim.controller.ApprovalComparator;
import com.innotech.electionsim.controller.ElectionComparator;
import com.innotech.electionsim.controller.UserInterface;
import com.innotech.electionsim.view.DisplayManager;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Campaign {
    private Population population;
    private final List<Candidate> candidates;

    public Campaign() {
        this.candidates = new ArrayList<>();
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(long totalVoters) {
        this.population = Population.getInstance(totalVoters);
        population.edit();
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void addCandidate() {
        candidates.add(new Candidate.Builder()
                .name(UserInterface.getStringInput(DisplayManager.CANDIDATE_NAME_PROMPT))
                .platform((Population.Segment) UserInterface.getMenuSelection(DisplayManager.CANDIDATE_ALIGNMENT_PROMPT, Population.getSegmentArray()))
                .energyLevel(UserInterface.getNumericInput(DisplayManager.CANDIDATE_ENERGY_PROMPT))
                .intelligence(UserInterface.getNumericInput(DisplayManager.CANDIDATE_INTELLIGENCE_PROMPT))
                .wit(UserInterface.getNumericInput(DisplayManager.CANDIDATE_WIT_PROMPT))
                .levelHeadedness(UserInterface.getNumericInput(DisplayManager.CANDIDATE_LEVEL_HEAD_PROMPT))
                .speakingAbility(UserInterface.getNumericInput(DisplayManager.CANDIDATE_SPEAK_ABILITY_PROMPT))
                .register()
        );
    }

    public ElectionResult runElection(String electionType) {
        PriorityQueue<Candidate> resultRank = new PriorityQueue<>(new ElectionComparator());
        for (PopulationSegment segment : population.getSegments()) {
            PriorityQueue<Candidate> eligibles = getApprovedCandidateRanking(segment);
            segment.castBallots(eligibles, electionType);
        }
        resultRank.addAll(candidates);
        return new ElectionResult(resultRank, population.getTotalPopulation());
    }

    private PriorityQueue<Candidate> getApprovedCandidateRanking(PopulationSegment segment) {
        int segmentPosition = population.getSegments().indexOf(segment);
        PriorityQueue<Candidate> eligibles = new PriorityQueue<>(new ApprovalComparator());
        for (Candidate candidate : candidates) {
            int candidatePosition = candidate.getPositionIndex();
            if (candidate.isApproved(segmentPosition, candidatePosition, segment.getMaxDiff())) {
                eligibles.add(candidate);
            }
        }
        return eligibles;
    }
}
