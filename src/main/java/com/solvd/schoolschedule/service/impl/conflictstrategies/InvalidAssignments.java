package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.ConflictType;
import com.solvd.schoolschedule.model.Lesson;
import com.solvd.schoolschedule.model.Timetable;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;

public class InvalidAssignments implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.INVALID_ASSIGMENTS;

    @Override
    public ConflictType getConflictType() {
        return conflictType;
    }

    @Override
    public int calculateConflicts(Timetable timetable) {
        int invalid = 0;

        for (Lesson lesson : timetable.getLessons()) {
            // Check teacher-subject match
            if (lesson.getTeacher().getSubject() != lesson.getSubject()) {
                lesson.setConflicted(true);
                invalid++;
            }

            // Check room-subject compatibility
            if (!lesson.getClassroom().canAccommodate(lesson.getSubject())) {
                lesson.setConflicted(true);
                invalid++;
            }
        }

        return invalid;
    }
}
