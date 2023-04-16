package com.innotech.electionsim.controller;

import com.innotech.electionsim.model.Population;
import com.innotech.electionsim.view.DisplayManager;
import com.innotech.electionsim.view.PopulationView;

public class PopulationController {
    private final Population population;
    private final PopulationView view;
    private static final String POPULATION_COMMAND_LIST = """
            You may adjust population metrics by entering the following commands:
            To slightly shift alignment left or right, enter 'l' or 'r', respectively;
            To majorly shift alignment left or right, enter 'L' or 'R', respectively;
            To slightly polarize the population, enter 'p'; or to majorly polarize the population enter 'P'.
            To slightly divide the population, enter 'd'; or to majorly divide the population enter 'D'.
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
                case "l": population.shift(4, "-"); break;
                case "r": population.shift(4, "+"); break;
                case "L": population.shift(2, "-"); break;
                case "R": population.shift(2, "+"); break;
                case "p": population.polarize(2); break;
                case "P": population.polarize(1); break;
                case "d": population.divide(4); break;
                case "D": population.divide(2); break;
                case "f": return;
                default:
                    System.out.println("Invalid command");
            }
            DisplayManager.refresh(view.getView() + POPULATION_COMMAND_LIST);
        } while (true);
    }
}
