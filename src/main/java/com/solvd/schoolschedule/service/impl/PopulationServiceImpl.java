package com.solvd.schoolschedule.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.*;

/**
 * Service for managing genetic algorithm populations.
 * Handles initialization and population operations.
 */
public class PopulationServiceImpl implements IPopulationService {

    private final List<Teacher> teachers;
    private final List<Classroom> classrooms;
    private final List<Group> groups;
    private final Random random;

    public PopulationServiceImpl() {
        this.teachers = initializeTeachers();
        this.classrooms = initializeClassrooms();
        this.groups = initializeGroups();
        this.random = new Random();
    }

    /**
     * Initialize a population of random timetables
     * @param populationSize the size of the population
     * @return list of timetables
     */
    @Override
    public List<Timetable> initializePopulation(int populationSize) {
        List<Timetable> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomTimetable());
        }
        return population;
    }

    /**
     * Generate a random timetable with all required lessons
     * @return a randomly generated timetable
     */
    private Timetable generateRandomTimetable() {
        Timetable timetable = new Timetable();
        List<Lesson> lessons = new ArrayList<>();

        // Generate lessons for each group and subject
        for (Group group : groups) {
            for (Subject subject : Subject.values()) {
                int weeklyLessons = SubjectConfig.getWeeklyLessons(subject);
                for (int i = 0; i < weeklyLessons; i++) {
                    Lesson lesson = generateRandomLesson(subject, group);
                    lessons.add(lesson);
                }
            }
        }

        // Add all lessons to timetable
        for (Lesson lesson : lessons) {
            timetable.addLesson(lesson);
        }

        return timetable;
    }

    /**
     * Generate a random lesson for given subject and group
     * @param subject the subject
     * @param group the group
     * @return a random lesson
     */
    private Lesson generateRandomLesson(Subject subject, Group group) {
        Teacher teacher = getTeacherForSubject(subject);
        Classroom classroom = getRandomClassroomForSubject(subject);
        TimeSlot timeSlot = getRandomTimeSlot();

        return new Lesson(subject, teacher, classroom, timeSlot, group);
    }

    /**
     * Get teacher for a specific subject
     * @param subject the subject
     * @return the teacher for that subject
     */
    private Teacher getTeacherForSubject(Subject subject) {
        return teachers.stream()
                .filter(t -> t.getSubject() == subject)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No teacher found for subject: " + subject));
    }

    /**
     * Get a random classroom that can accommodate the subject
     * @param subject the subject
     * @return a suitable classroom
     */
    private Classroom getRandomClassroomForSubject(Subject subject) {
        List<Classroom> suitableClassrooms = classrooms.stream()
                .filter(c -> c.canAccommodate(subject))
                .toList();

        if (suitableClassrooms.isEmpty()) {
            throw new IllegalStateException("No classroom available for subject: " + subject);
        }

        return suitableClassrooms.get(random.nextInt(suitableClassrooms.size()));
    }

    /**
     * Get a random time slot
     * @return a random time slot
     */
    private TimeSlot getRandomTimeSlot() {
        int day = random.nextInt(SchoolConfig.WORKING_DAYS_PER_WEEK);
        int period = random.nextInt(SchoolConfig.MAX_PERIODS_PER_DAY);
        return new TimeSlot(day, period);
    }

    /**
     * Initialize teachers (one per subject)
     * @return list of teachers
     */
    private List<Teacher> initializeTeachers() {
        List<Teacher> teacherList = new ArrayList<>();
        teacherList.add(new Teacher(1, "Mr. Smith", Subject.MATH));
        teacherList.add(new Teacher(2, "Ms. Johnson", Subject.PHYSICS));
        teacherList.add(new Teacher(3, "Dr. Brown", Subject.INFORMATICS));
        teacherList.add(new Teacher(4, "Mrs. Davis", Subject.PHYSICAL_CULTURE));
        return teacherList;
    }

    /**
     * Initialize classrooms (some specialized)
     * @return list of classrooms
     */
    private List<Classroom> initializeClassrooms() {
        List<Classroom> classroomList = new ArrayList<>();
        Set<Subject> usualSubjects= Arrays.stream(Subject.values())
                .filter(s->!s.equals(Subject.INFORMATICS))
                .collect(Collectors.toSet());
        // General classrooms
        classroomList.add(new Classroom(1, "Room 101", usualSubjects));
        classroomList.add(new Classroom(2, "Room 102", usualSubjects));
        classroomList.add(new Classroom(3, "Room 103", usualSubjects));
        // Specialized classrooms
        classroomList.add(new Classroom(4, "Physics Lab", Set.of(Subject.PHYSICS)));
        classroomList.add(new Classroom(5, "Computer Lab", Set.of(Subject.INFORMATICS)));
        return classroomList;
    }

    /**
     * Initialize student groups
     * @return list of groups
     */
    private List<Group> initializeGroups() {
        List<Group> groupList = new ArrayList<>();
        for (int i = 1; i <= SchoolConfig.NUM_GROUPS; i++) {
            groupList.add(new Group(i, "Group " + i));
        }
        return groupList;
    }

    /**
     * Get all teachers
     * @return list of teachers
     */
    @Override
    public List<Teacher> getTeachers() {
        return teachers;
    }

    /**
     * Get all classrooms
     * @return list of classrooms
     */
    @Override
    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    /**
     * Get all groups
     * @return list of groups
     */
    @Override
    public List<Group> getGroups() {
        return groups;
    }
}
