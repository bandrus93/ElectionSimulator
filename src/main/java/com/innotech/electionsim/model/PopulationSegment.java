package com.innotech.electionsim.model;

public class PopulationSegment {
    private final Population.Segment voterBlock;
    private int maxDiff = 3;
    private long blockBase;

    public PopulationSegment(Population.Segment blockGroup, long voterBase) {
        this.voterBlock = blockGroup;
        this.blockBase = voterBase;
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

    public void castBallots(Iterable<Candidate> eligibles) {
        int totalSway = computeTotalSway(eligibles);
        for (Candidate toRank : eligibles) {
            long votesGained = Math.round(this.blockBase * (toRank.getSwayScore() / Double.parseDouble(Integer.toString(totalSway))));
            toRank.incrementVotes(votesGained);
        }
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
}
