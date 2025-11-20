package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoomConflicts implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.ROOM_CONFLICTS;

    @Override
    public ConflictType getConflictType () {
        return conflictType;
    }

    @Override
    public int calculateConflicts (Timetable timetable) {
        int conflicts = 0;
        Map<TimeSlot, Set<Classroom>> timeSlotRooms = new HashMap<>();

        for (Lesson lesson : timetable.getLessons()) {
            TimeSlot timeSlot = lesson.getTimeSlot();
            Classroom classroom = lesson.getClassroom();

            timeSlotRooms.computeIfAbsent(timeSlot, k -> new HashSet<>());
            if (!timeSlotRooms.get(timeSlot).add(classroom)) {
                lesson.setConflicted(true);
                conflicts++;
            }
        }

        return conflicts;
    }
}
