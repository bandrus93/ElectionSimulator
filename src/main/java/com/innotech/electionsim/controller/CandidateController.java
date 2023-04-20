package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Candidate;
import com.innotech.electionsim.population.Population;
import com.innotech.electionsim.view.CandidateView;

import java.util.ArrayList;
import java.util.List;

public class CandidateController {
    private final List<Candidate> candidates;

    private CandidateController() {
        candidates = new ArrayList<>();
    }

    public static CandidateController getInstance() {
        return new CandidateController();
    }

    public void addCandidate() {
        candidates.add(new Candidate.Builder()
                .name(UserInterface.getStringInput(UserInterface.CANDIDATE_NAME_PROMPT))
                .platform((Population.Segment) UserInterface.getMenuSelection(UserInterface.CANDIDATE_ALIGNMENT_PROMPT, Population.getSegmentArray()))
                .energyLevel(UserInterface.getNumericInput(UserInterface.CANDIDATE_ENERGY_PROMPT))
                .intelligence(UserInterface.getNumericInput(UserInterface.CANDIDATE_INTELLIGENCE_PROMPT))
                .wit(UserInterface.getNumericInput(UserInterface.CANDIDATE_WIT_PROMPT))
                .levelHeadedness(UserInterface.getNumericInput(UserInterface.CANDIDATE_LEVEL_HEAD_PROMPT))
                .speakingAbility(UserInterface.getNumericInput(UserInterface.CANDIDATE_SPEAK_ABILITY_PROMPT))
                .register()
        );
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void edit(int atPosition) {
        Candidate candidate = candidates.get(atPosition);
        boolean editing = true;
        do {
            CandidateView.getInstance(candidate).display();
            String input = UserInterface.getStringInput(UserInterface.CANDIDATE_EDIT_PROMPT);
            String replaceable = input.substring(1);
            switch (String.valueOf(input.charAt(0)).toUpperCase()) {
                case "N" -> candidate.findStatById(CandidateView.CANDIDTAE_NAME_LABEL).setValue(replaceable);
                case "A" -> candidate.findStatById(CandidateView.CANDIDATE_ALIGNMENT_LABEL).setValue(replaceable);
                case "E" -> candidate.findStatById(CandidateView.CANDIDATE_ENERGY_LABEL).setValue(replaceable);
                case "I" -> candidate.findStatById(CandidateView.CANDIDATE_INTELLIGENCE_LABEL).setValue(replaceable);
                case "W" -> candidate.findStatById(CandidateView.CANDIDATE_WIT_LABEL).setValue(replaceable);
                case "L" -> candidate.findStatById(CandidateView.CANDIDATE_LEVEL_HEAD_LABEL).setValue(replaceable);
                case "S" -> candidate.findStatById(CandidateView.CANDIDATE_SPEAK_ABILITY_LABEL).setValue(replaceable);
                case "F" -> editing = false;
                default -> System.out.println("Invalid edit command");
            }
            candidate.setSwayScore(candidate.computeSwayScore());
        } while (editing);
    }

    public Candidate remove(int atPosition) {
        return candidates.remove(atPosition);
    }
}
