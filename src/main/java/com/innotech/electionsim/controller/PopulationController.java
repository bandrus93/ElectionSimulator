package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Population;
import com.innotech.electionsim.view.DisplayManager;
import com.innotech.electionsim.view.PopulationView;

public class PopulationController {
    private final Population population;
    private final PopulationView view;
    private static final String POPULATION_COMMAND_LIST = """
            You may adjust population metrics by entering the following commands:
            To slightly shift alignment left or right, type 'l' or 'r', respectively;
            To majorly shift alignment left or right, type 'L' or 'R', respectively;
            To slightly increase or decrease polarity, type 'p+' or 'p-' respectively;
            To majorly increase or decrease polarity, type 'P+' or 'P-', respectively;
            To slightly divide the population, type 'd'; or to majorly divide the population type 'D'.
            When finished, enter 'f'.""";

    private PopulationController(long registeredVoters) {
        population = Population.getInstance(registeredVoters);
        view = PopulationView.getInstance(population);
    }

    public static PopulationController getInstance(long registeredVoters) {
        return new PopulationController(registeredVoters);
    }

    public Population getPopulation() {
        return population;
    }

    public void edit() {
        do {
            switch (UserInterface.getStringInput(view.getView() + POPULATION_COMMAND_LIST)) {
                case "l": population.shift("LEFT", 0.013); break;
                case "r": population.shift("RIGHT", 0.013); break;
                case "L": population.shift("LEFT", 0.05); break;
                case "R": population.shift("RIGHT", 0.05); break;
                case "p+": population.polarize("OUT", 0.013); break;
                case "p-": population.polarize("IN", 0.013); break;
                case "P+": population.polarize("OUT", 0.05); break;
                case "P-": population.polarize("IN", 0.05); break;
                case "d": population.divide(1); break;
                case "D": population.divide(2); break;
                case "f": return;
                default:
                    System.out.println("Invalid command");
            }
            DisplayManager.refresh(view.getView() + POPULATION_COMMAND_LIST);
        } while (true);
    }
}
