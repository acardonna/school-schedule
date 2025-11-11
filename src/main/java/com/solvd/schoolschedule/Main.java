package com.solvd.schoolschedule;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize services
        PopulationService populationService = new PopulationService();
        FitnessService fitnessService = new FitnessService(populationService);
        SelectionService selectionService = new SelectionService(SchoolConfig.GA_TOURNAMENT_SIZE);
        GeneticOperatorService geneticOperatorService = new GeneticOperatorService(populationService, SchoolConfig.GA_MUTATION_RATE);

        // Initialize population
        List<Timetable> population = populationService.initializePopulation(SchoolConfig.GA_POPULATION_SIZE);

        // Evaluate initial population
        fitnessService.evaluatePopulation(population);

        // Find and display initial best fitness
        Timetable bestTimetable = population.stream()
                .max((t1, t2) -> Double.compare(t1.getFitness(), t2.getFitness()))
                .orElseThrow();
        System.out.println("Generation 0 - Best Fitness: " + bestTimetable.getFitness());

        // Run genetic algorithm for specified generations
        for (int generation = 1; generation <= SchoolConfig.GA_MAX_GENERATIONS; generation++) {
            // Create new generation
            population = geneticOperatorService.createNewGeneration(population, selectionService);

            // Evaluate new population
            fitnessService.evaluatePopulation(population);

            // Find best timetable in current generation
            bestTimetable = population.stream()
                    .max((t1, t2) -> Double.compare(t1.getFitness(), t2.getFitness()))
                    .orElseThrow();

            // Print progress every N generations
            if (generation % SchoolConfig.PROGRESS_UPDATE_FREQUENCY == 0) {
                System.out.println("Generation " + generation + " - Best Fitness: " + bestTimetable.getFitness());
            }
        }

        // Display final results
        System.out.println("\n=== FINAL RESULTS ===");
        System.out.println("Best Fitness: " + bestTimetable.getFitness());
        System.out.println("Total Lessons: " + bestTimetable.getLessons().size());

        // Display timetable summary
        displayTimetableSummary(bestTimetable, populationService);
    }

    private static void displayTimetableSummary(Timetable timetable, PopulationService populationService) {
        System.out.println("\n=== TIMETABLE SUMMARY ===");

        for (Group group : populationService.getGroups()) {
            System.out.println("\nGroup: " + group.getName());
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
                if (!dayLessons.isEmpty()) {
                    System.out.print("  " + SchoolConfig.DAY_NAMES[day] + ": ");
                    for (Lesson lesson : dayLessons) {
                        System.out.print(lesson.getSubject().getDisplayName().charAt(0) + " ");
                    }
                    System.out.println("(" + dayLessons.size() + " lessons)");
                }
            }
        }

        System.out.println("\n=== TEACHER SCHEDULES ===");
        for (Teacher teacher : populationService.getTeachers()) {
            System.out.println("\n" + teacher.getName() + " (" + teacher.getSubject().getDisplayName() + "):");
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);
                if (!dayLessons.isEmpty()) {
                    System.out.println("  " + SchoolConfig.DAY_NAMES[day] + ": " + dayLessons.size() + " lessons");
                }
            }
        }
    }
}
