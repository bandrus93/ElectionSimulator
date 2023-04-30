package com.innotech.electionsim.election;

import com.innotech.electionsim.candidate.ApprovalComparator;
import com.innotech.electionsim.candidate.Candidate;
import com.innotech.electionsim.interfaces.Editable;
import com.innotech.electionsim.population.Population;
import com.innotech.electionsim.population.PopulationSegment;
import com.innotech.electionsim.view.DisplayManager;
import com.innotech.electionsim.view.UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static com.innotech.electionsim.view.UserInterface.CANDIDATE_LIST_HEADING;

public class Campaign implements Editable {
    private final Population population;
    private final List<Candidate> candidates;
    private final ElectionSettings context;

    public Campaign(ElectionSettings context) {
        population = Population.getInstance(UserInterface.getNumericInput(UserInterface.POPULATION_PROMPT), context.getBias());
        candidates = new ArrayList<>();
        this.context = context;
    }

    private void addCandidate() {
        candidates.add(new Candidate.Builder()
                .name(UserInterface.getStringInput(UserInterface.CANDIDATE_NAME_PROMPT))
                .platform((Population.Segment) UserInterface.getMenuSelection(UserInterface.CANDIDATE_ALIGNMENT_PROMPT, Population.getSegmentArray(), List.of("")))
                .energyLevel(UserInterface.getNumericInput(UserInterface.CANDIDATE_ENERGY_PROMPT))
                .intelligence(UserInterface.getNumericInput(UserInterface.CANDIDATE_INTELLIGENCE_PROMPT))
                .wit(UserInterface.getNumericInput(UserInterface.CANDIDATE_WIT_PROMPT))
                .levelHeadedness(UserInterface.getNumericInput(UserInterface.CANDIDATE_LEVEL_HEAD_PROMPT))
                .speakingAbility(UserInterface.getNumericInput(UserInterface.CANDIDATE_SPEAK_ABILITY_PROMPT))
                .register()
        );
    }

    private Candidate remove(int atPosition) {
        return candidates.remove(atPosition);
    }

    @Override
    public void edit() {
        boolean editing = true;
        population.edit();
        do {
            DisplayManager.refresh(getCandidateList());
            String command = UserInterface.getStringInput(UserInterface.CAMPAIGN_EDIT_PROMPT);
            int selection = command.length() == 2 ? Integer.parseInt(String.valueOf(command.charAt(1))) - 1 : 0;
            switch (command.charAt(0)) {
                case '+' -> addCandidate();
                case '-' -> DisplayManager.refresh(remove(selection).getName() + " has dropped out of the race.");
                case 'e' -> candidates.get(selection).edit();
                default -> {
                    if (command.charAt(0) != 'r') DisplayManager.refresh(UserInterface.INVALID_COMMAND);
                    else editing = false;
                }
            }
        } while (editing);
    }

    public ElectionResult runElection() {
        if (!context.getType().equals(ElectionSettings.ElectionType.INSTANT_RUNOFF)) {
            return cycle();
        } else {
            ElectionResult result;
            List<Candidate> eliminated = new ArrayList<>();
            do {
                result = cycle();
                if (result.getWinner().hasMajority(result.getTotalPopulation())) {
                    candidates.remove(result.getLoser());
                    eliminated.add(result.getLoser());
                    resetCandidateVotes();
                }
            } while (result.getWinner().hasMajority(result.getTotalPopulation()));
            return result.append(eliminated);
        }
    }

    private String getCandidateList() {
        StringBuilder sb = new StringBuilder(CANDIDATE_LIST_HEADING);
        for (int i = 0; i < candidates.size(); i++) {
            sb.append(i + 1).append(" - ").append(candidates.get(i).getName()).append("\n");
            if (i == candidates.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private ElectionResult cycle() {
        PriorityQueue<Candidate> resultRank = new PriorityQueue<>(new ElectionComparator());
        ElectionSettings.ElectionType electionType = context.getType();
        for (PopulationSegment segment : population.getSegments()) {
            PriorityQueue<Candidate> eligibles = getApprovedCandidateRanking(segment);
            segment.castBallots(eligibles, electionType);
        }
        PopulationSegment swingSegment = population.getSwingSegment();
        swingSegment.castBallots(getApprovedCandidateRanking(swingSegment), electionType);
        resultRank.addAll(candidates);
        return new ElectionResult(resultRank, population.getTotalPopulation(), context);
    }

    private void resetCandidateVotes() {
        for (Candidate candidate : candidates) {
            candidate.resetVotes();
        }
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
