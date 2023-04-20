package com.innotech.electionsim.population;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PopulationGraph {
    public static final MathContext coefficientContext = new MathContext(8, RoundingMode.DOWN);
    private static final LinkedList<PopulationSegment> segments = new LinkedList<>();
    private static long populationIncrement;

    private PopulationGraph(long registeredVoters) {
        populationIncrement = new BigDecimal(registeredVoters / 256.0, coefficientContext).longValue();
    }

    public static PopulationGraph getInstance(long registeredVoters) {
        return new PopulationGraph(registeredVoters).initializeSegments();
    }

    private PopulationGraph initializeSegments() {
        segments.add(new PopulationSegment(Population.Segment.RADICAL_LEFT, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.FAR_LEFT, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.MODERATE_LEFT, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.CENTRE_LEFT, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.CENTRIST, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.CENTRE_RIGHT, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.MODERATE_RIGHT, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.FAR_RIGHT, populationIncrement));
        segments.add(new PopulationSegment(Population.Segment.RADICAL_RIGHT, populationIncrement));
        return this;
    }

    public static long computeBase(double coefficient, long increment) {
        return new BigDecimal(coefficient * increment, coefficientContext).longValue();
    }

    public LinkedList<PopulationSegment> getSegments() {
        return segments;
    }

    public PopulationSegment getSegment(Population.Segment segment) {
        for (PopulationSegment populationSegment : segments) {
            if (populationSegment.getVoterBlock().equals(segment)) {
                return populationSegment;
            }
        }
        return null;
    }

    public long getPopulationIncrement() {
        return populationIncrement;
    }

    public List<PopulationSegment> getOvertonWindow() {
        List<PopulationSegment> centerPeaks = new ArrayList<>(4);
        PopulationSegment currentCenter = getSegment(Population.Segment.CENTRIST);
        for (PopulationSegment candidate : segments) {
            if (candidate.getOvertonCoefficient() == currentCenter.getOvertonCoefficient()) {
                centerPeaks.add(candidate);
            } else if (candidate.getOvertonCoefficient() > currentCenter.getOvertonCoefficient()) {
                currentCenter = candidate;
                centerPeaks.clear();
                centerPeaks.add(candidate);
            }
        }
        return centerPeaks;
    }

    public int getOvertonCenterIndex(List<PopulationSegment> overtonWindow, Population.Bias bias) {
        int windowSize = overtonWindow.size();
        PopulationSegment center = overtonWindow.get(0);
        if (windowSize > 1 && windowSize % 2 == 0) {
            center = bias.equals(Population.Bias.RIGHT)
                    ? overtonWindow.get(windowSize / 2)
                    : overtonWindow.get((windowSize / 2) - 1);
        } else if (windowSize > 1) {
            center = overtonWindow.get(1);
        }
        return segments.indexOf(center);
    }

    public List<Integer> getAllWindowIndexes(List<PopulationSegment> overtonWindow) {
        List<Integer> centerIndexes = new ArrayList<>(overtonWindow.size());
        for (PopulationSegment segment : overtonWindow) {
            centerIndexes.add(segments.indexOf(segment));
        }
        return centerIndexes;
    }

    public List<Integer> getSplitIndexes(List<Integer> overtonCenterIndexes) {
        List<Integer> adjustedCenterIndexes = new ArrayList<>();
        for (Integer index : overtonCenterIndexes) {
            int leftSplitIndex = index - 2 < 0 ? segments.size() + (index - 2) : index - 2;
            int rightSplitIndex = index + 2 > segments.size() - 1 ? (index + 2) - segments.size() : index + 2;
            if (!adjustedCenterIndexes.contains(leftSplitIndex)) adjustedCenterIndexes.add(leftSplitIndex);
            if (!adjustedCenterIndexes.contains(rightSplitIndex)) adjustedCenterIndexes.add(rightSplitIndex);
        }
        return adjustedCenterIndexes;
    }

    public PopulationSegment getUnifiedCenter(Population.Bias bias) {
        List<PopulationSegment> overtonWindow = getOvertonWindow();
        int windowSize = overtonWindow.size();
        if (windowSize == 1) {
            return overtonWindow.get(0);
        } else if (windowSize == 2) {
            List<Integer> overtonIndexList = getAllWindowIndexes(overtonWindow);
            int unifiedCenterIndex = overtonIndexList.get(0) + ((overtonIndexList.get(1) - overtonIndexList.get(0)) / 2);
            return segments.get(unifiedCenterIndex);
        } else {
            return segments.get(getOvertonCenterIndex(overtonWindow, bias));
        }
    }

    public int getOvertonCoefficientSum() {
        int sum = 0;
        for (PopulationSegment segment : segments) {
            sum += segment.getOvertonCoefficient();
        }
        return sum;
    }

    public boolean checkDistribution(List<Integer> overtonIndexes, int toDistribute) {
        for (Integer index : overtonIndexes) {
            if (segments.get(index).getOvertonCoefficient() - toDistribute < 0) return false;
        }
        return true;
    }

    public void proportionalDistribution(int centerIndex, int distribute) {
        for (PopulationSegment segment : segments) {
            int indexAt = segments.indexOf(segment);
            if (indexAt != centerIndex) {
                segment.incrementCoefficient();
            } else {
                segment.incrementCoefficient(distribute - (segments.size() - 1));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder statBuilder = new StringBuilder();
        statBuilder.append("Population increment: ").append(populationIncrement).append("\n");
        for (PopulationSegment segment : segments) {
            statBuilder.append(segment.getVoterBlock())
                    .append(" voters: ")
                    .append(segment.getOvertonCoefficient() * populationIncrement)
                    .append("\n");
        }
        return statBuilder.toString();
    }

}
