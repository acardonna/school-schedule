package com.solvd.schoolschedule.service.interfaces;

import com.solvd.schoolschedule.model.Timetable;

/**
 * Interface for the service that orchestrates timetable generation using genetic algorithm
 */
public interface ITimetableGeneratorService {

    /**
     * Generates an optimized timetable using the genetic algorithm
     * This method handles the entire process: initialization, evolution, and display
     */
    Timetable generateAndDisplayTimetable();

    void findSolution(int maxAttempts);
}
