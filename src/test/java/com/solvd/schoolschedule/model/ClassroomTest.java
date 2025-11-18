package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Classroom class.
 * Tests classroom creation, subject accommodation, equality, and ITimetableFilter integration.
 */
@DisplayName("Classroom Tests")
class ClassroomTest {

    @Test
    @DisplayName("Should create Classroom with all parameters")
    void testClassroomCreation() {
        // Given
        Set<Subject> allowedSubjects = Set.of(Subject.MATH, Subject.PHYSICS);

        // When
        Classroom classroom = new Classroom(1, "Room 101", allowedSubjects);

        // Then
        assertNotNull(classroom);
        assertEquals(1, classroom.getId());
        assertEquals("Room 101", classroom.getName());
        assertEquals(allowedSubjects, classroom.getAllowedSubjects());
    }

    @Test
    @DisplayName("Should return correct id")
    void testGetId() {
        // Given
        Classroom classroom = new Classroom(5, "Lab 202", Set.of(Subject.INFORMATICS));

        // When
        int id = classroom.getId();

        // Then
        assertEquals(5, id);
    }

    @Test
    @DisplayName("Should return correct name")
    void testGetName() {
        // Given
        Classroom classroom = new Classroom(1, "Physics Lab", Set.of(Subject.PHYSICS));

        // When
        String name = classroom.getName();

        // Then
        assertEquals("Physics Lab", name);
    }

    @Test
    @DisplayName("Should return correct allowed subjects")
    void testGetAllowedSubjects() {
        // Given
        Set<Subject> allowedSubjects = Set.of(Subject.MATH, Subject.PHYSICS);
        Classroom classroom = new Classroom(1, "Room 101", allowedSubjects);

        // When
        Set<Subject> result = classroom.getAllowedSubjects();

        // Then
        assertEquals(allowedSubjects, result);
        assertEquals(2, result.size());
        assertTrue(result.contains(Subject.MATH));
        assertTrue(result.contains(Subject.PHYSICS));
    }

    @Test
    @DisplayName("canAccommodate should return true for allowed subject")
    void testCanAccommodateAllowedSubject() {
        // Given
        Set<Subject> allowedSubjects = Set.of(Subject.MATH, Subject.PHYSICS);
        Classroom classroom = new Classroom(1, "Room 101", allowedSubjects);

        // When & Then
        assertTrue(classroom.canAccommodate(Subject.MATH));
        assertTrue(classroom.canAccommodate(Subject.PHYSICS));
    }

    @Test
    @DisplayName("canAccommodate should return false for non-allowed subject")
    void testCanAccommodateNonAllowedSubject() {
        // Given
        Set<Subject> allowedSubjects = Set.of(Subject.MATH, Subject.PHYSICS);
        Classroom classroom = new Classroom(1, "Room 101", allowedSubjects);

        // When & Then
        assertFalse(classroom.canAccommodate(Subject.INFORMATICS));
        assertFalse(classroom.canAccommodate(Subject.PHYSICAL_CULTURE));
    }

    @Test
    @DisplayName("Should create specialized classroom for single subject")
    void testSpecializedClassroom() {
        // Given
        Set<Subject> informaticsOnly = Set.of(Subject.INFORMATICS);
        Classroom computerLab = new Classroom(1, "Computer Lab", informaticsOnly);

        // When & Then
        assertTrue(computerLab.canAccommodate(Subject.INFORMATICS));
        assertFalse(computerLab.canAccommodate(Subject.MATH));
        assertFalse(computerLab.canAccommodate(Subject.PHYSICS));
        assertFalse(computerLab.canAccommodate(Subject.PHYSICAL_CULTURE));
    }

    @Test
    @DisplayName("Should create general classroom accommodating multiple subjects")
    void testGeneralClassroom() {
        // Given
        Set<Subject> generalSubjects = Set.of(Subject.MATH, Subject.PHYSICS, Subject.PHYSICAL_CULTURE);
        Classroom classroom = new Classroom(1, "Room 101", generalSubjects);

        // When & Then
        assertTrue(classroom.canAccommodate(Subject.MATH));
        assertTrue(classroom.canAccommodate(Subject.PHYSICS));
        assertTrue(classroom.canAccommodate(Subject.PHYSICAL_CULTURE));
        assertFalse(classroom.canAccommodate(Subject.INFORMATICS));
    }

    @Test
    @DisplayName("Should create classroom accommodating all subjects")
    void testUniversalClassroom() {
        // Given
        Set<Subject> allSubjects = Set.of(Subject.MATH, Subject.PHYSICS,
                                          Subject.INFORMATICS, Subject.PHYSICAL_CULTURE);
        Classroom classroom = new Classroom(1, "Multi-purpose Room", allSubjects);

        // When & Then
        assertTrue(classroom.canAccommodate(Subject.MATH));
        assertTrue(classroom.canAccommodate(Subject.PHYSICS));
        assertTrue(classroom.canAccommodate(Subject.INFORMATICS));
        assertTrue(classroom.canAccommodate(Subject.PHYSICAL_CULTURE));
    }

