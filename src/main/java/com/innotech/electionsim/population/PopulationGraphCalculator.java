package com.innotech.electionsim.population;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PopulationGraphCalculator {
    public static final MathContext coefficientContext = new MathContext(8, RoundingMode.DOWN);

    public static double round(Double value) {
        String val = value.toString();
        return Double.parseDouble(val.substring(0, val.indexOf('.')));
    }

    public static long increment(long value) {
        return new BigDecimal(value / 256.0, coefficientContext).longValue();
    }

    public static long computeBase(double coefficient, long increment) {
        return new BigDecimal(coefficient * increment, coefficientContext).longValue();
    }

}
