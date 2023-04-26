package com.innotech.electionsim.services;

import com.innotech.electionsim.election.ElectionResult;
import com.innotech.electionsim.election.ElectionSettings;
import com.innotech.electionsim.repositories.ResultRepository;
import com.innotech.electionsim.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private final SettingsRepository settingsRepo;
    @Autowired
    private final ResultRepository resultRepo;

    public DataService() {
        settingsRepo = new SettingsRepository();
        resultRepo = new ResultRepository();
    }

    public ElectionSettings getSavedSettings() throws IOException {
        return settingsRepo.load();
    }

    public void saveCurrentSettings(ElectionSettings settings) throws IOException {
        settingsRepo.save(settings);
    }

    public List<ElectionResult> getSavedResults() throws IOException {
        return resultRepo.load();
    }

    public void saveElectionResult(ElectionResult result) {
        resultRepo.save(result);
    }
}
