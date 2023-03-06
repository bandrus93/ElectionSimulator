package com.innotech.electionsim.model;

import com.innotech.electionsim.view.CandidateView;

import java.util.*;

public class Candidate {
    private final List<Stat> stats = new ArrayList<>();
    private long votesReceived = 0L;
    private int swayScore;

    private Candidate() {

    }

    public List<Stat> getStats() {
        return stats;
    }

    public String getName() {
        Stat name = findStatById(CandidateView.CANDIDTAE_NAME_LABEL);
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

    public void setSwayScore(int swayScore) {
        this.swayScore = swayScore;
    }

    public int getPositionIndex() {
        Population.Segment[] segments = Population.getSegmentArray();
        for (int i = 0; i < segments.length; i++) {
            if (findStatById(CandidateView.CANDIDATE_ALIGNMENT_LABEL).getValue().equals(segments[i].toString())) {
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

    public static class Builder {
        private final Candidate candidate = new Candidate();

        public Builder name(String name) {
            candidate.stats.add(new Stat(CandidateView.CANDIDTAE_NAME_LABEL, name, false));
            return this;
        }

        public Builder platform(Population.Segment partyAlignment) {
            candidate.stats.add(new Stat(CandidateView.CANDIDATE_ALIGNMENT_LABEL, partyAlignment.toString(), false));
            return this;
        }

        public Builder energyLevel(Integer energy) {
            candidate.stats.add(new Stat(CandidateView.CANDIDATE_ENERGY_LABEL, energy.toString(), true));
            return this;
        }

        public Builder intelligence(Integer intelligence) {
            candidate.stats.add(new Stat(CandidateView.CANDIDATE_INTELLIGENCE_LABEL, intelligence.toString(), true));
            return this;
        }

        public Builder wit(Integer wit) {
            candidate.stats.add(new Stat(CandidateView.CANDIDATE_WIT_LABEL, wit.toString(), true));
            return this;
        }

        public Builder levelHeadedness(Integer levelHeadedness) {
            candidate.stats.add(new Stat(CandidateView.CANDIDATE_LEVEL_HEAD_LABEL, levelHeadedness.toString(), true));
            return this;
        }

        public Builder speakingAbility(Integer speakAbility) {
            candidate.stats.add(new Stat(CandidateView.CANDIDATE_SPEAK_ABILITY_LABEL, speakAbility.toString(), true));
            return this;
        }

        public Candidate register() {
            candidate.swayScore = candidate.computeSwayScore();
            return candidate;
        }
    }
}
