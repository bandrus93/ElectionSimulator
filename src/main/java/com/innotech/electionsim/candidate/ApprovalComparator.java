package com.innotech.electionsim.candidate;

import java.util.Comparator;

public class ApprovalComparator implements Comparator<Candidate> {

    @Override
    public int compare(Candidate o1, Candidate o2) {
        return o1.getSwayScore() - o2.getSwayScore();
    }
}
