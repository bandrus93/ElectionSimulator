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

    private void setMaxDiff() {
        if (this.voterBlock.equals(Population.Segment.CENTRIST)
        || this.voterBlock.equals(Population.Segment.MODERATE_LEFT)
        || this.voterBlock.equals(Population.Segment.MODERATE_RIGHT)) {
            maxDiff = 4;
        }
    }
}
