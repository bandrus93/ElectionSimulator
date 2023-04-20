package com.innotech.electionsim.population;

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

    private final long registeredVoters;
    private final Bias bias = Bias.RIGHT;
    private final PopulationGraph graph;

    private Population(long registeredVoters) {
        this.registeredVoters = registeredVoters;
        graph = PopulationGraph.getInstance(registeredVoters);
    }

    public static Population getInstance(long registeredVoters) {
        return new Population(registeredVoters);
    }

    public Long getTotalPopulation() {
        return registeredVoters;
    }

    public static Segment[] getSegmentArray() {
        return Segment.values();
    }

    public PopulationGraph getGraph() {
        return graph;
    }

    public LinkedList<PopulationSegment> getSegments() {
        return graph.getSegments();
    }

    public void shift(int adjustor, String direction) {
        Iterator<PopulationSegment> shifter = direction.equals("+")
                ? graph.getSegments().iterator() : graph.getSegments().descendingIterator();
        int distribute = 0;
        int centerIndex = graph.getOvertonCenterIndex(graph.getOvertonWindow(), bias);
        do {
            PopulationSegment segment = shifter.next();
            int segmentCoefficient = segment.getOvertonCoefficient();
            if (segmentCoefficient == 0) {
                continue;
            }
            if (!shifter.hasNext()) {
                segment.incrementCoefficient(distribute);
            } else {
                boolean anteCenter = (graph.getSegments().indexOf(segment) <= centerIndex && direction.equals("+"))
                        || (graph.getSegments().indexOf(segment) >= centerIndex && direction.equals("-"));
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
                segment.setOvertonCoefficient(adjustedCoefficient, graph.getPopulationIncrement());
            }
        } while (shifter.hasNext());
    }

    public void polarize(int magnitude) {
        int windowSize = graph.getOvertonWindow().size();
        int distribute = (graph.getSegments().size() - windowSize) * magnitude;
        int fractionalDistribute = distribute % windowSize == 0
                ? distribute / windowSize
                : (distribute - (distribute % windowSize)) / windowSize;
        int diff = distribute - (fractionalDistribute * windowSize);
        List<Integer> overtonIndexList = graph.getAllWindowIndexes(graph.getOvertonWindow());
        int centerCoefficient = graph.getOvertonWindow().get(0).getOvertonCoefficient();
        if (graph.checkDistribution(overtonIndexList, distribute) && centerCoefficient - distribute >= 32) {
            for (PopulationSegment segment : graph.getSegments()) {
                int segmentIndex = graph.getSegments().indexOf(segment);
                if (overtonIndexList.contains(segmentIndex)) {
                    segment.decrementCoefficient(fractionalDistribute);
                } else if (segment.getOvertonCoefficient() >= 28) {
                    segment.decrementCoefficient(magnitude);
                    diff += (magnitude * 2);
                } else {
                    segment.incrementCoefficient(magnitude);
                }
            }
            graph.getSegments().get(graph.getOvertonCenterIndex(graph.getOvertonWindow(), bias)).incrementCoefficient(diff);
        }
    }

    public void unify(int magnitude) {
        int unifiedCenterIndex = graph.getSegments().indexOf(graph.getUnifiedCenter(bias));
        int distribute = (graph.getSegments().size() - 1) * magnitude;
        for (PopulationSegment segment : graph.getSegments()) {
            int segmentCoefficient = segment.getOvertonCoefficient();
            if (graph.getSegments().indexOf(segment) == unifiedCenterIndex) {
                segment.incrementCoefficient(distribute);
            } else {
                if (segmentCoefficient >= magnitude) {
                    segment.decrementCoefficient(magnitude);
                } else {
                    int difference = Math.abs(segmentCoefficient - magnitude);
                    segment.setOvertonCoefficient(0, graph.getPopulationIncrement());
                    PopulationSegment unifiedCenter = graph.getSegments().get(unifiedCenterIndex);
                    unifiedCenter.decrementCoefficient(difference);
                }
            }
        }
    }

    public void divide(int divisor) {
        List<Integer> currentCenterIndexes = graph.getAllWindowIndexes(graph.getOvertonWindow());
        List<Integer> adjustedCenterIndexes = graph.getSplitIndexes(currentCenterIndexes);
        int overtonCoefficient = graph.getSegments().get(currentCenterIndexes.get(0)).getOvertonCoefficient();
        int distribute = divisor == 1
                ? overtonCoefficient - divisor
                : overtonCoefficient % divisor == 0
                ? overtonCoefficient / divisor
                : (overtonCoefficient - (overtonCoefficient % divisor)) / divisor;
        for (Integer centerAt : currentCenterIndexes) {
            PopulationSegment currentCenter = graph.getSegments().get(centerAt);
            currentCenter.decrementCoefficient(distribute);
        }
        int evenSplit = distribute % 2 == 0 ? distribute / 2 : (distribute - 1) / 2;
        for (Integer index : adjustedCenterIndexes) {
            graph.proportionalDistribution(index, evenSplit);
        }
        if (distribute % 2 != 0) graph.getSegments().get(graph.getOvertonCenterIndex(graph.getOvertonWindow(), bias)).incrementCoefficient();
    }

}
