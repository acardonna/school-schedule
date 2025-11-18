package com.solvd.schoolschedule.model;

import com.solvd.schoolschedule.model.interfaces.ITimetableFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a timetable for a school.
 */
public class Timetable {
    private final List<Lesson> lessons;
    private double fitness;
    private int generation;

    public Timetable() {
        this.lessons = new ArrayList<>();
        this.fitness = 0.0;
        this.generation=0;
    }

    public Timetable(List<Lesson> lessons) {
        this.lessons = new ArrayList<>(lessons);
        this.fitness = 0.0;
        this.generation=0;
    }

    public Timetable(List<Lesson> lessons, int generation) {
        this.lessons = new ArrayList<>(lessons);
        this.fitness = 0.0;
        this.generation=generation;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public Timetable copy() {
        return new Timetable(new ArrayList<>(lessons));
    }

    /**
     * Returns the lesson list of an ITimetableFilter object.
     *
     * @param object (Group, Teacher or Classroom)
     * @return dayLessons
     */
    public List<Lesson> getLessonsFor(ITimetableFilter object) {
        return object.filter(this);
    }

    /**
     * Get all lessons for a specific ITimetableFilter object on a specific day, sorted by period
     *
     * @param object (Group, Teacher or Classroom)
     * @param day    the day to filter by (0-4)
     * @return list of lessons for the teacher on that day, sorted by period
     */
    public List<Lesson> getLessonsOnDayFor(ITimetableFilter object, int day) {
        List<Lesson> lessons = this.getLessonsFor(object);
        return filterByDay(lessons, day);
    }

    /**
     * Filters a lesson list  by a specific day, sorted by period
     *
     * @param lessons list of the lessons
     * @param day     the day to filter by (0-4)
     * @return list of lessons on that day, sorted by period
     */
    private List<Lesson> filterByDay(List<Lesson> lessons, int day) {
        return lessons.stream()
                .filter(lesson -> lesson.getTimeSlot().getDay() == day)
                .sorted(Comparator.comparingInt(l -> l.getTimeSlot().getPeriod()))
                .collect(Collectors.toList());
    }

}