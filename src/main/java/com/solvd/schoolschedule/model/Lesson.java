package com.solvd.schoolschedule.model;

import com.solvd.schoolschedule.model.interfaces.ITimetableFilter;

/**
 * Represents a lesson in the school schedule.
 */
public class Lesson {
    private final Subject subject;
    private final Teacher teacher;
    private final Classroom classroom;
    private final TimeSlot timeSlot;
    private final Group group;
    private boolean conflicted = false;


    public Lesson(Subject subject, Teacher teacher, Classroom classroom, TimeSlot timeSlot, Group group) {
        this.subject = subject;
        this.teacher = teacher;
        this.classroom = classroom;
        this.timeSlot = timeSlot;
        this.group = group;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Group getGroup() {
        return group;
    }

    public boolean isConflicted() {
        return conflicted;
    }

    public void setConflicted(boolean conflicted) {
        this.conflicted = conflicted;
    }

    @Override
    public String toString() {
        return subject.getDisplayName() + " - " + teacher.getName() + " - " + classroom.getName();
    }

    /**
     * Returns the ITimetableFilter object (Group, Teacher or Classroom), from the lesson.
     *
     * @param object ITimetableFilter object
     * @return object ITimetableFilter (Group, Teacher or Classroom)
     */
    public ITimetableFilter get(ITimetableFilter object) {
        return object.getFromLesson(this);
    }
}
