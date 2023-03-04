package com.innotech.electionsim.view;

import com.innotech.electionsim.model.Population;
import com.innotech.electionsim.model.PopulationSegment;

import java.util.Locale;

public class PopulationView {
    private static final StringBuilder viewBuilder = new StringBuilder();
    private final String view;

    private PopulationView(String toDisplay) {
        view = toDisplay;
    }

    public static PopulationView getInstance(Population population) {
        return new PopulationView(getGraphRows(population));
    }

    private static String getGraphRows(Population population) {
        for (PopulationSegment currentSegment : population.getSegments()) {
            viewBuilder.append(formatGraphLabel(currentSegment.getVoterBlock().toString())).append("|");
            long interval = Math.floorDiv(Math.round((currentSegment.getBlockBase() / Double.parseDouble(population.getTotalPopulation().toString())) * 100), 5);
            for (int j = 0; j < interval; j++) {
                viewBuilder.append("*");
            }
            viewBuilder.append("\n");
        }
        return viewBuilder.toString();
    }

    private static String formatGraphLabel(String fullName) {
        StringBuilder sb = new StringBuilder();
        String[] labelData = fullName.split("_");
        for (String labelDatum : labelData) {
            sb.append(labelDatum.toUpperCase(Locale.ROOT).charAt(0));
            if (labelData.length == 1) {
                sb.append("T");
            }
        }
        return sb.toString();
    }

    public String getView() {
        return view;
    }

    public void display() {
        System.out.println(view);
    }
}
