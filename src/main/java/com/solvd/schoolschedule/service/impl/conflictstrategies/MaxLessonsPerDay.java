package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;

import java.util.List;

public class MaxLessonsPerDay implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.MAX_LESSONS_PER_DAY;
    private IPopulationService populationService;

    public MaxLessonsPerDay(IPopulationService populationService) {
        this.populationService = populationService;
    }

    @Override
    public ConflictType getConflictType() {
        return conflictType;
    }

    @Override
    public int calculateConflicts(Timetable timetable) {
        int violations = 0;

        for (Group group : populationService.getGroups()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsOnDayFor(group, day);
                if (dayLessons.size() > SchoolConfig.MAX_PERIODS_PER_DAY) {
                    violations += dayLessons.size() - SchoolConfig.MAX_PERIODS_PER_DAY;
                }
            }
        }

        return violations;
    }

}
