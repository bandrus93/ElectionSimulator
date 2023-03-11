package com.innotech.electionsim.model;

public class PopulationSegment {
    private final Population.Segment voterBlock;
    private int maxDiff = 3;
    private double overtonCoefficient;

    private long blockBase;

    public PopulationSegment(Population.Segment blockGroup, long totalPopulation) {
        voterBlock = blockGroup;
        switch (voterBlock.ordinal()) {
            case 8, 0 -> overtonCoefficient = 1;
            case 7, 1 -> overtonCoefficient = 8;
            case 6, 2 -> overtonCoefficient = 28;
            case 5, 3 -> overtonCoefficient = 56;
            case 4 -> overtonCoefficient = 70;
            default -> throw new RuntimeException();
        }
        blockBase = computeBlockBase(totalPopulation);
        setMaxDiff();
    }

    public Population.Segment getVoterBlock() {
        return voterBlock;
    }

    public long getBlockBase() {
        return blockBase;
    }

    public void setBlockBase(long blockBase) {
        this.blockBase = blockBase;
    }

    public int getMaxDiff() {
        return maxDiff;
    }

    public void castBallots(Iterable<Candidate> eligibles, ElectionSettings.ElectionType electionType) {
        for (Candidate toRank : eligibles) {
            int totalSway = ElectionSettings.ElectionType.APPROVAL.equals(electionType) ? toRank.getSwayScore() : computeTotalSway(eligibles);
            long votesGained = Math.round(blockBase * (toRank.getSwayScore() / Double.parseDouble(Integer.toString(totalSway))));
            toRank.incrementVotes(votesGained);
        }
    }

    private long computeBlockBase(long totalPopulation) {
        return Math.round((overtonCoefficient / 256.0) * totalPopulation);
    }

    private int computeTotalSway(Iterable<Candidate> eligibles) {
        int totalSway = 0;
        for (Candidate eligible : eligibles) {
            totalSway += eligible.getSwayScore();
        }
        return totalSway;
    }

    private void setMaxDiff() {
        if (this.voterBlock.equals(Population.Segment.CENTRIST)
        || this.voterBlock.equals(Population.Segment.MODERATE_LEFT)
        || this.voterBlock.equals(Population.Segment.MODERATE_RIGHT)) {
            maxDiff = 4;
        }
    }

    public double getOvertonCoefficient() {
        return overtonCoefficient;
    }

    public void setOvertonCoefficient(double coefficient) {
        overtonCoefficient = coefficient;
    }
}
