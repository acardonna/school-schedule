package com.solvd.schoolschedule.model;

public enum ConflictType {
    ROOM_CONFLICTS("Room Conflicts"),
    ROOM_ACCOMODATE("Room Accomodate"),
    GROUP_GAPS("Group Gaps"),
    TEACHER_GAPS("Teacher Gaps"),
    MAX_LESSONS_PER_DAY("Max Lessons Per Day"),
    INVALID_ASSIGMENTS("Invalid Assignments"),
    GROUP_COLLISIONS("Group Collisions"),
    TEACHER_COLLISIONS("Teacher Collisions"),
    LAST_LESSON("Last Lesson"),
    ADJUSTMENT("Adjustment");

    private final String displayName;

    ConflictType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
