package com.innotech.electionsim.view;

import com.innotech.electionsim.candidate.Candidate;
import com.innotech.electionsim.election.ElectionResult;
import com.innotech.electionsim.election.ElectionSettings;

import java.util.Calendar;
import java.util.List;

public class ResultView {
    private static final StringBuilder viewBuilder = new StringBuilder();

    private ResultView() {

    }

    public static String getInstance(ElectionResult result) {
        if (!viewBuilder.isEmpty()) {
            viewBuilder.delete(0, viewBuilder.length());
        }
        return viewBuilder.append(getTableHeader(result.getMetaData())).append(getTableRows(result)).toString();
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
}
