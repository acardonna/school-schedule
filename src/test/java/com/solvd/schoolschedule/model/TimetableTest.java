package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Timetable class.
 * Tests timetable operations, lesson filtering, copying, and fitness management.
 */
@DisplayName("Timetable Tests")
class TimetableTest {

    private Teacher mathTeacher;
    private Teacher physicsTeacher;
    private Classroom classroom1;
    private Classroom classroom2;
    private Group group1;
    private Group group2;

    @BeforeEach
    void setUp() {
        mathTeacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        physicsTeacher = new Teacher(2, "Ms. Johnson", Subject.PHYSICS);
        classroom1 = new Classroom(1, "Room 101", Set.of(Subject.MATH, Subject.PHYSICS));
        classroom2 = new Classroom(2, "Room 102", Set.of(Subject.MATH, Subject.PHYSICS));
        group1 = new Group(1, "Group 1");
        group2 = new Group(2, "Group 2");
    }

    @Test
    @DisplayName("Should create empty timetable with default constructor")
    void testEmptyTimetableCreation() {
        // When
        Timetable timetable = new Timetable();

        // Then
        assertNotNull(timetable);
        assertNotNull(timetable.getLessons());
        assertTrue(timetable.getLessons().isEmpty());
        assertEquals(0.0, timetable.getFitness());
    }

    @Test
    @DisplayName("Should create timetable with lesson list")
    void testTimetableCreationWithLessons() {
        // Given
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        Lesson lesson2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        List<Lesson> lessons = List.of(lesson1, lesson2);

        // When
        Timetable timetable = new Timetable(lessons);

        // Then
        assertNotNull(timetable);
        assertEquals(2, timetable.getLessons().size());
        assertTrue(timetable.getLessons().contains(lesson1));
        assertTrue(timetable.getLessons().contains(lesson2));
    }

    @Test
    @DisplayName("Should add lesson to timetable")
    void testAddLesson() {
        // Given
        Timetable timetable = new Timetable();
        Lesson lesson = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(1, 2), group1);

        // When
        timetable.addLesson(lesson);

