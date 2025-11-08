package com.solvd.schoolschedule.model;

/**
 * Represents a subject like Physics in the school schedule.
 */
public enum Subject {
    MATH("Mathematics"),
    PHYSICS("Physics"),
    INFORMATICS("Informatics"),
    PHYSICAL_CULTURE("Physical Culture");

    private final String displayName;

    Subject(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}