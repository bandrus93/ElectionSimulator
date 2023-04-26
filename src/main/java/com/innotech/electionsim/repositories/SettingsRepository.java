package com.innotech.electionsim.repositories;

import com.google.gson.Gson;
import com.innotech.electionsim.election.ElectionSettings;
import org.springframework.stereotype.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
@Repository
public class SettingsRepository {

    public ElectionSettings load() throws IOException {
        Scanner settingsData = new Scanner(Paths.get("src/main/resources/election_settings.txt"));
        Gson gson = new Gson();
        return gson.fromJson(settingsData.nextLine(), ElectionSettings.class);
    }

    public void save(ElectionSettings updatedSettings) {
        try (FileWriter settings = new FileWriter("src/main/resources/election_settings.txt")) {
            Gson gson = new Gson();
            String updatedJson = gson.toJson(updatedSettings);
            settings.write(updatedJson);
        } catch (IOException e) {
            System.out.println("Save Error: Unable to save settings");
        }
    }

}
