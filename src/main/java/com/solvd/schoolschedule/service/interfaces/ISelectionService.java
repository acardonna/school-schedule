package com.solvd.schoolschedule.service.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.Timetable;

/**
 * Interface for selecting parent timetables for reproduction
 */
public interface ISelectionService {

    /**
     * Select a parent from the population using tournament selection
     *
     * @param population the population to select from
     * @return the selected timetable
     */
    Timetable selectParent(List<Timetable> population);

    /**
     * Select two parents for crossover
     *
     * @param population the population to select from
     * @return array of two parent timetables
     */
    Timetable[] selectParents(List<Timetable> population);

    /**
     * Select multiple parents for reproduction
     *
     * @param population      the population
     * @param numberOfParents number of parents to select
     * @return list of selected parents
     */
    List<Timetable> selectParents(List<Timetable> population, int numberOfParents);
}
