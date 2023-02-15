package com.innotech.electionsim.model;

import com.innotech.electionsim.controller.UserInterface;
import com.innotech.electionsim.view.DisplayManager;

import java.util.*;

public class Candidate {
    private final List<Stat> stats = new ArrayList<>();
    private long votesReceived = 0L;
    private int swayScore;

    private Candidate() {

    }

    public String getName() {
        Stat name = findStatById(DisplayManager.CANDIDTAE_NAME_LABEL);
        return name.getValue() != null ? name.getValue() : "Candidate not found";
    }

    public Long getTotalVotes() {
        return votesReceived;
    }

    public void incrementVotes(long blocKVotes) {
        this.votesReceived += blocKVotes;
    }

    public void resetVotes() {
        this.votesReceived = 0;
    }

    public int getSwayScore() {
        return swayScore;
    }

    public int getPositionIndex() {
        Population.Segment[] segments = Population.getSegmentArray();
        for (int i = 0; i < segments.length; i++) {
            if (findStatById(DisplayManager.CANDIDATE_ALIGNMENT_LABEL).getValue().equals(segments[i].toString())) {
                return i;
            }
        }
        return -1;
    }

    public boolean isApproved(int segmentPosition, int candidatePosition, int maxDiff) {
        int positionDifference = Math.abs(segmentPosition - candidatePosition);
        if (positionDifference <= maxDiff) {
            switch (positionDifference) {
                case 0: return true;
                case 1: if (swayScore >= 29) {return true;}
                case 2: if (swayScore >= 33) {return true;}
                case 3: if (swayScore >= 37) {return true;}
                case 4: if (swayScore >= 41) {return true;}
            }
        }
        return false;
    }

    public void edit() {
        boolean editing = true;
        do {
            String input = UserInterface.getStringInput(printCandidateStats() + DisplayManager.CANDIDATE_EDIT_PROMPT);
            String[] edit = input.split(" ");
            switch (edit[0].toUpperCase()) {
                case "N":
                    findStatById(DisplayManager.CANDIDTAE_NAME_LABEL).setValue(edit[1]);
                    break;
                case "A":
                    findStatById(DisplayManager.CANDIDATE_ALIGNMENT_LABEL).setValue(edit[1]);
                    break;
                case "E":
                    findStatById(DisplayManager.CANDIDATE_ENERGY_LABEL).setValue(edit[1]);
                    break;
                case "I":
                    findStatById(DisplayManager.CANDIDATE_INTELLIGENCE_LABEL).setValue(edit[1]);
                    break;
                case "W":
                    findStatById(DisplayManager.CANDIDATE_WIT_LABEL).setValue(edit[1]);
                    break;
                case "L":
                    findStatById(DisplayManager.CANDIDATE_LEVEL_HEAD_LABEL).setValue(edit[1]);
                    break;
                case "S":
                    findStatById(DisplayManager.CANDIDATE_SPEAK_ABILITY_LABEL).setValue(edit[1]);
                    break;
                case "F":
                    editing = false;
                default:
                    System.out.println("Invalid edit command");
            }
            swayScore = computeSwayScore();
            DisplayManager.refresh(printCandidateStats() + DisplayManager.CANDIDATE_EDIT_PROMPT);
        } while (editing);
    }

    private String printCandidateStats() {
        Iterator<Stat> staterator = stats.iterator();
        StringBuilder sb = new StringBuilder();
        do {
            Stat nextStat = staterator.next();
            sb.append(nextStat.getLabel());
            if (nextStat.isNumeral()) {
                sb.append("*".repeat(Math.max(0, Integer.parseInt(nextStat.getValue())))).append("\n");
            } else {
                sb.append(nextStat.getValue()).append("\n");
            }
            if (!staterator.hasNext()) {
                sb.append("\n");
            }
        } while (staterator.hasNext());
        return sb.toString();
    }

    private Stat findStatById(String statLabel) {
        for (Stat stat : stats) {
            if (stat.getLabel().equals(statLabel)) {
                return stat;
            }
        }
        return null;
    }
    private int computeSwayScore() {
        int scoreTotal = 0;
        for (Stat stat : stats) {
            if (stat.isNumeral()) {
                scoreTotal += Integer.parseInt(stat.getValue());
            }
        }
        return scoreTotal;
    }

    public static class Builder {
        private final Candidate candidate = new Candidate();

        public Builder name(String name) {
            candidate.stats.add(new Stat(DisplayManager.CANDIDTAE_NAME_LABEL, name, false));
            return this;
        }

        public Builder platform(Population.Segment partyAlignment) {
            candidate.stats.add(new Stat(DisplayManager.CANDIDATE_ALIGNMENT_LABEL, partyAlignment.toString(), false));
            return this;
        }

        public Builder energyLevel(Integer energy) {
            candidate.stats.add(new Stat(DisplayManager.CANDIDATE_ENERGY_LABEL, energy.toString(), true));
            return this;
        }

        public Builder intelligence(Integer intelligence) {
            candidate.stats.add(new Stat(DisplayManager.CANDIDATE_INTELLIGENCE_LABEL, intelligence.toString(), true));
            return this;
        }

        public Builder wit(Integer wit) {
            candidate.stats.add(new Stat(DisplayManager.CANDIDATE_WIT_LABEL, wit.toString(), true));
            return this;
        }

        public Builder levelHeadedness(Integer levelHeadedness) {
            candidate.stats.add(new Stat(DisplayManager.CANDIDATE_LEVEL_HEAD_LABEL, levelHeadedness.toString(), true));
            return this;
        }

        public Builder speakingAbility(Integer speakAbility) {
            candidate.stats.add(new Stat(DisplayManager.CANDIDATE_SPEAK_ABILITY_LABEL, speakAbility.toString(), true));
            return this;
        }

        public Candidate register() {
            candidate.swayScore = candidate.computeSwayScore();
            return candidate;
        }
    }
}
