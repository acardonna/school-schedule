package com.solvd.schoolschedule.service.interfaces;

import com.solvd.schoolschedule.model.ConflictType;
import com.solvd.schoolschedule.model.Timetable;

public interface IConflictStrategy {

    /**
     * Calculate the number of times the timetable doesn't fit a rule
     *
     * @param timetable the timetable
     * @return number of conflicts
     */
    int calculateConflicts(Timetable timetable);

    /**
     * Returns the ConflictType of the strategy
     *
     * @return conflictType conflictType
     */
    ConflictType getConflictType();
}
