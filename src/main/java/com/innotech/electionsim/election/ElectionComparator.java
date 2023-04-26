package com.innotech.electionsim.election;

import com.innotech.electionsim.candidate.Candidate;

import java.util.Comparator;

public class ElectionComparator implements Comparator<Candidate> {

    @Override
    public int compare(Candidate o1, Candidate o2) {
        return Long.compare(o1.getTotalVotes(), o2.getTotalVotes());
    }
}
