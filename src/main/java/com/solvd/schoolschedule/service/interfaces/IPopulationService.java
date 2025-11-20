package com.solvd.schoolschedule.service.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.*;

/**
 * Interface for managing genetic algorithm populations
 */
public interface IPopulationService {

    /**
     * Initialize a population of random timetables
     *
     * @param populationSize the size of the population
     * @return list of timetables
     */
    List<Timetable> initializePopulation(int populationSize);

    /**
     * Get all teachers
     *
     * @return list of teachers
     */
    List<Teacher> getTeachers();

    /**
     * Get all classrooms
     *
     * @return list of classrooms
     */
    List<Classroom> getClassrooms();

    /**
     * Get all groups
     *
     * @return list of groups
     */
    List<Group> getGroups();
}
