package com.innotech.electionsim.modeltests;

import com.innotech.electionsim.model.Population;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PopulationTests {
    private Population testPop;
    private final int expectedSum = 256;

    @BeforeMethod
    public void setup() {
        long populationExpected = 100000L;
        testPop = Population.getInstance(populationExpected);
    }

    @Test
    public void givenASingleMinorShift_Shift_ShouldShiftPopulationWithoutAffectingTotalPopulationCount() {
        testPop.shift(4, "-");
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenASingleMajorShift_Shift_ShouldShiftPopulationWithoutAffectingTotalPopulationCount() {
        testPop.shift(2, "+");
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenASingleMinorPolarization_Polarize_ShouldAdjustPopulationWithoutAffectingTotalPopulationCount() {
        testPop.polarize(1);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenASingleMajorPolarization_Polarize_ShouldAdjustPopulationWithoutAffectingTotalPopulationCount() {
        testPop.polarize(2);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenAnyPolarization_Polarize_ShouldFlattenTheCurve() {
        int beginLeftSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int beginRightSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        testPop.polarize(1);
        int alteredLeftSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int alteredRightSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(alteredLeftSlope < beginLeftSlope && alteredRightSlope < beginRightSlope);
        testPop.polarize(2);
        int finalLeftSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int finalRightSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(finalLeftSlope < alteredLeftSlope && finalRightSlope < alteredRightSlope);
    }

    @Test
    public void givenAnyUnification_Unify_ShouldTightenTheCurve() {
        int beginLeftSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int beginRightSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        testPop.unify(4);
        int alteredLeftSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int alteredRightSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(alteredLeftSlope > beginLeftSlope && alteredRightSlope > beginRightSlope);
        testPop.unify(2);
        int finalLeftSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(0).getOvertonCoefficient();
        int finalRightSlope = testPop.getOvertonWindow().get(0).getOvertonCoefficient() - testPop.getSegments().get(8).getOvertonCoefficient();
        Assert.assertTrue(finalLeftSlope > alteredLeftSlope && finalRightSlope > alteredRightSlope);
    }

    @Test
    public void givenASingleMinorDivide_Divide_ShouldSplitPopulationWithoutAffectingTotalPopulationCount() {
        testPop.divide(2);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenASingleMajorDivide_Divide_ShouldSplitPopulationWithoutAffectingTotalPopulationCount() {
        testPop.divide(1);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

}
