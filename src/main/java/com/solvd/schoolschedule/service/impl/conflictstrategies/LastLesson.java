package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;

import java.util.List;
import java.util.stream.Collectors;

public class LastLesson implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.LAST_LESSON;
    private IPopulationService populationService;

    public LastLesson(IPopulationService populationService) {
        this.populationService = populationService;
    }

    @Override
    public ConflictType getConflictType() {
        return conflictType;
    }

    @Override
    public int calculateConflicts(Timetable timetable) {
        int totalViolations = 0;

        for (Group group : populationService.getGroups()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsOnDayFor(group, day);
                if (!dayLessons.isEmpty()) {
                    totalViolations += calculateLastLessonInDay(dayLessons);
                }
            }
        }

        return totalViolations;
    }

    /**
     * Checks if that day violates the rule:
     * Last lesson should be Physical Culture
     *
     * @param dayLessons list of lessons for the day
     * @return 0 or 1, depending on if that day violates the rule
     */
    private int calculateLastLessonInDay(List<Lesson> dayLessons) {
        if (dayLessons.isEmpty()) return 0;

        List<Integer> periods = dayLessons.stream()
                .filter(lesson -> lesson.getSubject().equals(Subject.PHYSICAL_CULTURE))
                .map(lesson -> lesson.getTimeSlot().getPeriod())
                .collect(Collectors.toList());

        int phyCulGaps = 0;

        for (int i = 0; i < periods.size() - 1; i++) {
            int newGaps = periods.get(i + 1) - periods.get(i) - 1;
            if (newGaps > 0) {
                dayLessons.stream()
                        .filter(lesson -> lesson.getSubject().equals(Subject.PHYSICAL_CULTURE))
                        .forEach(lesson -> lesson.setConflicted(true));
            }
            phyCulGaps += newGaps;
        }
        if (!periods.isEmpty()) {
            int lastPeriod = dayLessons.getLast().getTimeSlot().getPeriod();
            int newGaps = lastPeriod - periods.getLast();
            if (newGaps > 0) {
                dayLessons.getLast().setConflicted(true);
            }
            phyCulGaps += newGaps;
        }
        return phyCulGaps;
    }
}
