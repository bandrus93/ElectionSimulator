package com.innotech.electionsim.view;

import com.innotech.electionsim.model.ElectionResult;

import java.util.Calendar;
import java.util.List;

public class ResultListView {
    private static final String EMPTY_SAVE_MESSAGE = "No saved elections found.";
    private static final String VIEW_RESULT_PROMPT = "Enter the number corresponding to a saved election to view that election's outcome (or enter 'x' to return to the main menu):";
    private final String view;

    private ResultListView(String listToDisplay) {
        view = listToDisplay;
    }

    public static ResultListView getInstance(List<ElectionResult> resultList) {
        return new ResultListView(getResultListAsString(resultList) + VIEW_RESULT_PROMPT);
    }

    private static String getResultListAsString(List<ElectionResult> resultList) {
        if (resultList.isEmpty()) {
            return EMPTY_SAVE_MESSAGE;
        } else {
            StringBuilder menuBuilder = new StringBuilder();
            for (ElectionResult result : resultList) {
                String electionYear = result.getMetaData().getElectionDay().equals("NULL") ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) : result.getMetaData().getElectionDay().split("-")[2];
                menuBuilder.append(resultList.indexOf(result) + 1)
                        .append(" - ")
                        .append(result.getMetaData().getLocale())
                        .append(" ")
                        .append(result.getMetaData().getRace())
                        .append(" Election ")
                        .append(electionYear)
                        .append("\n");
            }
            return menuBuilder.toString();
        }
    }

    public String getView() {
        return view;
    }
}
