package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;

public class RoomAccomodate implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.ROOM_ACCOMODATE;

    @Override
    public ConflictType getConflictType () {
        return conflictType;
    }

    @Override
    public int calculateConflicts (Timetable timetable) {

        return (int) timetable.getLessons().stream()
                .filter(lesson ->lesson.getClassroom().canAccommodate(lesson.getSubject())==false )
                .peek(lesson -> lesson.setConflicted(true))
                .count();
    }
}
