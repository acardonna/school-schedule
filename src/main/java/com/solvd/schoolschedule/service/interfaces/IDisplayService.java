package com.solvd.schoolschedule.service.interfaces;

import com.solvd.schoolschedule.model.*;

/**
 * Interface for display services that handle console output of timetables
 */
public interface IDisplayService {

    /**
     * Displays on console a summary of the timetable
     *
     * @param timetable timetable
     * @param populationService populationService
     */
    void timetableSummary(Timetable timetable, IPopulationService populationService);

    /**
     * Displays on console the weekly schedule of a specific group
     *
     * @param timetable timetable
     * @param group group
     */
    void groupSchedule(Timetable timetable, Group group);

    /**
     * Displays on console the weekly schedule of a specific teacher
     *
     * @param timetable timetable
     * @param teacher teacher
     */
    void teacherSchedule(Timetable timetable, Teacher teacher);

    /**
     * Abbreviates the string into a string of length 4.
     * If it's one word, takes the first 4 characters.
     * If it's more than one word, takes the first character of the first word, and 3 characters of the second word.
     *
     * @param string string
     * @return abbreviation
     */
    String abbreviate(String string);
}
