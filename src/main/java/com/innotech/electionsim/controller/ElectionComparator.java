package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Candidate;

import java.util.Comparator;

public class ElectionComparator implements Comparator<Candidate> {

    @Override
    public int compare(Candidate o1, Candidate o2) {
        return Long.compare(o1.getTotalVotes(), o2.getTotalVotes());
    }
}
