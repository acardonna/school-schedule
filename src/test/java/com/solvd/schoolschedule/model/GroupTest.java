package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Group class.
 * Tests group creation, equality, ITimetableFilter integration.
 */
@DisplayName("Group Tests")
class GroupTest {

    @Test
    @DisplayName("Should create Group with all parameters")
    void testGroupCreationWithAllParameters() {
        // When
        Group group = new Group(1, "Group 1", 30);

        // Then
        assertNotNull(group);
        assertEquals(1, group.getId());
        assertEquals("Group 1", group.getName());
        assertEquals(30, group.getNumberOfStudents());
    }

    @Test
    @DisplayName("Should create Group with default number of students")
    void testGroupCreationWithDefaultStudents() {
        // When
        Group group = new Group(2, "Group 2");

        // Then
        assertNotNull(group);
        assertEquals(2, group.getId());
        assertEquals("Group 2", group.getName());
        assertEquals(30, group.getNumberOfStudents(), "Default should be 30 students");
    }

    @Test
    @DisplayName("Should return correct id")
    void testGetId() {
        // Given
        Group group = new Group(5, "Test Group", 25);

        // When
        int id = group.getId();

        // Then
        assertEquals(5, id);
    }

    @Test
    @DisplayName("Should return correct name")
    void testGetName() {
        // Given
        Group group = new Group(1, "Advanced Math Class", 28);

        // When
        String name = group.getName();

        // Then
        assertEquals("Advanced Math Class", name);
    }

    @Test
    @DisplayName("Should return correct number of students")
    void testGetNumberOfStudents() {
        // Given
        Group group = new Group(1, "Group 1", 35);

        // When
        int numberOfStudents = group.getNumberOfStudents();

        // Then
        assertEquals(35, numberOfStudents);
    }

    @Test
    @DisplayName("toString should return group name")
    void testToString() {
        // Given
        Group group = new Group(1, "Group Alpha");

        // When
        String result = group.toString();

        // Then
        assertEquals("Group Alpha", result);
    }

    @Test
    @DisplayName("Two Groups with same id should be equal")
    void testEquality() {
        // Given
        Group group1 = new Group(3, "Group A", 25);
        Group group2 = new Group(3, "Group B", 30); // Different name and students

        // Then
        assertEquals(group1, group2, "Groups with same id should be equal");
    }

    @Test
    @DisplayName("Group should be equal to itself")
    void testEqualityReflexive() {
        // Given
        Group group = new Group(1, "Group 1");

        // Then
        assertEquals(group, group);
    }

    @Test
    @DisplayName("Equality should be symmetric")
    void testEqualitySymmetric() {
        // Given
        Group group1 = new Group(2, "Group 1");
        Group group2 = new Group(2, "Group 1");

        // Then
        assertEquals(group1, group2);
        assertEquals(group2, group1);
    }

    @Test
    @DisplayName("Two Groups with different ids should not be equal")
    void testInequality() {
        // Given
        Group group1 = new Group(1, "Group 1");
        Group group2 = new Group(2, "Group 1"); // Same name, different id

        // Then
        assertNotEquals(group1, group2);
    }

    @Test
    @DisplayName("Group should not equal null")
    void testNotEqualToNull() {
        // Given
        Group group = new Group(1, "Group 1");

        // Then
        assertNotEquals(null, group);
    }

    @Test
    @DisplayName("Group should not equal object of different type")
    void testNotEqualToDifferentType() {
        // Given
        Group group = new Group(1, "Group 1");
        String differentType = "Group 1";

        // Then
        assertNotEquals(group, differentType);
    }

    @Test
    @DisplayName("Equal Groups should have equal hash codes")
    void testHashCodeConsistency() {
        // Given
        Group group1 = new Group(5, "Group A");
        Group group2 = new Group(5, "Group B");

        // Then
        assertEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    @DisplayName("Different Groups should likely have different hash codes")
    void testHashCodeDifference() {
        // Given
        Group group1 = new Group(1, "Group 1");
        Group group2 = new Group(2, "Group 2");

        // Then
        assertNotEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    @DisplayName("getFromLesson should return the lesson's group")
    void testGetFromLesson() {
        // Given
        Group group = new Group(1, "Group 1");
        Teacher teacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        Classroom classroom = new Classroom(1, "Room 101", Set.of(Subject.MATH));
        TimeSlot timeSlot = new TimeSlot(0, 0);
        Lesson lesson = new Lesson(Subject.MATH, teacher, classroom, timeSlot, group);

        // When
        var result = group.getFromLesson(lesson);

        // Then
        assertNotNull(result);
        assertEquals(group, result);
    }

    @Test
    @DisplayName("Should handle groups with different student counts")
    void testDifferentStudentCounts() {
        // Given
        Group smallGroup = new Group(1, "Small Group", 15);
        Group largeGroup = new Group(2, "Large Group", 45);

        // Then
        assertTrue(smallGroup.getNumberOfStudents() < largeGroup.getNumberOfStudents());
        assertEquals(15, smallGroup.getNumberOfStudents());
        assertEquals(45, largeGroup.getNumberOfStudents());
    }

    @Test
    @DisplayName("Should handle groups with zero students")
    void testZeroStudents() {
        // Given
        Group emptyGroup = new Group(1, "Empty Group", 0);

        // Then
        assertEquals(0, emptyGroup.getNumberOfStudents());
    }

    @Test
    @DisplayName("Should create multiple distinct groups")
    void testMultipleGroups() {
        // Given
        Group group1 = new Group(1, "Group 1");
        Group group2 = new Group(2, "Group 2");
        Group group3 = new Group(3, "Group 3");
        Group group4 = new Group(4, "Group 4");

        // Then
        assertNotEquals(group1, group2);
        assertNotEquals(group2, group3);
        assertNotEquals(group3, group4);
        assertEquals(4, Set.of(group1, group2, group3, group4).size());
    }
}
