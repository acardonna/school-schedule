package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;

import java.util.List;

public class Adjustment implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.ADJUSTMENT;
    private IPopulationService populationService;

    public Adjustment(IPopulationService populationService) {
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
            List<Lesson> lessons = timetable.getLessonsFor(group);
            totalViolations += calculateGroupAdjustment(lessons);

        }

        return totalViolations;
    }

    /**
     * Count the number of lessons that the group
     * doesn't have yet (compared to the SchoolConfig)
     *
     * @param lessons list of lessons for the group
     * @return needed number of lessons to be added
     */
    public int calculateGroupAdjustment(List<Lesson> lessons) {

        int violations = 0;

        for (Subject subject : Subject.values()) {
            int lessonsNumberConfig = SubjectConfig.getWeeklyLessons(subject);

            int lessonsNumber = (int) lessons.stream()
                    .filter(lesson -> lesson.getSubject().equals(subject))
                    .count();

            violations += Math.abs(lessonsNumberConfig - lessonsNumber);
        }

        return violations;
    }
}
