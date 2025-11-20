package com.solvd.schoolschedule.service.interfaces;

import com.solvd.schoolschedule.model.Timetable;

/**
 * Interface for display services that handle all console output
 */
public interface IDisplayService {

    /**
     * Displays on console a complete summary of the timetable
     *
     * @param timetable         timetable
     * @param populationService populationService
     */
    void displayTimetableSummary(Timetable timetable, IPopulationService populationService);

    /**
     * Displays the progress of a generation in the genetic algorithm
     *
     * @param generation generation number
     * @param fitness    best fitness value
     */
    void displayGenerationProgress(int generation, double fitness);

    /**
     * Displays the final results of the genetic algorithm
     *
     * @param timetable the final best timetable
     */
    void displayFinalResults(Timetable timetable);

    void display(String string);
}
