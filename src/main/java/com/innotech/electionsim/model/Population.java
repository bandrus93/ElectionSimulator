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

    private final LinkedList<PopulationSegment> segments = new LinkedList<>();
    private final long registeredVoters;
    private long populationIncrement;

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

    private PopulationSegment getOvertonCenter() {
        PopulationSegment currentCenter = getSegment(Segment.CENTRIST);
        for (PopulationSegment candidate : segments) {
            if (candidate.getBlockBase() > currentCenter.getBlockBase()) {
                currentCenter = candidate;
            }
        }
        return currentCenter;
    }

    public double getOvertonCoefficientSum() {
        double sum = 0.0;
        for (PopulationSegment segment : segments) {
            sum += segment.getOvertonCoefficient();
        }
        return sum;
    }

    private void checkSum(PopulationSegment segment) {
        if (getOvertonCoefficientSum() > 256) {
            double shiftCorrection = getOvertonCoefficientSum() - 256;
            segment.setOvertonCoefficient(segment.getOvertonCoefficient() - shiftCorrection, populationIncrement);
        } else if (getOvertonCoefficientSum() < 256) {
            double difference = 256 - getOvertonCoefficientSum();
            PopulationSegment center = getOvertonCenter();
            center.setOvertonCoefficient(center.getOvertonCoefficient() + difference, populationIncrement);
        }
    }

    public void shift(double adjustor, String direction) {
        Iterator<PopulationSegment> shifter = direction.equals("+") ? segments.iterator() : segments.descendingIterator();
        double shifted = 0.0;
        do {
            PopulationSegment segment = shifter.next();
            double segmentCoefficient = segment.getOvertonCoefficient();
            if (segmentCoefficient < adjustor) {
                segment.setOvertonCoefficient(0.0, populationIncrement);
                shifted += segmentCoefficient;
            } else {
                double preAdjusted = segmentCoefficient + shifted;
                shifted = 0.0;
                double toShift = PopulationGraphCalculator.round(preAdjusted / adjustor);
                segment.setOvertonCoefficient(preAdjusted - toShift, populationIncrement);
                shifted += toShift;
            }
            if (!shifter.hasNext()) {
                checkSum(segment);
            }
        } while (shifter.hasNext());
    }

    private void pushLeft(double magnitude, PopulationSegment overtonCenter) {
        double pushed = 0.0;
        int boundary = segments.indexOf(overtonCenter);
        for (int i = segments.size() - 1; i >= boundary; i--) {
            PopulationSegment segment = segments.get(i);
            double segmentCoefficient = segment.getOvertonCoefficient();
            if (i == segments.size() - 1 && segmentCoefficient < magnitude) {
                segment.setOvertonCoefficient(0.0, populationIncrement);
                pushed += segmentCoefficient;
            } else {
                double preAdjusted = segmentCoefficient + pushed;
                pushed = 0.0;
                double toPush = PopulationGraphCalculator.round(preAdjusted / magnitude);
                segment.setOvertonCoefficient(preAdjusted - toPush, populationIncrement);
                pushed += toPush;
            }
        }
    }

    private void pushRight(double magnitude, PopulationSegment overtonCenter) {
        double pushed = 0.0;
        int boundary = segments.indexOf(overtonCenter);
        for (int i = 0; i <= boundary; i++) {
            PopulationSegment segment = segments.get(i);
            double segmentCoefficient = segment.getOvertonCoefficient();
            if (i == 0 && segmentCoefficient < magnitude) {
                segment.setOvertonCoefficient(0.0, populationIncrement);
                pushed += segmentCoefficient;
            } else {
                double preAdjusted = segmentCoefficient + pushed;
                pushed = 0.0;
                double toPush = PopulationGraphCalculator.round(preAdjusted / magnitude);
                segment.setOvertonCoefficient(preAdjusted - toPush, populationIncrement);
                pushed += toPush;
            }
        }
    }

    public void polarize(double magnitude) {
        PopulationSegment overtonCenter = getOvertonCenter();
        pushLeft(magnitude, overtonCenter);
        pushRight(magnitude, overtonCenter);
        double centerAdjust = 256 - getOvertonCoefficientSum();
        overtonCenter.setOvertonCoefficient(overtonCenter.getOvertonCoefficient() + centerAdjust, populationIncrement);
    }

    private void distributeLeft(double distributable, PopulationSegment overtonCenter) {
        overtonCenter.setOvertonCoefficient(overtonCenter.getOvertonCoefficient() - distributable, populationIncrement);
        double toDistribute = distributable;
        int receivers = segments.indexOf(overtonCenter);
        double receiverDistribution = PopulationGraphCalculator.round(toDistribute / receivers);
        for (int i = receivers; i >= 0; i--) {
            PopulationSegment segment = segments.get(i);
            if (i != 0) {
                segment.setOvertonCoefficient(segment.getOvertonCoefficient() + receiverDistribution, populationIncrement);
                toDistribute -= receiverDistribution;
            } else {
                segment.setOvertonCoefficient(segment.getOvertonCoefficient() + toDistribute, populationIncrement);
            }
        }
    }

    private void distributeRight(double distributable, PopulationSegment overtonCenter) {
        overtonCenter.setOvertonCoefficient(overtonCenter.getOvertonCoefficient() - distributable, populationIncrement);
        double toDistribute = distributable;
        int receivers = (segments.size() - 1) - segments.indexOf(overtonCenter);
        double receiverDistribution = PopulationGraphCalculator.round(toDistribute / receivers);
        for (int i = receivers; i <= segments.size() - 1; i++) {
            PopulationSegment segment = segments.get(i);
            if (i != segments.size() - 1) {
                segment.setOvertonCoefficient(segment.getOvertonCoefficient() + receiverDistribution, populationIncrement);
                toDistribute -= receiverDistribution;
            } else {
                segment.setOvertonCoefficient(segment.getOvertonCoefficient() + toDistribute, populationIncrement);
            }
        }
    }

    public void divide(int divisor) {
        PopulationSegment overtonCenter = getOvertonCenter();
        double centerCoefficient = overtonCenter.getOvertonCoefficient();
        double distributable = centerCoefficient % 2 == 0
                ? PopulationGraphCalculator.round(centerCoefficient / divisor)
                : PopulationGraphCalculator.round((centerCoefficient - 1) / divisor);
        distributeLeft(distributable, overtonCenter);
        distributeRight(distributable, overtonCenter);
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
