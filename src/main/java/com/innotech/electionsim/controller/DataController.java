package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.ElectionResult;
import com.innotech.electionsim.model.ResultDao;
import com.innotech.electionsim.view.ResultListView;
import com.innotech.electionsim.view.ResultView;

import java.io.IOException;

public class DataController {
    private final ResultDao data;
    private final ResultListView listView;

    private DataController(ResultDao savedData) {
        data = savedData;
        listView = ResultListView.getInstance(data.getSavedResults());
    }

    public static DataController getInstance() {
        ResultDao savedData;
        try {
            savedData = ResultDao.load();
            return new DataController(savedData);
        } catch (IOException e) {
            return null;
        }
    }

    public void readResultList() {
        boolean viewing = true;
        do {
            String selection = UserInterface.getStringInput(listView.getView());
            if ("x".equals(selection)) {
                viewing = false;
            } else {
                ElectionResult selectedResult = data.getSavedResults().get(Integer.parseInt(selection) - 1);
                ResultView.getInstance(selectedResult).display();
            }
        } while (viewing);
    }

    public void saveElectionResult(ElectionResult result) {
        data.save(result);
    }
}
