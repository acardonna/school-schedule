package com.solvd.schoolschedule.model;

/**
 * Central configuration for all school scheduling parameters.
 */
public class SchoolConfig {

    // ========== Schedule Configuration ==========

    /**
     * Number of working days per week (Monday-Friday)
     */
    public static final int WORKING_DAYS_PER_WEEK = 5;

    /**
     * Maximum number of class periods per day
     */
    public static final int MAX_PERIODS_PER_DAY = 6;

    /**
     * Day names for display purposes
     */
    public static final String[] DAY_NAMES = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };

    // ========== School Resources ==========

    /**
     * Total number of classrooms in the school
     */
    public static final int NUM_CLASSROOMS = 5;

    /**
     * Total number of teachers in the school
     */
    public static final int NUM_TEACHERS = 8;

    /**
     * Total number of student groups
     */
    public static final int NUM_GROUPS = 4;

    // ========== Genetic Algorithm Parameters ==========

    /**
     * Population size for the genetic algorithm
     * Larger population = more diversity but slower execution
     */
    public static final int GA_POPULATION_SIZE = 100;

    /**
     * Maximum number of generations for evolution
     * More generations = better optimization but longer runtime
     */
    public static final int GA_MAX_GENERATIONS = 700;

    /**
     * Mutation rate (probability of mutation occurring)
     * Higher rate = more exploration but less stability
     */
    public static final double GA_MUTATION_RATE = 0.05;

    /**
     * Tournament size for selection
     * Larger tournament = stronger selection pressure
     */
    public static final int GA_TOURNAMENT_SIZE = 5;

    // ========== Display Configuration ==========

    /**
     * Progress update frequency (every N generations)
     */
    public static final int PROGRESS_UPDATE_FREQUENCY = 100;

    // Private constructor to prevent instantiation
    private SchoolConfig() {
        throw new AssertionError("SchoolConfig is a utility class and should not be instantiated");
    }
}