    @Test
    @DisplayName("Should handle empty allowed subjects set")
    void testEmptyAllowedSubjects() {
        // Given
        Set<Subject> emptySet = new HashSet<>();
        Classroom classroom = new Classroom(1, "Storage Room", emptySet);

        // When & Then
        assertFalse(classroom.canAccommodate(Subject.MATH));
        assertFalse(classroom.canAccommodate(Subject.PHYSICS));
        assertFalse(classroom.canAccommodate(Subject.INFORMATICS));
        assertFalse(classroom.canAccommodate(Subject.PHYSICAL_CULTURE));
    }

    @Test
    @DisplayName("toString should return classroom name")
    void testToString() {
        // Given
        Classroom classroom = new Classroom(1, "Physics Lab", Set.of(Subject.PHYSICS));

        // When
        String result = classroom.toString();

        // Then
        assertEquals("Physics Lab", result);
    }

    @Test
    @DisplayName("Two Classrooms with same id should be equal")
    void testEquality() {
        // Given
        Classroom classroom1 = new Classroom(3, "Room A", Set.of(Subject.MATH));
        Classroom classroom2 = new Classroom(3, "Room B", Set.of(Subject.PHYSICS)); // Different name and subjects

        // Then
        assertEquals(classroom1, classroom2, "Classrooms with same id should be equal");
    }

    @Test
    @DisplayName("Classroom should be equal to itself")
    void testEqualityReflexive() {
        // Given
        Classroom classroom = new Classroom(1, "Room 101", Set.of(Subject.MATH));

        // Then
        assertEquals(classroom, classroom);
    }

    @Test
    @DisplayName("Equality should be symmetric")
    void testEqualitySymmetric() {
        // Given
        Classroom classroom1 = new Classroom(5, "Lab", Set.of(Subject.INFORMATICS));
        Classroom classroom2 = new Classroom(5, "Lab", Set.of(Subject.INFORMATICS));

        // Then
        assertEquals(classroom1, classroom2);
        assertEquals(classroom2, classroom1);
    }

    @Test
    @DisplayName("Two Classrooms with different ids should not be equal")
    void testInequality() {
        // Given
        Classroom classroom1 = new Classroom(1, "Room 101", Set.of(Subject.MATH));
        Classroom classroom2 = new Classroom(2, "Room 101", Set.of(Subject.MATH)); // Same name and subjects, different id

        // Then
        assertNotEquals(classroom1, classroom2);
    }

    @Test
    @DisplayName("Classroom should not equal null")
    void testNotEqualToNull() {
        // Given
        Classroom classroom = new Classroom(1, "Room 101", Set.of(Subject.MATH));

        // Then
        assertNotEquals(null, classroom);
    }

    @Test
    @DisplayName("Classroom should not equal object of different type")
    void testNotEqualToDifferentType() {
        // Given
        Classroom classroom = new Classroom(1, "Room 101", Set.of(Subject.MATH));
        String differentType = "Room 101";

        // Then
        assertNotEquals(classroom, differentType);
    }

    @Test
    @DisplayName("getFromLesson should return the lesson's classroom")
    void testGetFromLesson() {
        // Given
        Classroom classroom = new Classroom(1, "Room 101", Set.of(Subject.MATH));
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        Group group = new Group(1, "Group 1");
        TimeSlot timeSlot = new TimeSlot(0, 0);
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // When
        var result = classroom.getFromLesson(lesson);

        // Then
        assertNotNull(result);
        assertEquals(classroom, result);
    }

    @Test
    @DisplayName("Should handle classroom names with special formats")
    void testSpecialClassroomNames() {
        // Given
        Classroom lab = new Classroom(1, "Lab-301", Set.of(Subject.INFORMATICS));
        Classroom gym = new Classroom(2, "Gymnasium #1", Set.of(Subject.PHYSICAL_CULTURE));
        Classroom room = new Classroom(3, "Room 2.04", Set.of(Subject.MATH));

        // Then
        assertEquals("Lab-301", lab.getName());
        assertEquals("Gymnasium #1", gym.getName());
        assertEquals("Room 2.04", room.getName());
    }

    @Test
    @DisplayName("Should create multiple distinct classrooms")
    void testMultipleClassrooms() {
        // Given
        Classroom room1 = new Classroom(1, "Room 101", Set.of(Subject.MATH, Subject.PHYSICS));
        Classroom room2 = new Classroom(2, "Room 102", Set.of(Subject.MATH, Subject.PHYSICS));
        Classroom lab = new Classroom(3, "Computer Lab", Set.of(Subject.INFORMATICS));
        Classroom physicsLab = new Classroom(4, "Physics Lab", Set.of(Subject.PHYSICS));

        // Then
        assertNotEquals(room1, room2);
        assertNotEquals(room2, lab);
        assertNotEquals(lab, physicsLab);
        assertEquals(4, Set.of(room1, room2, lab, physicsLab).size());
    }

    @Test
    @DisplayName("Allowed subjects should be independent for each classroom")
    void testIndependentAllowedSubjects() {
        // Given
        Classroom classroom1 = new Classroom(1, "Room 101", Set.of(Subject.MATH));
        Classroom classroom2 = new Classroom(2, "Lab", Set.of(Subject.INFORMATICS));

        // When & Then
        assertTrue(classroom1.canAccommodate(Subject.MATH));
        assertFalse(classroom1.canAccommodate(Subject.INFORMATICS));

        assertFalse(classroom2.canAccommodate(Subject.MATH));
        assertTrue(classroom2.canAccommodate(Subject.INFORMATICS));
    }
}
