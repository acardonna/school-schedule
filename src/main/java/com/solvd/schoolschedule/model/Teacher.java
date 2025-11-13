package com.solvd.schoolschedule.model;

import java.util.List;

/**
 * Represents a teacher in the school schedule.
 */
public class Teacher implements ITimetableFilter{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return id == teacher.id;
    }

    /**
     * Get the ITimetableFilter object from a lesson.
     * In this case, get teacher.
     *
     * @param lesson
     * @return teacher
     */
    @Override
    public ITimetableFilter getFromLesson(Lesson lesson){
        return lesson.getTeacher();
    }
}
