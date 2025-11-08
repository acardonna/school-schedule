package com.solvd.schoolschedule.model;

/**
 * Represents a teacher in the school schedule.
 */
public class Teacher {
    private final int id;
    private final String name;
    private final Subject subject;

    public Teacher(int id, String name, Subject subject) {
        this.id = id;
        this.name = name;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return name + " (" + subject.getDisplayName() + ")";
    }
}
