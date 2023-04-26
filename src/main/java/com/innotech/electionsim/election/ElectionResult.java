package com.innotech.electionsim.election;

import com.innotech.electionsim.candidate.Candidate;
import com.innotech.electionsim.view.ResultView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.PriorityQueue;

public class ElectionResult {
    private final List<Candidate> candidateRanking;
    private final Long totalPopulation;
    private final ElectionSettings metaData;

    public ElectionResult(PriorityQueue<Candidate> resultRank, long totalPopulation, ElectionSettings settings) {
        Object[] resultArr = resultRank.toArray();
        ArrayList<Candidate> resultList = new ArrayList<>();
        for (Object candidate : resultArr) {
            Candidate nextRanked = (Candidate) candidate;
            resultList.add(nextRanked);
        }
        this.candidateRanking = resultList;
        this.totalPopulation = totalPopulation;
        metaData = settings;
    }

    public List<Candidate> getCandidateRanking() {
        return candidateRanking;
    }

    public Long getTotalPopulation() {
        return totalPopulation;
    }

    public ElectionSettings getMetaData() {
        return metaData;
    }

    public Candidate getWinner() {
        return candidateRanking.get(candidateRanking.size() - 1);
    }

    public Candidate getLoser() {
        return candidateRanking.get(0);
    }

    public ElectionResult append(List<Candidate> eliminated) {
        for (Candidate candidate : eliminated) {
            candidate.resetVotes();
            candidateRanking.add(candidate);
        }
        return this;
    }

    public String getResultTable() {
        return ResultView.getInstance(this);
    }

    @Override
    public String toString() {
        StringBuilder titleBuilder = new StringBuilder();
        String electionYear = metaData.getElectionDay().equals("NULL")
                ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR))
                : metaData.getElectionDay().split("-")[2];
        return titleBuilder.append(metaData.getLocale())
                .append(" ")
                .append(metaData.getRace())
                .append(" Election ")
                .append(electionYear)
                .append("\n")
                .toString();
    }
}
