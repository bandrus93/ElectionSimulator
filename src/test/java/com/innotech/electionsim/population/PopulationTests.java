package com.innotech.electionsim.population;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PopulationTests {
    private Population testPop;
    private final int expectedSum = 256;

    @BeforeMethod(groups = "release")
    public void setup() {
        long populationExpected = 100000L;
        testPop = Population.getInstance(populationExpected);
    }

    @Test(groups = "release")
    public void givenASingleMinorShift_Shift_ShouldShiftPopulationWithoutAffectingTotalPopulationCount() {
        testPop.shift(4, "-");
        Assert.assertEquals(testPop.getGraph().getOvertonCoefficientSum(), expectedSum);
    }

    @Test(groups = "release")
    public void givenASingleMajorShift_Shift_ShouldShiftPopulationWithoutAffectingTotalPopulationCount() {
        testPop.shift(2, "+");
        Assert.assertEquals(testPop.getGraph().getOvertonCoefficientSum(), expectedSum);
    }

    @Test(groups = "release")
    public void givenASingleMinorPolarization_Polarize_ShouldAdjustPopulationWithoutAffectingTotalPopulationCount() {
        testPop.polarize(1);
        Assert.assertEquals(testPop.getGraph().getOvertonCoefficientSum(), expectedSum);
    }

    @Test(groups = "release")
    public void givenASingleMajorPolarization_Polarize_ShouldAdjustPopulationWithoutAffectingTotalPopulationCount() {
        testPop.polarize(2);
        Assert.assertEquals(testPop.getGraph().getOvertonCoefficientSum(), expectedSum);
    }

    @Test(groups = "release")
    public void givenAnyPolarization_Polarize_ShouldFlattenTheCurve() {
        int beginLeftSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int beginRightSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        testPop.polarize(1);
        int alteredLeftSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int alteredRightSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(alteredLeftSlope < beginLeftSlope && alteredRightSlope < beginRightSlope);
        testPop.polarize(2);
        int finalLeftSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int finalRightSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(finalLeftSlope < alteredLeftSlope && finalRightSlope < alteredRightSlope);
    }

    @Test(groups = "release")
    public void givenAnyUnification_Unify_ShouldTightenTheCurve() {
        int beginLeftSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int beginRightSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        testPop.unify(4);
        int alteredLeftSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int alteredRightSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(alteredLeftSlope > beginLeftSlope && alteredRightSlope > beginRightSlope);
        testPop.unify(2);
        int finalLeftSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int finalRightSlope = testPop.getGraph().getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(finalLeftSlope > alteredLeftSlope && finalRightSlope > alteredRightSlope);
    }

    @Test(groups = "release")
    public void givenASingleMinorDivide_Divide_ShouldSplitPopulationWithoutAffectingTotalPopulationCount() {
        testPop.divide(2);
        Assert.assertEquals(testPop.getGraph().getOvertonCoefficientSum(), expectedSum);
    }

    @Test(groups = "release")
    public void givenASingleMajorDivide_Divide_ShouldSplitPopulationWithoutAffectingTotalPopulationCount() {
        testPop.divide(1);
        Assert.assertEquals(testPop.getGraph().getOvertonCoefficientSum(), expectedSum);
    }

}
