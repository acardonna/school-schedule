package com.solvd.schoolschedule.service.impl;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.*;
import com.solvd.schoolschedule.view.TimetableView;

/**
 * Service for displaying all console output
 */
public class DisplayServiceImpl implements IDisplayService {

    private final TimetableView timetableView;

    public DisplayServiceImpl() {
        this.timetableView = new TimetableView();
    }

    /**
     * Displays on console a complete summary of the timetable
     *
     * @param timetable timetable
     * @param populationService populationService
     */
    @Override
    public void displayTimetableSummary(Timetable timetable, IPopulationService populationService) {
        timetableView.displayTimetableSummaryHeader();

        for (Group group : populationService.getGroups()) {
            timetableView.displayGroupSchedule(timetable, group);
        }

        timetableView.displayTeacherSchedulesHeader();
        for (Teacher teacher : populationService.getTeachers()) {
            timetableView.displayTeacherSchedule(timetable, teacher);
        }
    }

    /**
     * Displays the progress of a generation in the genetic algorithm
     *
     * @param generation generation number
     * @param fitness best fitness value
     */
    @Override
    public void displayGenerationProgress(int generation, double fitness) {
        timetableView.displayGenerationProgress(generation, fitness);
    }

    /**
     * Displays the final results of the genetic algorithm
     *
     * @param timetable the final best timetable
     */
    @Override
    public void displayFinalResults(Timetable timetable) {
        timetableView.displayFinalResultsHeader();
        timetableView.displayBestFitness(timetable.getFitness());
        timetableView.displayTotalLessons(timetable.getLessons().size());
    }
}
