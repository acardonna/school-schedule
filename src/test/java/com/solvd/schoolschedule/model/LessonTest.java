package com.solvd.schoolschedule.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Lesson class.
 * Tests lesson creation, getters, toString formatting, and ITimetableFilter integration.
 */
@DisplayName("Lesson Tests")
class LessonTest {

    private Teacher teacher;
    private Classroom classroom;
    private TimeSlot timeSlot;
    private Group group;

    @BeforeEach
    void setUp() {
        // Arrange common test objects
        teacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        classroom = new Classroom(1, "Room 101", Set.of(Subject.MATH, Subject.PHYSICS));
        timeSlot = new TimeSlot(2, 3);
        group = new Group(1, "Group 1", 30);
    }

    @Test
    @DisplayName("Should create Lesson with all required fields")
    void testLessonCreation() {
        // When
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // Then
        assertNotNull(lesson);
        assertEquals(Subject.MATH, lesson.getSubject());
        assertEquals(teacher, lesson.getTeacher());
        assertEquals(classroom, lesson.getClassroom());
        assertEquals(timeSlot, lesson.getTimeSlot());
        assertEquals(group, lesson.getGroup());
    }

    @Test
    @DisplayName("Should return correct subject")
    void testGetSubject() {
        // Given
        Lesson lesson = new Lesson(Subject.PHYSICS, teacher, classroom, timeSlot, group);

        // When
        Subject subject = lesson.getSubject();

        // Then
        assertEquals(Subject.PHYSICS, subject);
    }

    @Test
    @DisplayName("Should return correct teacher")
    void testGetTeacher() {
        // Given
        Teacher anotherTeacher = new Teacher(2, "Ms. Johnson", Subject.PHYSICS);
        Lesson lesson = new Lesson(Subject.MATH, anotherTeacher, classroom, timeSlot, group);

        // When
        Teacher result = lesson.getTeacher();

        // Then
        assertEquals(anotherTeacher, result);
        assertEquals("Ms. Johnson", result.getName());
    }

    @Test
    @DisplayName("Should return correct classroom")
    void testGetClassroom() {
        // Given
        Classroom anotherClassroom = new Classroom(2, "Room 202", Set.of(Subject.INFORMATICS));
        Lesson lesson = new Lesson(Subject.MATH, teacher, anotherClassroom, timeSlot, group);

        // When
        Classroom result = lesson.getClassroom();

        // Then
        assertEquals(anotherClassroom, result);
        assertEquals("Room 202", result.getName());
    }

    @Test
    @DisplayName("Should return correct time slot")
    void testGetTimeSlot() {
        // Given
        TimeSlot anotherTimeSlot = new TimeSlot(4, 5);
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, anotherTimeSlot, group);

        // When
        TimeSlot result = lesson.getTimeSlot();

        // Then
        assertEquals(anotherTimeSlot, result);
        assertEquals(4, result.getDay());
        assertEquals(5, result.getPeriod());
    }

    @Test
    @DisplayName("Should return correct group")
    void testGetGroup() {
        // Given
        Group anotherGroup = new Group(2, "Group 2", 25);
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, anotherGroup);

        // When
        Group result = lesson.getGroup();

        // Then
        assertEquals(anotherGroup, result);
        assertEquals("Group 2", result.getName());
    }

    @Test
    @DisplayName("toString should include subject, teacher, and classroom")
    void testToString() {
        // Given
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // When
        String result = lesson.toString();

        // Then
        assertTrue(result.contains("Mathematics"), "Should contain subject display name");
        assertTrue(result.contains("Mr. Smith"), "Should contain teacher name");
        assertTrue(result.contains("Room 101"), "Should contain classroom name");
    }

    @Test
    @DisplayName("toString should format as 'Subject - Teacher - Classroom'")
    void testToStringFormat() {
        // Given
        Lesson lesson = new Lesson(Subject.PHYSICS, teacher, classroom, timeSlot, group);

        // When
        String result = lesson.toString();

        // Then
        String expected = "Physics - Mr. Smith - Room 101";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("get() with Group filter should return the lesson's group")
    void testGetWithGroupFilter() {
        // Given
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // When
        Group emptyGroup = new Group(999, "Filter Group");
        var result = lesson.get(emptyGroup);

        // Then
        assertNotNull(result);
        assertEquals(group, result);
    }

    @Test
    @DisplayName("get() with Teacher filter should return the lesson's teacher")
    void testGetWithTeacherFilter() {
        // Given
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // When
        Teacher filterTeacher = new Teacher(999, "Filter Teacher", Subject.PHYSICS);
        var result = lesson.get(filterTeacher);

        // Then
        assertNotNull(result);
        assertEquals(teacher, result);
    }

    @Test
    @DisplayName("get() with Classroom filter should return the lesson's classroom")
    void testGetWithClassroomFilter() {
        // Given
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // When
        Classroom filterClassroom = new Classroom(999, "Filter Room", Set.of(Subject.MATH));
        var result = lesson.get(filterClassroom);

        // Then
        assertNotNull(result);
        assertEquals(classroom, result);
    }

    @Test
    @DisplayName("Should create lessons with different subjects")
    void testDifferentSubjects() {
        // When
        Lesson mathLesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);
        Lesson physicsLesson = new Lesson(Subject.PHYSICS, teacher, classroom, timeSlot, group);
        Lesson informaticsLesson = new Lesson(Subject.INFORMATICS, teacher, classroom, timeSlot, group);
        Lesson physCulLesson = new Lesson(Subject.PHYSICAL_CULTURE, teacher, classroom, timeSlot, group);

        // Then
        assertEquals(Subject.MATH, mathLesson.getSubject());
        assertEquals(Subject.PHYSICS, physicsLesson.getSubject());
        assertEquals(Subject.INFORMATICS, informaticsLesson.getSubject());
        assertEquals(Subject.PHYSICAL_CULTURE, physCulLesson.getSubject());
    }

    @Test
    @DisplayName("Should handle lessons with same time slot but different groups")
    void testSameTimeSlotDifferentGroups() {
        // Given
        Group group1 = new Group(1, "Group 1");
        Group group2 = new Group(2, "Group 2");
        TimeSlot sameTimeSlot = new TimeSlot(1, 2);

        // When
        Lesson lesson1 = new Lesson(Subject.MATH, teacher, classroom, sameTimeSlot, group1);
        Lesson lesson2 = new Lesson(Subject.MATH, teacher, classroom, sameTimeSlot, group2);

        // Then
        assertNotEquals(lesson1.getGroup(), lesson2.getGroup());
        assertEquals(lesson1.getTimeSlot(), lesson2.getTimeSlot());
    }
}
