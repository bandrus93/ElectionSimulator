package com.innotech.electionsim.candidate;

import com.innotech.electionsim.interfaces.Editable;
import com.innotech.electionsim.population.Population;
import com.innotech.electionsim.view.UserInterface;

import java.util.*;

public class Candidate implements Editable {
    public static final String CANDIDATE_NAME_LABEL = "[N]ame: ";
    public static final String CANDIDATE_ALIGNMENT_LABEL = "[A]lignment: ";
    public static final String CANDIDATE_ENERGY_LABEL = "[E]nergy          | ";
    public static final String CANDIDATE_INTELLIGENCE_LABEL = "[I]ntelligence    | ";
    public static final String CANDIDATE_WIT_LABEL = "[W]it             | ";
    public static final String CANDIDATE_LEVEL_HEAD_LABEL = "[L]evel-headedness| ";
    public static final String CANDIDATE_SPEAK_ABILITY_LABEL = "[S]peaking ability| ";
    private final List<Stat> stats = new ArrayList<>();
    private long votesReceived = 0L;
    private int swayScore;

    private Candidate() {

    }

    public List<Stat> getStats() {
        return stats;
    }

    public String getName() {
        Stat name = findStatById(CANDIDATE_NAME_LABEL);
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
            if (findStatById(CANDIDATE_ALIGNMENT_LABEL).getValue().equals(segments[i].toString())) {
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

    public boolean hasMajority(Long totalPopulation) {
        return !(votesReceived / Double.parseDouble(totalPopulation.toString()) > 0.5);
    }

    public Stat findStatById(String statLabel) {
        for (Stat stat : stats) {
            if (stat.getLabel().equals(statLabel)) {
                return stat;
            }
        }
        return null;
    }
    public int computeSwayScore() {
        int scoreTotal = 0;
        for (Stat stat : stats) {
            if (stat.isNumeral()) {
                scoreTotal += Integer.parseInt(stat.getValue());
            }
        }
        return scoreTotal;
    }

    @Override
    public void edit() {
        boolean editing = true;
        do {
            System.out.println(this);
            String input = UserInterface.getStringInput(UserInterface.CANDIDATE_EDIT_PROMPT);
            String replaceable = input.substring(1);
            switch (String.valueOf(input.charAt(0)).toUpperCase()) {
                case "N" -> findStatById(CANDIDATE_NAME_LABEL).setValue(replaceable);
                case "A" -> findStatById(CANDIDATE_ALIGNMENT_LABEL).setValue(replaceable);
                case "E" -> findStatById(CANDIDATE_ENERGY_LABEL).setValue(replaceable);
                case "I" -> findStatById(CANDIDATE_INTELLIGENCE_LABEL).setValue(replaceable);
                case "W" -> findStatById(CANDIDATE_WIT_LABEL).setValue(replaceable);
                case "L" -> findStatById(CANDIDATE_LEVEL_HEAD_LABEL).setValue(replaceable);
                case "S" -> findStatById(CANDIDATE_SPEAK_ABILITY_LABEL).setValue(replaceable);
                case "F" -> editing = false;
                default -> System.out.println("Invalid edit command");
            }
            swayScore = computeSwayScore();
        } while (editing);
    }

    @Override
    public String toString() {
        StringBuilder viewBuilder = new StringBuilder();
        Iterator<Stat> staterator = getStats().iterator();
        do {
            Stat nextStat = staterator.next();
            viewBuilder.append(nextStat.getLabel());
            if (nextStat.isNumeral()) {
                viewBuilder.append("*".repeat(Math.max(0, Integer.parseInt(nextStat.getValue())))).append("\n");
            } else {
                viewBuilder.append(nextStat.getValue()).append("\n");
            }
            if (!staterator.hasNext()) {
                viewBuilder.append("\n");
            }
        } while (staterator.hasNext());
        return viewBuilder.toString();
    }

    public static class Builder {
        private final Candidate candidate = new Candidate();

        public Builder name(String name) {
            candidate.stats.add(new Stat(CANDIDATE_NAME_LABEL, name, false));
            return this;
        }

        public Builder platform(Population.Segment partyAlignment) {
            candidate.stats.add(new Stat(CANDIDATE_ALIGNMENT_LABEL, partyAlignment.toString(), false));
            return this;
        }

        public Builder energyLevel(Integer energy) {
            candidate.stats.add(new Stat(CANDIDATE_ENERGY_LABEL, energy.toString(), true));
            return this;
        }

        public Builder intelligence(Integer intelligence) {
            candidate.stats.add(new Stat(CANDIDATE_INTELLIGENCE_LABEL, intelligence.toString(), true));
            return this;
        }

        public Builder wit(Integer wit) {
            candidate.stats.add(new Stat(CANDIDATE_WIT_LABEL, wit.toString(), true));
            return this;
        }

        public Builder levelHeadedness(Integer levelHeadedness) {
            candidate.stats.add(new Stat(CANDIDATE_LEVEL_HEAD_LABEL, levelHeadedness.toString(), true));
            return this;
        }

        public Builder speakingAbility(Integer speakAbility) {
            candidate.stats.add(new Stat(CANDIDATE_SPEAK_ABILITY_LABEL, speakAbility.toString(), true));
            return this;
        }

        public Candidate register() {
            candidate.swayScore = candidate.computeSwayScore();
            return candidate;
        }
    }
}
