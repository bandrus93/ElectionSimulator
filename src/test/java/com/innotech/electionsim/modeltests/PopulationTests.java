package com.innotech.electionsim.modeltests;

import com.innotech.electionsim.model.Population;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PopulationTests {
    private Population testPop;
    private final double expectedSum = 256.0;

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
        testPop.polarize(4);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenASingleMajorPolarization_Polarize_ShouldAdjustPopulationWithoutAffectingTotalPopulationCount() {
        testPop.polarize(2);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenASingleMinorDivide_Divide_ShouldSplitPopulationWithoutAffectingTotalPopulationCount() {
        testPop.divide(1);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

    @Test
    public void givenASingleMajorDivide_Divide_ShouldSplitPopulationWithoutAffectingTotalPopulationCount() {
        testPop.divide(2);
        Assert.assertEquals(testPop.getOvertonCoefficientSum(), expectedSum);
    }

}
