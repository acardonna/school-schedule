package com.solvd.schoolschedule.service.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.*;

/**
 * Interface for evaluating timetable fitness based on scheduling constraints
 */
public interface IFitnessService {

    /**
     * Calculate fitness for a timetable
     * Higher fitness = better timetable (fewer constraint violations)
     * @param timetable the timetable to evaluate
     * @return fitness score
     */
    double calculateFitness(Timetable timetable);

    /**
     * Evaluate the entire population and set fitness for each timetable
     * @param population the population to evaluate
     */
    void evaluatePopulation(List<Timetable> population);

//    /**
//     * Count the number of lessons that the group
//     * doesn't have yet (compared to the SchoolConfig)
//     * @param lessons list of lessons for the group
//     * @return needed number of lessons to be added
//     */
    // int calculateGroupAdjustment(List<Lesson> lessons);
}
