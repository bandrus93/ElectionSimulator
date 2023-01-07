package com.innotech.electionsim.modeltests;

import com.innotech.electionsim.model.Population;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class PopulationTests {
    private Population testPop;
    private final long populationExpected = 10000L;

    @BeforeTest
    public void setup() {
        testPop = new Population(populationExpected);
    }

    @Test
    public void shiftShouldShiftPopulationWithoutAffectingTotalPopulationCountGivenASingleMinorShift() {
        testPop.shift("LEFT", 0.013);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

    @Test
    public void shiftShouldShiftPopulationWithoutAffectingTotalPopulationCountGivenASingleMajorShift() {
        testPop.shift("RIGHT", 0.05);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

    @Test
    public void polarizeShouldAdjustPopulationWithoutAffectingTotalPopulationCountGivenASingleMinorPolarizationIn() {
        testPop.polarize("IN", 0.013);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

    @Test
    public void polarizeShouldAdjustPopulationWithoutAffectingTotalPopulationCountGivenASingleMajorPolarizationIn() {
        testPop.polarize("IN", 0.05);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

    @Test
    public void polarizeShouldAdjustPopulationWithoutAffectingTotalPopulationCountGivenASingleMinorPolarizationOut() {
        testPop.polarize("OUT", 0.013);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

    @Test
    public void polarizeShouldAdjustPopulationWithoutAffectingTotalPopulationCountGivenASingleMajorPolarizationOut() {
        testPop.polarize("OUT", 0.05);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

    @Test
    public void divideShouldSplitPopulationWithoutAffectingTotalPopulationCountGivenASingleMinorDivide() {
        testPop.divide(1);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

    @Test
    public void divideShouldSplitPopulationWithoutAffectingTotalPopulationCountGivenASingleMajorDivide() {
        testPop.divide(2);
        Assert.assertEquals(testPop.getTotalPopulation(), populationExpected);
    }

}
