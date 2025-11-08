package com.solvd.schoolschedule.model;

/**
 * Represents a lesson in the school schedule.
 */
public class Lesson {
    private final Subject subject;
    private final Teacher teacher;
    private final Classroom classroom;
    private final TimeSlot timeSlot;
    private final Group group;

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

    @Override
    public String toString() {
        return subject.getDisplayName() + " - " + teacher.getName() + " - " + classroom.getName();
    }
}
