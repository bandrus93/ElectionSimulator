package com.innotech.electionsim.model;

import com.innotech.electionsim.population.PopulationGraphCalculator;

import java.util.*;

public class Population {
    public enum Segment {
        RADICAL_RIGHT,
        FAR_RIGHT,
        MODERATE_RIGHT,
        CENTRE_RIGHT,
        CENTRIST,
        CENTRE_LEFT,
        MODERATE_LEFT,
        FAR_LEFT,
        RADICAL_LEFT
    }

    public enum Bias {
        LEFT,
        RIGHT
    }

    private final LinkedList<PopulationSegment> segments = new LinkedList<>();
    private final long registeredVoters;
    private long populationIncrement;
    private final Bias bias = Bias.RIGHT;

    private Population(long registeredVoters) {
        this.registeredVoters = registeredVoters;
    }

    public static Population getInstance(long registeredVoters) {
        Population population = new Population(registeredVoters);
        population.populationIncrement = PopulationGraphCalculator.increment(registeredVoters);
        population.segments.add(new PopulationSegment(Segment.RADICAL_LEFT, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.FAR_LEFT, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.MODERATE_LEFT, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.CENTRE_LEFT, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.CENTRIST, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.CENTRE_RIGHT, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.MODERATE_RIGHT, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.FAR_RIGHT, population.populationIncrement));
        population.segments.add(new PopulationSegment(Segment.RADICAL_RIGHT, population.populationIncrement));
        return population;
    }

    public Long getTotalPopulation() {
        return registeredVoters;
    }

    public long getComputedPopulation() {
        long population = 0;
        for (PopulationSegment segment : segments) {
            population += segment.getBlockBase();
        }
        return population;
    }

    public PopulationSegment getSegment(Population.Segment segment) {
        for (PopulationSegment populationSegment : segments) {
            if (populationSegment.getVoterBlock().equals(segment)) {
                return populationSegment;
            }
        }
        return null;
    }

    public static Segment[] getSegmentArray() {
        return Segment.values();
    }

    public LinkedList<PopulationSegment> getSegments() {
        return segments;
    }

