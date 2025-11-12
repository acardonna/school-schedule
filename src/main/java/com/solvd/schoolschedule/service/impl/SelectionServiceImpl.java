package com.solvd.schoolschedule.service.impl;

import java.util.*;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.*;

/**
 * Service for selecting parent timetables for reproduction.
 * Uses tournament selection.
 */
public class SelectionServiceImpl implements ISelectionService {

    private final Random random;
    private final int tournamentSize;

    public SelectionServiceImpl(int tournamentSize) {
        this.random = new Random();
        this.tournamentSize = tournamentSize;
    }

    /**
     * Select a parent from the population using tournament selection
     * @param population the population to select from
     * @return the selected timetable
     */
    @Override
    public Timetable selectParent(List<Timetable> population) {
        List<Timetable> tournament = new ArrayList<>();

        // Select random individuals for tournament
        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(population.size());
            tournament.add(population.get(randomIndex));
        }

        // Return the best individual from tournament
        return tournament.stream()
                .max(Comparator.comparingDouble(Timetable::getFitness))
                .orElseThrow(() -> new IllegalStateException("Tournament selection failed"));
    }

    /**
     * Select two parents for crossover
     * @param population the population to select from
     * @return array of two parent timetables
     */
    @Override
    public Timetable[] selectParents(List<Timetable> population) {
        Timetable parent1 = selectParent(population);
        Timetable parent2 = selectParent(population);

        // Ensure different parents if possible
        while (parent1 == parent2 && population.size() > 1) {
            parent2 = selectParent(population);
        }

        return new Timetable[]{parent1, parent2};
    }

    /**
     * Select multiple parents for reproduction
     * @param population the population
     * @param numberOfParents number of parents to select
     * @return list of selected parents
     */
    @Override
    public List<Timetable> selectParents(List<Timetable> population, int numberOfParents) {
        List<Timetable> parents = new ArrayList<>();
        for (int i = 0; i < numberOfParents; i++) {
            parents.add(selectParent(population));
        }
        return parents;
    }
}
