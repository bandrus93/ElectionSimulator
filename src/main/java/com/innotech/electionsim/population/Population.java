package com.innotech.electionsim.population;

import com.innotech.electionsim.election.ElectionSettings;
import com.innotech.electionsim.interfaces.Editable;
import com.innotech.electionsim.view.UserInterface;
import com.innotech.electionsim.view.DisplayManager;

import java.util.*;

public class Population implements Editable {
    public enum Segment {
        RADICAL_RIGHT,
        FAR_RIGHT,
        MODERATE_RIGHT,
        CENTRE_RIGHT,
        CENTRIST,
        CENTRE_LEFT,
        MODERATE_LEFT,
        FAR_LEFT,
        RADICAL_LEFT,
        INDEPENDENT
    }

    private final long registeredVoters;
    private final ElectionSettings.PopulationBias bias;
    private final PopulationGraph graph;

    private Population(long registeredVoters, ElectionSettings.PopulationBias bias) {
        this.registeredVoters = registeredVoters;
        graph = PopulationGraph.getInstance(registeredVoters);
        this.bias = bias;
    }

    public static Population getInstance(long registeredVoters, ElectionSettings.PopulationBias bias) {
        return new Population(registeredVoters, bias);
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

    public PopulationSegment getSwingSegment() {
        return graph.getSegment(Segment.INDEPENDENT);
    }

    @Override
    public void edit() {
        do {
            switch (UserInterface.getStringInput(graph + UserInterface.POPULATION_EDIT_PROMPT)) {
                case "l": shift(4, "-"); break;
                case "r": shift(4, "+"); break;
                case "L": shift(2, "-"); break;
                case "R": shift(2, "+"); break;
                case "p": polarize(2); break;
                case "P": polarize(1); break;
                case "u": unify(4); break;
                case "U": unify(2);
                case "d": divide(4); break;
                case "D": divide(2); break;
                case "f": return;
                default:
                    System.out.println("Invalid command");
            }
            DisplayManager.refresh(graph + UserInterface.POPULATION_EDIT_PROMPT);
        } while (true);
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
