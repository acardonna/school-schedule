package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;

import java.util.List;

public class TeacherGaps implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.TEACHER_GAPS;
    private IPopulationService populationService;

    public TeacherGaps(IPopulationService populationService){
        this.populationService=populationService;
    }

    @Override
    public ConflictType getConflictType () {
        return conflictType;
    }

    @Override
    public int calculateConflicts (Timetable timetable){
        int totalGaps = 0;

        for (Teacher teacher : populationService.getTeachers()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsOnDayFor(teacher, day);
                if (!dayLessons.isEmpty()) {
                    totalGaps += calculateGapsInDay(dayLessons);
                }
            }
        }

        return totalGaps;
    }

        /**
         * Calculate gaps in a day's lessons
         * @param dayLessons lessons for one day, sorted by period
         * @return number of gaps
         */
        private int calculateGapsInDay(List<Lesson> dayLessons) {
            if (dayLessons.size() <= 1) return 0;

            int gaps = 0;
            int previousPeriod = -1;

            for (Lesson lesson : dayLessons) {
                int currentPeriod = lesson.getTimeSlot().getPeriod();
                if (previousPeriod != -1 && currentPeriod > previousPeriod + 1) {
                    lesson.setConflicted(true);
                    //set conflicted for previous lesson true??
                    gaps += currentPeriod - previousPeriod - 1;
                }
                previousPeriod = currentPeriod;
            }

            return gaps;
        }
}
