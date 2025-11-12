package com.solvd.schoolschedule.service.impl;

import java.util.List;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.*;

/**
 * Service that orchestrates the timetable generation using genetic algorithm
 */
public class TimetableGeneratorServiceImpl implements ITimetableGeneratorService {

    private final IPopulationService populationService;
    private final IFitnessService fitnessService;
    private final ISelectionService selectionService;
    private final IGeneticOperatorService geneticOperatorService;
    private final IDisplayService displayService;

    public TimetableGeneratorServiceImpl() {
        // Initialize all required services
        this.populationService = new PopulationServiceImpl();
        this.fitnessService = new FitnessServiceImpl(populationService);
        this.selectionService = new SelectionServiceImpl(SchoolConfig.GA_TOURNAMENT_SIZE);
        this.geneticOperatorService = new GeneticOperatorServiceImpl(populationService, SchoolConfig.GA_MUTATION_RATE);
        this.displayService = new DisplayServiceImpl();
    }

    /**
     * Generates an optimized timetable using the genetic algorithm
     * This method handles the entire process: initialization, evolution, and display
     */
    @Override
    public void generateAndDisplayTimetable() {
        // Initialize population
        List<Timetable> population = populationService.initializePopulation(SchoolConfig.GA_POPULATION_SIZE);

        // Evaluate initial population
        fitnessService.evaluatePopulation(population);

        // Find and display initial best fitness
        Timetable bestTimetable = findBestTimetable(population);
        displayService.displayGenerationProgress(0, bestTimetable.getFitness());

        // Run genetic algorithm for specified generations
        for (int generation = 1; generation <= SchoolConfig.GA_MAX_GENERATIONS; generation++) {
            // Create new generation
            population = geneticOperatorService.createNewGeneration(population, selectionService);

            // Evaluate new population
            fitnessService.evaluatePopulation(population);

            // Find best timetable in current generation
            bestTimetable = findBestTimetable(population);

            // Print progress every N generations
            if (generation % SchoolConfig.PROGRESS_UPDATE_FREQUENCY == 0) {
                displayService.displayGenerationProgress(generation, bestTimetable.getFitness());
            }
        }

        displayService.displayFinalResults(bestTimetable);

        displayService.displayTimetableSummary(bestTimetable, populationService);
    }

    /**
     * Finds the timetable with the best fitness in the population
     *
     * @param population list of timetables
     * @return timetable with highest fitness
     */
    private Timetable findBestTimetable(List<Timetable> population) {
        return population.stream()
                .max((t1, t2) -> Double.compare(t1.getFitness(), t2.getFitness()))
                .orElseThrow(() -> new IllegalStateException("Population is empty"));
    }
}