    public List<PopulationSegment> getOvertonWindow() {
        List<PopulationSegment> centerPeaks = new ArrayList<>(4);
        PopulationSegment currentCenter = getSegment(Segment.CENTRIST);
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

    private int getOvertonCenterIndex(List<PopulationSegment> overtonWindow) {
        int windowSize = overtonWindow.size();
        PopulationSegment center = overtonWindow.get(0);
        if (windowSize > 1 && windowSize % 2 == 0) {
            center = bias.equals(Bias.RIGHT)
                    ? overtonWindow.get(windowSize / 2)
                    : overtonWindow.get((windowSize / 2) - 1);
        } else if (windowSize > 1) {
            center = overtonWindow.get(1);
        }
        return segments.indexOf(center);
    }

    private List<Integer> getAllWindowIndexes(List<PopulationSegment> overtonWindow) {
        List<Integer> centerIndexes = new ArrayList<>(overtonWindow.size());
        for (PopulationSegment segment : overtonWindow) {
            centerIndexes.add(segments.indexOf(segment));
        }
        return centerIndexes;
    }

    public int getOvertonCoefficientSum() {
        int sum = 0;
        for (PopulationSegment segment : segments) {
            sum += segment.getOvertonCoefficient();
        }
        return sum;
    }

    private boolean checkDistribution(List<Integer> overtonIndexes, int toDistribute) {
        for (Integer index : overtonIndexes) {
            if (segments.get(index).getOvertonCoefficient() - toDistribute < 0) return false;
        }
        return true;
    }

    private PopulationSegment getUnifiedCenter() {
        List<PopulationSegment> overtonWindow = getOvertonWindow();
        int windowSize = overtonWindow.size();
        if (windowSize == 1) {
            return overtonWindow.get(0);
        } else if (windowSize == 2) {
            List<Integer> overtonIndexList = getAllWindowIndexes(overtonWindow);
            int unifiedCenterIndex = overtonIndexList.get(0) + ((overtonIndexList.get(1) - overtonIndexList.get(0)) / 2);
            return segments.get(unifiedCenterIndex);
        } else {
            return segments.get(getOvertonCenterIndex(overtonWindow));
        }
    }

    private List<Integer> getSplitIndexes(List<Integer> overtonCenterIndexes) {
        List<Integer> adjustedCenterIndexes = new ArrayList<>();
        for (Integer index : overtonCenterIndexes) {
            int leftSplitIndex = index - 2 < 0 ? segments.size() + (index - 2) : index - 2;
            int rightSplitIndex = index + 2 > segments.size() - 1 ? (index + 2) - segments.size() : index + 2;
            if (!adjustedCenterIndexes.contains(leftSplitIndex)) adjustedCenterIndexes.add(leftSplitIndex);
            if (!adjustedCenterIndexes.contains(rightSplitIndex)) adjustedCenterIndexes.add(rightSplitIndex);
        }
        return adjustedCenterIndexes;
    }

    private void proportionalDistribution(int centerIndex, int distribute) {
        for (PopulationSegment segment : segments) {
            int indexAt = segments.indexOf(segment);
            if (indexAt != centerIndex) {
                segment.incrementCoefficient();
            } else {
                segment.incrementCoefficient(distribute - (segments.size() - 1));
            }
        }
    }

    public void shift(int adjustor, String direction) {
        Iterator<PopulationSegment> shifter = direction.equals("+") ? segments.iterator() : segments.descendingIterator();
        int distribute = 0;
        int centerIndex = getOvertonCenterIndex(getOvertonWindow());
        do {
            PopulationSegment segment = shifter.next();
            int segmentCoefficient = segment.getOvertonCoefficient();
            if (segmentCoefficient == 0) {
                continue;
            }
            if (!shifter.hasNext()) {
                segment.incrementCoefficient(distribute);
            } else {
                boolean anteCenter = (segments.indexOf(segment) <= centerIndex && direction.equals("+"))
                        || (segments.indexOf(segment) >= centerIndex && direction.equals("-"));
                int adjustedCoefficient;
                if (segmentCoefficient <= adjustor && anteCenter) {
                    adjustedCoefficient = 0;
                    distribute += segmentCoefficient;
                } else if (anteCenter) {
                    adjustedCoefficient = segmentCoefficient - adjustor;
                    distribute += adjustor;
                } else {
                    adjustedCoefficient = segmentCoefficient + adjustor;
                    distribute -= adjustor;
                }
                segment.setOvertonCoefficient(adjustedCoefficient, populationIncrement);
            }
        } while (shifter.hasNext());
    }

    public void polarize(int magnitude) {
        int windowSize = getOvertonWindow().size();
        int distribute = (segments.size() - windowSize) * magnitude;
        int fractionalDistribute = distribute % windowSize == 0
                ? distribute / windowSize
                : (distribute - (distribute % windowSize)) / windowSize;
        int diff = distribute - (fractionalDistribute * windowSize);
        List<Integer> overtonIndexList = getAllWindowIndexes(getOvertonWindow());
        int centerCoefficient = getOvertonWindow().get(0).getOvertonCoefficient();
        if (checkDistribution(overtonIndexList, distribute) && centerCoefficient - distribute >= 32) {
            for (PopulationSegment segment : segments) {
                int segmentIndex = segments.indexOf(segment);
                if (overtonIndexList.contains(segmentIndex)) {
                    segment.decrementCoefficient(fractionalDistribute);
                } else if (segment.getOvertonCoefficient() >= 28) {
                    segment.decrementCoefficient(magnitude);
                    diff += (magnitude * 2);
                } else {
                    segment.incrementCoefficient(magnitude);
                }
            }
            segments.get(getOvertonCenterIndex(getOvertonWindow())).incrementCoefficient(diff);
        }
    }

    public void unify(int magnitude) {
        int unifiedCenterIndex = segments.indexOf(getUnifiedCenter());
        int distribute = (segments.size() - 1) * magnitude;
        for (PopulationSegment segment : segments) {
            int segmentCoefficient = segment.getOvertonCoefficient();
            if (segments.indexOf(segment) == unifiedCenterIndex) {
                segment.incrementCoefficient(distribute);
            } else {
                if (segmentCoefficient >= magnitude) {
                    segment.decrementCoefficient(magnitude);
                } else {
                    int difference = Math.abs(segmentCoefficient - magnitude);
                    segment.setOvertonCoefficient(0, populationIncrement);
                    PopulationSegment unifiedCenter = segments.get(unifiedCenterIndex);
                    unifiedCenter.decrementCoefficient(difference);
                }
            }
        }
    }

    public void divide(int divisor) {
        List<Integer> currentCenterIndexes = getAllWindowIndexes(getOvertonWindow());
        List<Integer> adjustedCenterIndexes = getSplitIndexes(currentCenterIndexes);
        int overtonCoefficient = segments.get(currentCenterIndexes.get(0)).getOvertonCoefficient();
        int distribute = divisor == 1
                ? overtonCoefficient - divisor
                : overtonCoefficient % divisor == 0
                ? overtonCoefficient / divisor
                : (overtonCoefficient - (overtonCoefficient % divisor)) / divisor;
        for (Integer centerAt : currentCenterIndexes) {
            PopulationSegment currentCenter = segments.get(centerAt);
            currentCenter.decrementCoefficient(distribute);
        }
        int evenSplit = distribute % 2 == 0 ? distribute / 2 : (distribute - 1) / 2;
        for (Integer index : adjustedCenterIndexes) {
            proportionalDistribution(index, evenSplit);
        }
        if (distribute % 2 != 0) segments.get(getOvertonCenterIndex(getOvertonWindow())).incrementCoefficient();
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
