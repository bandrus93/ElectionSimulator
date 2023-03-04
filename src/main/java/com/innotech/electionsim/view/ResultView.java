package com.innotech.electionsim.view;

import com.innotech.electionsim.model.Candidate;
import com.innotech.electionsim.model.ElectionResult;
import com.innotech.electionsim.model.ElectionSettings;

import java.util.Calendar;
import java.util.List;

public class ResultView {
    private static final StringBuilder viewBuilder = new StringBuilder();
    private final String view;

    private ResultView(String toDisplay) {
        view = toDisplay;
    }

    public static ResultView getInstance(ElectionResult result) {
        if (!viewBuilder.isEmpty()) {
            viewBuilder.delete(0, viewBuilder.length());
        }
        return new ResultView(viewBuilder.append(getTableHeader(result.getMetaData())).append(getTableRows(result)).toString());
    }

    private static String getTableHeader(ElectionSettings settings) {
        StringBuilder headerBuilder = new StringBuilder();
        String electionYear = settings.getElectionDay().equals("NULL") ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) : settings.getElectionDay().split("-")[2];
        String title = settings.getLocale() + " " + settings.getRace() + " Election " + electionYear + "\n";
        String header = String.format("%-30.30s %-30.30s %-30.30s\n", "Candidate", "Popular Votes", "Percentage of Total");
        return headerBuilder.append(title).append(header).toString();
    }

    private static String getTableRows(ElectionResult result) {
        StringBuilder rowBuilder = new StringBuilder();
        List<Candidate> rankings = result.getCandidateRanking();
        for (int i = rankings.size() - 1; i >= 0; i--) {
            Candidate next = rankings.get(i);
            String candidateId = (rankings.size() - i) + " - " + next.getName();
            String popularVotes = next.getTotalVotes().toString();
            double percentage = (next.getTotalVotes() / Double.parseDouble(result.getTotalPopulation().toString())) * 100;
            String row = String.format("%-30.30s %-30.30s %-30.30s %-20.20s\n", candidateId, popularVotes, percentage, getVoteMeter(percentage));
            rowBuilder.append(row);
        }
        return rowBuilder.toString();
    }

    private static String getVoteMeter(double percentageOfVote) {
        StringBuilder meterBuilder = new StringBuilder();
        double tallyCount = percentageOfVote / 5;
        int percentCounter = (int) tallyCount;
        for (; percentCounter > 0; percentCounter--) {
            meterBuilder.append("*");
        }
        return meterBuilder.toString();
    }

    public void display() {
        System.out.println(view);
    }
}
