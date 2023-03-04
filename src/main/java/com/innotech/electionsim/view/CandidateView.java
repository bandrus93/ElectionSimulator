package com.innotech.electionsim.view;

import com.innotech.electionsim.model.Candidate;
import com.innotech.electionsim.model.Stat;

import java.util.Iterator;

public class CandidateView {
    public static final String CANDIDTAE_NAME_LABEL = "[N]ame: ";
    public static final String CANDIDATE_ALIGNMENT_LABEL = "[A]lignment: ";
    public static final String CANDIDATE_ENERGY_LABEL = "[E]nergy          | ";
    public static final String CANDIDATE_INTELLIGENCE_LABEL = "[I]ntelligence    | ";
    public static final String CANDIDATE_WIT_LABEL = "[W]it             | ";
    public static final String CANDIDATE_LEVEL_HEAD_LABEL = "[L]evel-headedness| ";
    public static final String CANDIDATE_SPEAK_ABILITY_LABEL = "[S]peaking ability| ";
    private static final StringBuilder viewBuilder = new StringBuilder();
    private final String view;

    private CandidateView(String toDisplay) {
        view = toDisplay;
    }

    public static CandidateView getInstance(Candidate candidate) {
        if (!viewBuilder.isEmpty()) {
            viewBuilder.delete(0, viewBuilder.length());
        }
        return new CandidateView(getStatsGraph(candidate));
    }

    private static String getStatsGraph(Candidate candidate) {
        Iterator<Stat> staterator = candidate.getStats().iterator();
        do {
            Stat nextStat = staterator.next();
            viewBuilder.append(nextStat.getLabel());
            if (nextStat.isNumeral()) {
                viewBuilder.append("*".repeat(Math.max(0, Integer.parseInt(nextStat.getValue())))).append("\n");
            } else {
                viewBuilder.append(nextStat.getValue()).append("\n");
            }
            if (!staterator.hasNext()) {
                viewBuilder.append("\n");
            }
        } while (staterator.hasNext());
        return viewBuilder.toString();
    }

    public String getView() {
        return view;
    }

    public void display() {
        System.out.println(view);
    }
}
