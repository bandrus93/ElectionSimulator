package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Candidate;

import java.util.Comparator;

public class ApprovalComparator implements Comparator<Candidate> {

    @Override
    public int compare(Candidate o1, Candidate o2) {
        return o1.getSwayScore() - o2.getSwayScore();
    }
}
