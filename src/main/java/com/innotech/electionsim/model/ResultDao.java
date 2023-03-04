package com.innotech.electionsim.model;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResultDao {
    private final String DATA_PATH = "src/main/resources/result_repository.txt";
    private final Gson gson = new Gson();
    private final List<ElectionResult> savedResults = new ArrayList<>();

    private ResultDao() throws IOException {
        Scanner dataReader = new Scanner(Paths.get(DATA_PATH)).useDelimiter("\\[\\{\"candidateRanking\"|},\\{\"candidateRanking\"|}}]");
        do {
            if (dataReader.hasNext()) {
                String result = dataReader.next();
                String reformattedResult = "{\"candidateRanking\"" + result + "}}";
                ElectionResult savedResult = gson.fromJson(reformattedResult, ElectionResult.class);
                savedResults.add(savedResult);
            }
        } while (dataReader.hasNext());
    }

    public void save(ElectionResult result) {
        try (FileWriter dataWriter = new FileWriter(DATA_PATH)) {
            String toSave = gson.toJson(result);
            Scanner dataReader = new Scanner(Paths.get(DATA_PATH));
            String savedResults;
            if (!dataReader.hasNext()) {
                dataWriter.write("[" + toSave + "]");
            } else {
                savedResults = dataReader.next();
                String toAppend = savedResults.substring(0, savedResults.lastIndexOf("]"));
                dataWriter.write(toAppend + "," + toSave + "]");
                dataReader.close();
            }
        } catch (IOException e) {
            System.out.println("Save Error: Unable to save results");
        }
    }
    public static ResultDao load() throws IOException {
        return new ResultDao();
    }

    public List<ElectionResult> getSavedResults() {
        return savedResults;
    }

}
