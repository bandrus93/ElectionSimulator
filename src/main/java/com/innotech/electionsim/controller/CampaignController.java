package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Campaign;
import com.innotech.electionsim.model.Candidate;
import com.innotech.electionsim.model.ElectionResult;
import com.innotech.electionsim.model.ElectionSettings;
import com.innotech.electionsim.population.PopulationController;
import com.innotech.electionsim.view.CampaignView;

import java.util.ArrayList;
import java.util.List;

public class CampaignController {
    public static final String CAMPAIGN_COMMAND_LIST = """
            To add a new candidate, enter '+';
            To remove a candidate, enter '-' and the number corresponding to the candidate you wish to remove, (ex. '-1');
            To edit a candidate, enter 'e' and the number corresponding to the candidate you wish to edit, (ex. 'e1').
            When you are finished making changes to the ballot roster, you may run the election by entering 'r'.
            """;

    private CampaignController() {

    }

    public static CampaignController getInstance() {
        return new CampaignController();
    }

    public ElectionResult runElection(ElectionSettings electionContext) {
        Campaign campaign = new Campaign(
                PopulationController.getInstance(UserInterface.getNumericInput(UserInterface.POPULATION_PROMPT)),
                CandidateController.getInstance(),
                electionContext
        );
        campaign.getPopulationController().edit();
        do {
            CampaignView.getInstance(campaign).display();
            String input = UserInterface.getStringInput(CAMPAIGN_COMMAND_LIST);
            if (String.valueOf(input.charAt(0)).equals("r")) {
                if (!electionContext.getType().equals(ElectionSettings.ElectionType.INSTANT_RUNOFF)) {
                    return campaign.cycle();
                } else {
                    ElectionResult result;
                    List<Candidate> eliminated = new ArrayList<>();
                    do {
                        result = campaign.cycle();
                        if (result.getWinner().hasMajority(result.getTotalPopulation())) {
                            campaign.getCandidates().remove(result.getLoser());
                            eliminated.add(result.getLoser());
                            campaign.resetCandidateVotes();
                        }
                    } while (result.getWinner().hasMajority(result.getTotalPopulation()));
                    return result.append(eliminated);
                }
            } else {
                campaign.edit(input);
            }
        } while (true);
    }
}
