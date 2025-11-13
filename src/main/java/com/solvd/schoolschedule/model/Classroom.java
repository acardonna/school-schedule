package com.solvd.schoolschedule.model;

import com.solvd.schoolschedule.model.interfaces.ITimetableFilter;

import java.util.Set;

/**
 * Represents a classroom in the school.
 * Each classroom has a name and a set of allowed subjects.
 */
public class Classroom implements ITimetableFilter {
    private final int id;
    private final String name;
    private final Set<Subject> allowedSubjects;

    public Classroom(int id, String name, Set<Subject> allowedSubjects) {
        this.id = id;
        this.name = name;
        this.allowedSubjects = allowedSubjects;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Subject> getAllowedSubjects() {
        return allowedSubjects;
    }

    public boolean canAccommodate(Subject subject) {
        return allowedSubjects.contains(subject);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return id == classroom.id;
    }

    /**
     * Get the ITimetableFilter object from a lesson.
     * In this case, get classroom.
     *
     * @param lesson lesson
     * @return classroom
     */
    @Override
    public ITimetableFilter getFromLesson(Lesson lesson) {
        return lesson.getClassroom();
    }
}
