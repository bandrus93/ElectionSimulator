package com.innotech.electionsim.view;

import com.innotech.electionsim.model.Campaign;
import com.innotech.electionsim.model.Candidate;

import java.util.List;

public class CampaignView {
    public static final String CANDIDATE_LIST_HEADING = "Active Candidates:\n";
    private final String view;

    private CampaignView(String toDisplay) {
        view = toDisplay;
    }

    public static CampaignView getInstance(Campaign campaign) {
        return new CampaignView(getCandidateList(campaign));
    }

    private static String getCandidateList(Campaign campaign) {
        StringBuilder sb = new StringBuilder(CANDIDATE_LIST_HEADING);
        List<Candidate> candidates = campaign.getCandidates();
        for (int i = 0; i < candidates.size(); i++) {
            sb.append(i + 1).append(" - ").append(candidates.get(i).getName()).append("\n");
            if (i == candidates.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public void display() {
        System.out.println(view);
    }
}
