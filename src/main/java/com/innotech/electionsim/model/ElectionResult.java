package com.innotech.electionsim.model;

import com.innotech.electionsim.data.ElectionSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ElectionResult {
    private final List<Candidate> candidateRanking;
    private final Long totalPopulation;
    private ElectionSettings metaData;

    public ElectionResult(PriorityQueue<Candidate> resultRank, long totalPopulation) {
        Object[] resultArr = resultRank.toArray();
        ArrayList<Candidate> resultList = new ArrayList<>();
        for (Object candidate : resultArr) {
            Candidate nextRanked = (Candidate) candidate;
            resultList.add(nextRanked);
        }
        this.candidateRanking = resultList;
        this.totalPopulation = totalPopulation;
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

    public void setMetaData(ElectionSettings metaData) {
        this.metaData = metaData;
    }

    public ElectionResult append(List<Candidate> eliminated) {
        for (Candidate candidate : eliminated) {
            candidate.resetVotes();
            candidateRanking.add(candidate);
        }
        return this;
    }
}
