package com.innotech.electionsim.population;

import com.innotech.electionsim.controller.UserInterface;
import com.innotech.electionsim.view.DisplayManager;

public class PopulationController {
    private final Population population;
    private final PopulationView view;
    private static final String POPULATION_COMMAND_LIST = """
            You may adjust population metrics by entering the following commands:
            To slightly shift alignment left or right, enter 'l' or 'r', respectively;
            To majorly shift alignment left or right, enter 'L' or 'R', respectively;
            To slightly polarize the population, enter 'p'; or to majorly polarize the population enter 'P'.
            To slightly unify the population, enter 'u'; or to majorly unify the population enter 'U'.
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
                case "u": population.unify(4); break;
                case "U": population.unify(2);
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
