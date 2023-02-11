package com.innotech.electionsim.data;

import com.google.gson.Gson;
import com.innotech.electionsim.model.ElectionResult;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResultDao {
    private final Gson gson = new Gson();
    private final String DATA_PATH = "src/main/resources/result_repository.txt";

    private ResultDao() {

    }

    public static ResultDao getInstance() {
        return new ResultDao();
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
    public List<ElectionResult> load() throws IOException {
        Scanner dataReader = new Scanner(Paths.get(DATA_PATH)).useDelimiter("\\[\\{\"candidateRanking\"|},\\{\"candidateRanking\"|}}]");
        List<ElectionResult> savedResults = new ArrayList<>();
        do {
            if (dataReader.hasNext()) {
                String result = dataReader.next();
                String reformattedResult = "{\"candidateRanking\"" + result + "}}";
                ElectionResult savedResult = gson.fromJson(reformattedResult, ElectionResult.class);
                savedResults.add(savedResult);
            }
        } while (dataReader.hasNext());
        return savedResults;
    }

}
