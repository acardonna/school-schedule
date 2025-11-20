package com.solvd.schoolschedule.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.solvd.schoolschedule.model.TimetableConflicts;
import com.solvd.schoolschedule.util.ConflictJSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solvd.schoolschedule.dao.impl.TimetableDAOImpl;
import com.solvd.schoolschedule.dao.interfaces.ITimetableDAO;
import com.solvd.schoolschedule.model.SchoolConfig;
import com.solvd.schoolschedule.model.Timetable;
import com.solvd.schoolschedule.service.interfaces.IDisplayService;
import com.solvd.schoolschedule.service.interfaces.IFitnessService;
import com.solvd.schoolschedule.service.interfaces.IGeneticOperatorService;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;
import com.solvd.schoolschedule.service.interfaces.ISelectionService;
import com.solvd.schoolschedule.service.interfaces.ITimetableGeneratorService;

/**
 * Service that orchestrates the timetable generation using genetic algorithm
 */
public class TimetableGeneratorServiceImpl implements ITimetableGeneratorService {

    private static final Logger logger = LogManager.getLogger(TimetableGeneratorServiceImpl.class);

    private final IPopulationService populationService;
    private final IFitnessService fitnessService;
    private final ISelectionService selectionService;
    private final IGeneticOperatorService geneticOperatorService;
    private final IDisplayService displayService;
    private final ITimetableDAO timetableDAO;

    public TimetableGeneratorServiceImpl() {
        // Initialize all required services
        this.populationService = new PopulationServiceImpl();
        this.fitnessService = new FitnessServiceImpl(populationService);
        this.selectionService = new SelectionServiceImpl(SchoolConfig.GA_TOURNAMENT_SIZE);
        this.geneticOperatorService = new GeneticOperatorServiceImpl(populationService, SchoolConfig.GA_MUTATION_RATE);
        this.displayService = new DisplayServiceImpl();
        this.timetableDAO = new TimetableDAOImpl();
    }

    /**
     * Generates an optimized timetable using the genetic algorithm
     * This method handles the entire process: initialization, evolution, and display
     */
    @Override
    public Timetable generateAndDisplayTimetable() {
        List<TimetableConflicts> bestTimetables = new ArrayList<>();

        // Initialize population
        List<Timetable> population = populationService.initializePopulation(SchoolConfig.GA_POPULATION_SIZE);

        // Evaluate initial population
        fitnessService.evaluatePopulation(population);

        // Find and display initial best fitness
        Timetable bestTimetable = findBestTimetable(population);
        displayService.displayGenerationProgress(0, bestTimetable.getFitness());

        // Run genetic algorithm for specified generations
        int generation = 1;
        boolean solutionFound = false;
        while (generation <= SchoolConfig.GA_MAX_GENERATIONS & !solutionFound) {
            // Create new generation
            population = geneticOperatorService.createNewGeneration(population, selectionService);

            // Evaluate new population
            fitnessService.evaluatePopulation(population);

            // Find best timetable in current generation
            bestTimetable = findBestTimetable(population);
            bestTimetable.setGeneration(generation);


            // Print progress every 20 generations
            if (generation % 20 == 0) {
                fitnessService.updateConflicts(bestTimetable);
                TimetableConflicts timetableConflicts = new TimetableConflicts(bestTimetable);
                bestTimetables.add(timetableConflicts);
            }


            solutionFound = bestTimetable.getFitness() >= 2000;

            // Print progress every N generations
            if (generation % SchoolConfig.PROGRESS_UPDATE_FREQUENCY == 0) {
                displayService.displayGenerationProgress(generation, bestTimetable.getFitness());

            }

            generation++;
        }

        if (solutionFound) {
            displayService.displayFinalResults(bestTimetable);

            displayService.display("Number of generations: " + (generation - 1));

            displayService.displayTimetableSummary(bestTimetable, populationService);

            displayService.displayFinalResults(bestTimetable);

            // Save the best timetable to database
            logger.info("=== Saving timetable to database... ===");
            timetableDAO.create(bestTimetable);
            logger.info("=== Timetable saved successfully!   ===");

            ConflictJSONParser.serealize(bestTimetables);
        }
        return bestTimetable;

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

    /**
     * Tries to find a solution with perfect fitness
     *
     * @param maxNumberOfTries list of timetables
     */
    public void findSolution(int maxNumberOfTries) {
        int attempt = 0;
        boolean solutionFound = false;
        while (attempt < maxNumberOfTries & !solutionFound) {
            displayService.display("ATTEMPT #" + (attempt + 1) + ":");
            Timetable bestTimeTable = generateAndDisplayTimetable();
            solutionFound = bestTimeTable.getFitness() >= 2000;
            attempt++;
        }
    }
}
