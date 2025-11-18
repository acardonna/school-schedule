package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Unit tests for the Teacher class.
 * Tests teacher creation, subject assignment, equality, and ITimetableFilter integration.
 */
@DisplayName("Teacher Tests")
class TeacherTest {

    @Test
    @DisplayName("Should create Teacher with all parameters")
    void testTeacherCreation() {
        // When
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);

        // Then
        assertNotNull(teacher);
        assertEquals(1, teacher.getId());
        assertEquals("Mr. Smith", teacher.getName());
        assertEquals(Subject.MATH, teacher.getSubject());
    }

    @Test
    @DisplayName("Should return correct id")
    void testGetId() {
        // Given
        Teacher teacher = new Teacher(5, "Dr. Brown", Subject.INFORMATICS);

        // When
        int id = teacher.getId();

        // Then
        assertEquals(5, id);
    }

    @Test
    @DisplayName("Should return correct name")
    void testGetName() {
        // Given
        Teacher teacher = new Teacher(1, "Ms. Johnson", Subject.PHYSICS);

        // When
        String name = teacher.getName();

        // Then
        assertEquals("Ms. Johnson", name);
    }

    @Test
    @DisplayName("Should return correct subject")
    void testGetSubject() {
        // Given
        Teacher teacher = new Teacher(1, "Mrs. Davis", Subject.PHYSICAL_CULTURE);

        // When
        Subject subject = teacher.getSubject();

        // Then
        assertEquals(Subject.PHYSICAL_CULTURE, subject);
    }

    @ParameterizedTest
    @DisplayName("Should create teachers for all subjects")
    @EnumSource(Subject.class)
    void testTeacherForAllSubjects(Subject subject) {
        // When
        Teacher teacher = new Teacher(1, "Test Teacher", subject);

        // Then
        assertEquals(subject, teacher.getSubject());
    }

    @Test
    @DisplayName("toString should include name and subject")
    void testToString() {
        // Given
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);

        // When
        String result = teacher.toString();

        // Then
        assertTrue(result.contains("Mr. Smith"));
        assertTrue(result.contains("Mathematics"));
    }

    @Test
    @DisplayName("toString should format as 'Name (Subject)'")
    void testToStringFormat() {
        // Given
        Teacher teacher = new Teacher(2, "Ms. Johnson", Subject.PHYSICS);

        // When
        String result = teacher.toString();

        // Then
        assertEquals("Ms. Johnson (Physics)", result);
    }

    @Test
    @DisplayName("Two Teachers with same id should be equal")
    void testEquality() {
        // Given
        Teacher teacher1 = new Teacher(3, "Mr. Smith", Subject.MATH);
        Teacher teacher2 = new Teacher(3, "Ms. Johnson", Subject.PHYSICS); // Different name and subject

        // Then
        assertEquals(teacher1, teacher2, "Teachers with same id should be equal");
    }

    @Test
    @DisplayName("Teacher should be equal to itself")
    void testEqualityReflexive() {
        // Given
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);

        // Then
        assertEquals(teacher, teacher);
    }

    @Test
    @DisplayName("Equality should be symmetric")
    void testEqualitySymmetric() {
        // Given
        Teacher teacher1 = new Teacher(5, "Test Teacher", Subject.MATH);
        Teacher teacher2 = new Teacher(5, "Test Teacher", Subject.MATH);

        // Then
        assertEquals(teacher1, teacher2);
        assertEquals(teacher2, teacher1);
    }

    @Test
    @DisplayName("Two Teachers with different ids should not be equal")
    void testInequality() {
        // Given
        Teacher teacher1 = new Teacher(1, "Mr. Smith", Subject.MATH);
        Teacher teacher2 = new Teacher(2, "Mr. Smith", Subject.MATH); // Same name and subject, different id

        // Then
        assertNotEquals(teacher1, teacher2);
    }

    @Test
    @DisplayName("Teacher should not equal null")
    void testNotEqualToNull() {
        // Given
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);

        // Then
        assertNotEquals(null, teacher);
    }

    @Test
    @DisplayName("Teacher should not equal object of different type")
    void testNotEqualToDifferentType() {
        // Given
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        String differentType = "Mr. Smith";

        // Then
        assertNotEquals(teacher, differentType);
    }

    @Test
    @DisplayName("Equal Teachers should have same identity based on id")
    void testEqualityBasedOnId() {
        // Given
        Teacher teacher1 = new Teacher(7, "Teacher A", Subject.MATH);
        Teacher teacher2 = new Teacher(7, "Teacher B", Subject.PHYSICS);

        // Then
        assertEquals(teacher1, teacher2, "Teachers with same id should be equal regardless of name or subject");
    }

    @Test
    @DisplayName("getFromLesson should return the lesson's teacher")
    void testGetFromLesson() {
        // Given
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        Group group = new Group(1, "Group 1");
        Classroom classroom = new Classroom(1, "Room 101", Set.of(Subject.MATH));
        TimeSlot timeSlot = new TimeSlot(0, 0);
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // When
        var result = teacher.getFromLesson(lesson);

        // Then
        assertNotNull(result);
        assertEquals(teacher, result);
    }

    @Test
    @DisplayName("Should create teachers with different subjects")
    void testDifferentSubjects() {
        // Given
        Teacher mathTeacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        Teacher physicsTeacher = new Teacher(2, "Ms. Johnson", Subject.PHYSICS);
        Teacher informaticsTeacher = new Teacher(3, "Dr. Brown", Subject.INFORMATICS);
        Teacher physCulTeacher = new Teacher(4, "Mrs. Davis", Subject.PHYSICAL_CULTURE);

        // Then
        assertEquals(Subject.MATH, mathTeacher.getSubject());
        assertEquals(Subject.PHYSICS, physicsTeacher.getSubject());
        assertEquals(Subject.INFORMATICS, informaticsTeacher.getSubject());
        assertEquals(Subject.PHYSICAL_CULTURE, physCulTeacher.getSubject());
    }

    @Test
    @DisplayName("Should handle teachers with special characters in name")
    void testSpecialCharactersInName() {
        // Given
        Teacher teacher = new Teacher(1, "O'Brien-Smith", Subject.MATH);

        // Then
        assertEquals("O'Brien-Smith", teacher.getName());
        assertTrue(teacher.toString().contains("O'Brien-Smith"));
    }

    @Test
    @DisplayName("Each subject should map to correct display name in toString")
    void testSubjectDisplayNames() {
        // Given
        Teacher mathTeacher = new Teacher(1, "Teacher", Subject.MATH);
        Teacher physicsTeacher = new Teacher(2, "Teacher", Subject.PHYSICS);
        Teacher informaticsTeacher = new Teacher(3, "Teacher", Subject.INFORMATICS);
        Teacher physCulTeacher = new Teacher(4, "Teacher", Subject.PHYSICAL_CULTURE);

        // Then
        assertTrue(mathTeacher.toString().contains("Mathematics"));
        assertTrue(physicsTeacher.toString().contains("Physics"));
        assertTrue(informaticsTeacher.toString().contains("Informatics"));
        assertTrue(physCulTeacher.toString().contains("Physical Culture"));
    }

    @Test
    @DisplayName("Should create multiple distinct teachers")
    void testMultipleTeachers() {
        // Given
        Teacher teacher1 = new Teacher(1, "Mr. Smith", Subject.MATH);
        Teacher teacher2 = new Teacher(2, "Ms. Johnson", Subject.PHYSICS);
        Teacher teacher3 = new Teacher(3, "Dr. Brown", Subject.INFORMATICS);
        Teacher teacher4 = new Teacher(4, "Mrs. Davis", Subject.PHYSICAL_CULTURE);

        // Then
        assertNotEquals(teacher1, teacher2);
        assertNotEquals(teacher2, teacher3);
        assertNotEquals(teacher3, teacher4);
        assertEquals(4, Set.of(teacher1, teacher2, teacher3, teacher4).size());
    }
}
