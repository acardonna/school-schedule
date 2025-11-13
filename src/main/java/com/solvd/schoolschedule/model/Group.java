package com.solvd.schoolschedule.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a student group (class) in the school.
 * Each group has its own timetable.
 */
public class Group implements ITimetableFilter {
    private final int id;
    private final String name;
    private final int numberOfStudents;

    public Group(int id, String name, int numberOfStudents) {
        this.id = id;
        this.name = name;
        this.numberOfStudents = numberOfStudents;
    }

    public Group(int id, String name) {
        this(id, name, 30); // Default: 30 students per group
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Get the ITimetableFilter object from a lesson.
     * In this case, get group.
     *
     * @param lesson
     * @return group
     */
    @Override
    public ITimetableFilter getFromLesson(Lesson lesson){
        return lesson.getGroup();
    }

}
