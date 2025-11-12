package com.solvd.schoolschedule.model;

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

    public Timetable() {
        this.lessons = new ArrayList<>();
        this.fitness = 0.0;
    }

    public Timetable(List<Lesson> lessons) {
        this.lessons = new ArrayList<>(lessons);
        this.fitness = 0.0;
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

    public Timetable copy() {
        return new Timetable(new ArrayList<>(lessons));
    }

    /**
     * Get all lessons for a specific group
     * @param group the group to filter by
     * @return list of lessons for the group
     */
    public List<Lesson> getLessonsForGroup(Group group) {
        return lessons.stream()
                .filter(lesson -> lesson.getGroup().equals(group))
                .collect(Collectors.toList());
    }

    /**
     * Get all lessons for a specific teacher
     * @param teacher the teacher to filter by
     * @return list of lessons for the teacher
     */
    public List<Lesson> getLessonsForTeacher(Teacher teacher) {
        return lessons.stream()
                .filter(lesson -> lesson.getTeacher().equals(teacher))
                .collect(Collectors.toList());
    }


    public List<Lesson> getLessonsForClassroom(Classroom classroom) {
        return lessons.stream()
                .filter(lesson -> lesson.getClassroom().equals(classroom))
                .collect(Collectors.toList());
    }


    /**
     * Returns the lesson list of an ITimetableFilter object.
     *
     *@param object (Group, Teacher or Classroom)
     * @return dayLessons
     */
    public List<Lesson> getLessonsFor(ITimetableFilter object) {
        return object.filter(this);
    }

    public List<Lesson> filterByDay(List<Lesson> lessons,int day) {
        return lessons.stream()
                .filter(lesson -> lesson.getTimeSlot().getDay() == day)
                .sorted(Comparator.comparingInt(l -> l.getTimeSlot().getPeriod()))
                .collect(Collectors.toList());
    }

    /**
     * Get all lessons for a specific group on a specific day, sorted by period
     * @param group the group to filter by
     * @param day the day to filter by (0-4)
     * @return list of lessons for the group on that day, sorted by period
     */
    public List<Lesson> getLessonsForGroupOnDay(Group group, int day) {
        List<Lesson> lessons=this.getLessonsFor(group);
        return filterByDay(lessons,day);
    }

    /**
     * Get all lessons for a specific teacher on a specific day, sorted by period
     * @param teacher the teacher to filter by
     * @param day the day to filter by (0-4)
     * @return list of lessons for the teacher on that day, sorted by period
     */
    public List<Lesson> getLessonsForTeacherOnDay(Teacher teacher, int day) {
        List<Lesson> lessons=this.getLessonsFor(teacher);
        return filterByDay(lessons,day);
    }

    /**
     * Get all lessons for a specific ITimetableFilter object on a specific day, sorted by period
     *@param object (Group, Teacher or Classroom)
     * @param day the day to filter by (0-4)
     * @return list of lessons for the teacher on that day, sorted by period
     */
    public List<Lesson> getLessonsOnDay(ITimetableFilter object, int day) {
        List<Lesson> lessons=this.getLessonsFor(object);
        return filterByDay(lessons,day);
    }

}