package com.innotech.electionsim.model;

import com.innotech.electionsim.controller.CampaignController;
import com.innotech.electionsim.controller.SettingsController;

public class Election {
    private final CampaignController campaignController;
    private final SettingsController settingsController;

    private Election() {
        settingsController = SettingsController.getInstance();
        campaignController = CampaignController.getInstance();
    }

    public static Election getInstance() {
        return new Election();
    }

    public ElectionSettings getElectionSettings() {
        return settingsController.getSettings();
    }

    public void updateSettings() {
        settingsController.update();
    }

    public ElectionResult run() {
        return campaignController.runElection(settingsController.getSettings());
    }
}