        // Then
        assertEquals(1, timetable.getLessons().size());
        assertTrue(timetable.getLessons().contains(lesson));
    }

    @Test
    @DisplayName("Should add multiple lessons to timetable")
    void testAddMultipleLessons() {
        // Given
        Timetable timetable = new Timetable();
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        Lesson lesson2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        Lesson lesson3 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(1, 0), group2);

        // When
        timetable.addLesson(lesson1);
        timetable.addLesson(lesson2);
        timetable.addLesson(lesson3);

        // Then
        assertEquals(3, timetable.getLessons().size());
    }

    @Test
    @DisplayName("Should set and get fitness value")
    void testFitnessGetterAndSetter() {
        // Given
        Timetable timetable = new Timetable();

        // When
        timetable.setFitness(95.5);

        // Then
        assertEquals(95.5, timetable.getFitness(), 0.001);
    }

    @Test
    @DisplayName("Should have initial fitness of 0.0")
    void testInitialFitness() {
        // Given
        Timetable timetable = new Timetable();

        // Then
        assertEquals(0.0, timetable.getFitness());
    }

    @Test
    @DisplayName("Should copy timetable with all lessons")
    void testCopyTimetable() {
        // Given
        Timetable original = new Timetable();
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        Lesson lesson2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        original.addLesson(lesson1);
        original.addLesson(lesson2);
        original.setFitness(100.0);

        // When
        Timetable copy = original.copy();

        // Then
        assertNotNull(copy);
        assertEquals(original.getLessons().size(), copy.getLessons().size());
        assertTrue(copy.getLessons().containsAll(original.getLessons()));

        // Verify it's a new instance (not same reference)
        assertNotSame(original, copy);
        assertNotSame(original.getLessons(), copy.getLessons());
    }

    @Test
    @DisplayName("Should filter lessons for specific group")
    void testGetLessonsForGroup() {
        // Given
        Timetable timetable = new Timetable();
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        Lesson lesson2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        Lesson lesson3 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(1, 0), group2);
        timetable.addLesson(lesson1);
        timetable.addLesson(lesson2);
        timetable.addLesson(lesson3);

        // When
        List<Lesson> group1Lessons = timetable.getLessonsFor(group1);

        // Then
        assertEquals(2, group1Lessons.size());
        assertTrue(group1Lessons.contains(lesson1));
        assertTrue(group1Lessons.contains(lesson2));
        assertFalse(group1Lessons.contains(lesson3));
    }

    @Test
    @DisplayName("Should filter lessons for specific teacher")
    void testGetLessonsForTeacher() {
        // Given
        Timetable timetable = new Timetable();
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        Lesson lesson2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        Lesson lesson3 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(1, 0), group2);
        timetable.addLesson(lesson1);
        timetable.addLesson(lesson2);
        timetable.addLesson(lesson3);

        // When
        List<Lesson> mathTeacherLessons = timetable.getLessonsFor(mathTeacher);

        // Then
        assertEquals(2, mathTeacherLessons.size());
        assertTrue(mathTeacherLessons.contains(lesson1));
        assertTrue(mathTeacherLessons.contains(lesson3));
        assertFalse(mathTeacherLessons.contains(lesson2));
    }

    @Test
    @DisplayName("Should filter lessons for specific classroom")
    void testGetLessonsForClassroom() {
        // Given
        Timetable timetable = new Timetable();
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        Lesson lesson2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        Lesson lesson3 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(1, 0), group2);
        timetable.addLesson(lesson1);
        timetable.addLesson(lesson2);
        timetable.addLesson(lesson3);

        // When
        List<Lesson> classroom1Lessons = timetable.getLessonsFor(classroom1);

        // Then
        assertEquals(2, classroom1Lessons.size());
        assertTrue(classroom1Lessons.contains(lesson1));
        assertTrue(classroom1Lessons.contains(lesson3));
        assertFalse(classroom1Lessons.contains(lesson2));
    }

    @Test
    @DisplayName("Should get lessons for specific group on specific day")
    void testGetLessonsOnDayForGroup() {
        // Given
        Timetable timetable = new Timetable();
        Lesson monday1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);  // Monday
        Lesson monday2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);  // Monday
        Lesson tuesday = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(1, 0), group1);  // Tuesday
        timetable.addLesson(monday1);
        timetable.addLesson(monday2);
        timetable.addLesson(tuesday);

        // When
        List<Lesson> mondayLessons = timetable.getLessonsOnDayFor(group1, 0);

        // Then
        assertEquals(2, mondayLessons.size());
        assertTrue(mondayLessons.contains(monday1));
        assertTrue(mondayLessons.contains(monday2));
        assertFalse(mondayLessons.contains(tuesday));
    }

    @Test
    @DisplayName("Should return lessons sorted by period for a day")
    void testGetLessonsOnDaySortedByPeriod() {
        // Given
        Timetable timetable = new Timetable();
        Lesson period3 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 3), group1);
        Lesson period1 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        Lesson period5 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 5), group1);

        // Add in random order
        timetable.addLesson(period3);
        timetable.addLesson(period1);
        timetable.addLesson(period5);

        // When
        List<Lesson> dayLessons = timetable.getLessonsOnDayFor(group1, 0);

        // Then
        assertEquals(3, dayLessons.size());
        assertEquals(period1, dayLessons.get(0));  // Period 1 first
        assertEquals(period3, dayLessons.get(1));  // Period 3 second
        assertEquals(period5, dayLessons.get(2));  // Period 5 third
    }

    @Test
    @DisplayName("Should return empty list when no lessons for group on specific day")
    void testGetLessonsOnDayForGroupEmpty() {
        // Given
        Timetable timetable = new Timetable();
        Lesson monday = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        timetable.addLesson(monday);

        // When
        List<Lesson> fridayLessons = timetable.getLessonsOnDayFor(group1, 4);

        // Then
        assertTrue(fridayLessons.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when filtering with non-existent group")
    void testGetLessonsForNonExistentGroup() {
        // Given
        Timetable timetable = new Timetable();
        Lesson lesson = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        timetable.addLesson(lesson);
        Group nonExistentGroup = new Group(999, "Non-existent");

        // When
        List<Lesson> lessons = timetable.getLessonsFor(nonExistentGroup);

        // Then
        assertTrue(lessons.isEmpty());
    }

    @Test
    @DisplayName("Should handle timetable with lessons across all days")
    void testTimetableWithAllDays() {
        // Given
        Timetable timetable = new Timetable();
        for (int day = 0; day < 5; day++) {
            Lesson lesson = new Lesson(Subject.MATH, mathTeacher, classroom1,
                new TimeSlot(day, 0), group1);
            timetable.addLesson(lesson);
        }

        // When & Then
        for (int day = 0; day < 5; day++) {
            List<Lesson> dayLessons = timetable.getLessonsOnDayFor(group1, day);
            assertEquals(1, dayLessons.size(), "Should have 1 lesson on day " + day);
        }
    }

    @Test
    @DisplayName("Copy should not affect original when modified")
    void testCopyIndependence() {
        // Given
        Timetable original = new Timetable();
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, classroom1,
            new TimeSlot(0, 0), group1);
        original.addLesson(lesson1);

        // When
        Timetable copy = original.copy();
        Lesson lesson2 = new Lesson(Subject.PHYSICS, physicsTeacher, classroom2,
            new TimeSlot(0, 1), group1);
        copy.addLesson(lesson2);

        // Then
        assertEquals(1, original.getLessons().size(), "Original should have 1 lesson");
        assertEquals(2, copy.getLessons().size(), "Copy should have 2 lessons");
    }

    @Test
    @DisplayName("Should handle multiple periods on same day")
    void testMultiplePeriodsOnSameDay() {
        // Given
        Timetable timetable = new Timetable();
        for (int period = 0; period < 6; period++) {
            Lesson lesson = new Lesson(Subject.MATH, mathTeacher, classroom1,
                new TimeSlot(0, period), group1);
            timetable.addLesson(lesson);
        }

        // When
        List<Lesson> mondayLessons = timetable.getLessonsOnDayFor(group1, 0);

        // Then
        assertEquals(6, mondayLessons.size());
        // Verify sorting
        for (int i = 0; i < 6; i++) {
            assertEquals(i, mondayLessons.get(i).getTimeSlot().getPeriod());
        }
    }
}
