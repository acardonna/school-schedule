package com.solvd.schoolschedule;

import com.solvd.schoolschedule.service.impl.TimetableGeneratorServiceImpl;
import com.solvd.schoolschedule.service.interfaces.ITimetableGeneratorService;
import com.solvd.schoolschedule.util.DatabaseInitializer;

/**
 * This application uses a genetic algorithm to generate optimized school timetables
 * that avoid scheduling conflicts and meet various constraints.
 *
 * <p>The application flow:</p>
 * <ol>
 *   <li>Initialize the database with reference data (subjects, groups, teachers, classrooms)</li>
 *   <li>Run the genetic algorithm to generate an optimized timetable</li>
 *   <li>Persist the best timetable to the database</li>
 *   <li>Display the timetable in the console</li>
 * </ol>
 */
public class Main {
    public static void main(String[] args) {

        // Initialize database with initial data (except for timetable and lesson tables)
        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.populateDatabase();

        // Generate optimized timetable using genetic algorithm
        ITimetableGeneratorService timetableGenerator = new TimetableGeneratorServiceImpl();
        timetableGenerator.findSolution(10);

     }
}
