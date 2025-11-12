package com.solvd.schoolschedule.service.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.Timetable;

/**
 * Interface for genetic operators: crossover and mutation
 */
public interface IGeneticOperatorService {

    /**
     * Perform crossover between two parent timetables
     * @param parent1 first parent
     * @param parent2 second parent
     * @return offspring timetable
     */
    Timetable crossover(Timetable parent1, Timetable parent2);

    /**
     * Perform mutation on a timetable
     * @param timetable the timetable to mutate
     * @return mutated timetable (or original if no mutation)
     */
    Timetable mutate(Timetable timetable);

    /**
     * Create a new generation through selection, crossover, and mutation
     * @param population current population
     * @param selectionService selection service
     * @return new generation
     */
    List<Timetable> createNewGeneration(List<Timetable> population, ISelectionService selectionService);
}
