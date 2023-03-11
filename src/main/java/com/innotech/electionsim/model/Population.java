package com.innotech.electionsim.model;

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

    private Population(long registeredVoters) {
        this.registeredVoters = registeredVoters;
    }

    public static Population getInstance(long registeredVoters) {
        Population population = new Population(registeredVoters);
        population.segments.add(new PopulationSegment(Segment.RADICAL_LEFT, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.FAR_LEFT, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.MODERATE_LEFT, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.CENTRE_LEFT, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.CENTRIST, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.CENTRE_RIGHT, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.MODERATE_RIGHT, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.FAR_RIGHT, registeredVoters));
        population.segments.add(new PopulationSegment(Segment.RADICAL_RIGHT, registeredVoters));
        return population;
    }

    public Long getTotalPopulation() {
        return registeredVoters;
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

    private PopulationSegment getLeftBound(PopulationSegment fromCenter) {
        PopulationSegment windowBoundary = getSegment(Segment.RADICAL_LEFT);
        for (int i = segments.indexOf(fromCenter) - 1; i > -1; i--) {
            PopulationSegment candidate = segments.get(i);
            if (!candidate.equals(windowBoundary)) {
                if (candidate.getBlockBase() > 0 && segments.get(segments.indexOf(candidate) - 1).getBlockBase() == 0) {
                    return candidate;
                }
            }
        }
        return windowBoundary;
    }

    private Long getLeftTotal() {
        long leftTotal = 0L;
        for (int i = 3; i >= 0; i--) {
            leftTotal += segments.get(i).getBlockBase();
        }
        return leftTotal;
    }

    private PopulationSegment getRightBound(PopulationSegment fromCenter) {
        PopulationSegment windowBoundary = getSegment(Segment.RADICAL_RIGHT);
        for (int i = segments.indexOf(fromCenter) + 1; i < segments.size(); i++) {
            PopulationSegment candidate = segments.get(i);
            if (!candidate.equals(windowBoundary)) {
                if (candidate.getBlockBase() > 0 && segments.get(segments.indexOf(candidate) + 1).getBlockBase() == 0) {
                    return candidate;
                }
            }
        }
        return windowBoundary;
    }

    private Long getRightTotal() {
        long rightTotal = 0L;
        for (int i = 5; i < segments.size(); i++) {
            rightTotal += segments.get(i).getBlockBase();
        }
        return rightTotal;
    }

    private double getOvertonCoefficientSum() {
        double sum = 0.0;
        for (PopulationSegment segment : segments) {
            sum += segment.getOvertonCoefficient();
        }
        return sum;
    }

    public void shiftLeft(double adjustor) {
        double shifted = 0.0;
        for (int i = segments.size() - 1; i >= 0; i--) {
            PopulationSegment segment = segments.get(i);
            double segmentCoefficient = segment.getOvertonCoefficient();
            if (i == segments.size() - 1 && segmentCoefficient < 4) {
                segment.setOvertonCoefficient(0.0);
                shifted += segmentCoefficient;
            } else {
                double preAdjusted = segmentCoefficient + shifted;
                shifted = 0.0;
                double toShift = Math.round(preAdjusted / adjustor);
                segment.setOvertonCoefficient(preAdjusted - toShift);
                shifted += toShift;
            }
            if (i == 0 && getOvertonCoefficientSum() > 256) {
                double shiftCorrection = getOvertonCoefficientSum() - 256;
                segment.setOvertonCoefficient(segment.getOvertonCoefficient() - shiftCorrection);
            }
        }
    }

    public void shiftRight(double adjustor) {
        double shifted = 0.0;
        for (int i = 0; i <= segments.size() - 1; i++) {
            PopulationSegment segment = segments.get(i);
            double segmentCoefficient = segment.getOvertonCoefficient();
            if (i == 0 && segmentCoefficient < 4) {
                segment.setOvertonCoefficient(0.0);
                shifted += segmentCoefficient;
            } else {
                double preAdjusted = segmentCoefficient + shifted;
                shifted = 0.0;
                double toShift = Math.round(preAdjusted / adjustor);
                segment.setOvertonCoefficient(preAdjusted - toShift);
                shifted += toShift;
            }
            if (i == segments.size() - 1 && getOvertonCoefficientSum() > 256) {
                double shiftCorrection = getOvertonCoefficientSum() - 256;
                segment.setOvertonCoefficient(segment.getOvertonCoefficient() - shiftCorrection);
            }
        }
    }

    public void polarize(String direction, double magnitude) {
        Long registeredVoters = getTotalPopulation();
        long affectedVoters = Math.round(registeredVoters * magnitude);
        PopulationSegment currentCenter = getOvertonCenter();
        double polarityBound = currentCenter.getBlockBase() / Double.parseDouble(registeredVoters.toString());
        if (polarityBound > 0.112 && polarityBound < 0.8) {
            if (direction.equals("IN")) {
                long totalAffectedVoters = 0L;
                for (PopulationSegment currentSegment : segments) {
                    if (!currentSegment.equals(currentCenter)) {
                        long adjustedBase = currentSegment.getBlockBase() - affectedVoters;
                        if (adjustedBase >= 0) {
                            currentSegment.setBlockBase(adjustedBase);
                        } else {
                            currentSegment.setBlockBase(0);
                        }
                        totalAffectedVoters += affectedVoters;
                    }
                }
                long centerPush = Math.round(totalAffectedVoters * .8);
                long centerDrift = Math.round((totalAffectedVoters - centerPush) / 2.0);
                PopulationSegment leftOfCenter = segments.get(segments.indexOf(currentCenter) - 1);
                PopulationSegment rightOfCenter = segments.get(segments.indexOf(currentCenter) + 1);
                currentCenter.setBlockBase(currentCenter.getBlockBase() + centerPush);
                leftOfCenter.setBlockBase(leftOfCenter.getBlockBase() + centerDrift);
                rightOfCenter.setBlockBase(rightOfCenter.getBlockBase() + centerDrift);
            } else {
                for (PopulationSegment currentSegment : segments) {
                    if (currentSegment.getBlockBase() / Double.parseDouble(registeredVoters.toString()) < .112) {
                        currentSegment.setBlockBase(currentSegment.getBlockBase() + affectedVoters);
                    } else {
                        currentSegment.setBlockBase(currentSegment.getBlockBase() - affectedVoters);
                    }
                }
            }
            if (registeredVoters - getTotalPopulation() < 0) {
                long voterOverflow = Math.abs(registeredVoters - getTotalPopulation());
                currentCenter.setBlockBase(currentCenter.getBlockBase() - voterOverflow);
            } else if (registeredVoters - getTotalPopulation() > 0) {
                long voterUnderflow = registeredVoters - getTotalPopulation();
                polarCorrection(voterUnderflow, direction);
            }
        }
    }

    public void divide(int divisor) {
        Long registeredVoters = getTotalPopulation();
        PopulationSegment centerAt = getOvertonCenter();
        long affectedVoters = divisor > 1 ? centerAt.getBlockBase() : centerAt.getBlockBase() / 2;
        long leftDistributable = affectedVoters / 2;
        long rightDistributable = affectedVoters / 2;
        for (int i = segments.indexOf(centerAt) - 1; i > -1; i--) {
            long distributed = proportionalDistribution(leftDistributable, segments.get(i));
            leftDistributable -= distributed;
        }
        for (int i = segments.indexOf(centerAt) + 1; i < segments.size(); i++) {
            long distributed = proportionalDistribution(rightDistributable, segments.get(i));
            rightDistributable -= distributed;
        }
        long adjustedCenter = centerAt.getBlockBase() - affectedVoters;
        if (adjustedCenter >= 0) {
            centerAt.setBlockBase(adjustedCenter);
        } else {
            centerAt.setBlockBase(0L);
            fringeDistribution(Math.abs(adjustedCenter), centerAt);
        }
        do {
            if (getTotalPopulation() > registeredVoters) {
                long voterOverflow = getTotalPopulation() - registeredVoters;
                fringeCorrection(voterOverflow, centerAt);
            } else if (getTotalPopulation() < registeredVoters) {
                long voterUnderflow = registeredVoters - getTotalPopulation();
                centerRedistribution(voterUnderflow, centerAt);
            }
        } while (!Objects.equals(getTotalPopulation(), registeredVoters));
        StringBuilder debugOutput = new StringBuilder();
        for (PopulationSegment segment : segments) {
            debugOutput.append(segment.getVoterBlock()).append(": ").append(segment.getBlockBase()).append("\n");
        }
        System.out.println(debugOutput);
    }

    private long proportionalDistribution(long distributable, PopulationSegment segment) {
        double broadTotal = segments.indexOf(segment) < 4 ? Double.parseDouble(getLeftTotal().toString()) : Double.parseDouble(getRightTotal().toString());
        long votesReceivable = Math.round(
                distributable * (1 - (segment.getBlockBase() / broadTotal)));
        segment.setBlockBase(segment.getBlockBase() + votesReceivable);
        return votesReceivable;
    }

    private void fringeDistribution(long voterOverflow, PopulationSegment fromCenter) {
        if (!Segment.RADICAL_LEFT.equals(getLeftBound(fromCenter).getVoterBlock())) {
            segments.get(segments.indexOf(getLeftBound(fromCenter)) - 1).setBlockBase(voterOverflow / 2);
        } else {
            getSegment(Segment.RADICAL_LEFT).setBlockBase(voterOverflow / 2);
        }
        if (!Segment.RADICAL_RIGHT.equals(getRightBound(fromCenter).getVoterBlock())) {
            segments.get(segments.indexOf(getRightBound(fromCenter)) + 1).setBlockBase(voterOverflow / 2);
        } else {
            getSegment(Segment.RADICAL_RIGHT).setBlockBase(voterOverflow / 2);
        }
    }

    private void centerRedistribution(long voterUnderflow, PopulationSegment fromCenter) {
        PopulationSegment leftOfCenter = segments.get(segments.indexOf(fromCenter) - 1);
        PopulationSegment rightOfCenter = segments.get(segments.indexOf(fromCenter) + 1);
        if (voterUnderflow % 2 == 0) {
            long distributable = voterUnderflow / 2;
            leftOfCenter.setBlockBase(leftOfCenter.getBlockBase() + distributable);
            rightOfCenter.setBlockBase(rightOfCenter.getBlockBase() + distributable);
        } else {
            long lessor = voterUnderflow / 2;
            long greater = (voterUnderflow / 2) + 1;
            leftOfCenter.setBlockBase(leftOfCenter.getBlockBase() + lessor);
            rightOfCenter.setBlockBase(rightOfCenter.getBlockBase() + greater);
        }
    }

    private void polarCorrection(long voterUnderflow, String direction) {
        if (direction.equals("IN")) {
            centerRedistribution(voterUnderflow, getOvertonCenter());
        } else {
            fringeRedistribution(voterUnderflow, getOvertonCenter());
        }
    }

    private void fringeCorrection(long voterOverflow, PopulationSegment fromCenter) {
        if (!Segment.RADICAL_LEFT.equals(getLeftBound(fromCenter).getVoterBlock())) {
            PopulationSegment leftBound = segments.get(segments.indexOf(getLeftBound(fromCenter)) - 1);
            leftBound.setBlockBase(leftBound.getBlockBase() - (voterOverflow / 2));
        } else {
            getSegment(Segment.RADICAL_LEFT).setBlockBase(getSegment(Segment.RADICAL_LEFT).getBlockBase() - (voterOverflow / 2));
        }
        if (!Segment.RADICAL_RIGHT.equals(getRightBound(fromCenter).getVoterBlock())) {
            PopulationSegment rightBound = segments.get(segments.indexOf(getRightBound(fromCenter)) + 1);
            rightBound.setBlockBase(rightBound.getBlockBase() - (voterOverflow / 2));
        } else {
            getSegment(Segment.RADICAL_RIGHT).setBlockBase(getSegment(Segment.RADICAL_RIGHT).getBlockBase() - (voterOverflow / 2));
        }
    }

    private void fringeRedistribution(long voterUnderflow, PopulationSegment fromCenter) {
        PopulationSegment leftBound;
        PopulationSegment rightBound;
        if (!Segment.RADICAL_LEFT.equals(getLeftBound(fromCenter).getVoterBlock())) {
            leftBound = segments.get(segments.indexOf(getLeftBound(fromCenter)) - 1);
        } else {
            leftBound = getSegment(Segment.RADICAL_LEFT);
        }
        if (!Segment.RADICAL_RIGHT.equals(getRightBound(fromCenter).getVoterBlock())) {
            rightBound = segments.get(segments.indexOf(getRightBound(fromCenter)) + 1);
        } else {
            rightBound = getSegment(Segment.RADICAL_RIGHT);
        }
        if (voterUnderflow % 2 == 0) {
            long distributable = voterUnderflow / 2;
            leftBound.setBlockBase(leftBound.getBlockBase() + distributable);
            rightBound.setBlockBase(rightBound.getBlockBase() + distributable);
        } else {
            long lessor = voterUnderflow / 2;
            long greater = (voterUnderflow / 2) + 1;
            leftBound.setBlockBase(leftBound.getBlockBase() + lessor);
            rightBound.setBlockBase(rightBound.getBlockBase() + greater);
        }
    }
}
