package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Unit tests for the TimeSlot class.
 * Tests time slot creation, equality, hashing, and day name conversion.
 */
@DisplayName("TimeSlot Tests")
class TimeSlotTest {

    @Test
    @DisplayName("Should create TimeSlot with valid day and period")
    void testTimeSlotCreation() {
        // Given
        int day = 2;
        int period = 3;

        // When
        TimeSlot timeSlot = new TimeSlot(day, period);

        // Then
        assertEquals(day, timeSlot.getDay());
        assertEquals(period, timeSlot.getPeriod());
    }

    @ParameterizedTest
    @DisplayName("Should return correct day name for each day")
    @CsvSource({
        "0, Monday",
        "1, Tuesday",
        "2, Wednesday",
        "3, Thursday",
        "4, Friday"
    })
    void testGetDayName(int day, String expectedName) {
        // Given
        TimeSlot timeSlot = new TimeSlot(day, 0);

        // When
        String dayName = timeSlot.getDayName();

        // Then
        assertEquals(expectedName, dayName);
    }

    @Test
    @DisplayName("Should return 'Unknown' for invalid day")
    void testGetDayNameWithInvalidDay() {
        // Given
        TimeSlot timeSlot = new TimeSlot(99, 0);

        // When
        String dayName = timeSlot.getDayName();

        // Then
        assertEquals("Unknown", dayName);
    }

    @Test
    @DisplayName("Should return 'Unknown' for negative day")
    void testGetDayNameWithNegativeDay() {
        // Given
        TimeSlot timeSlot = new TimeSlot(-1, 0);

        // When
        String dayName = timeSlot.getDayName();

        // Then
        assertEquals("Unknown", dayName);
    }

    @Test
    @DisplayName("Two TimeSlots with same day and period should be equal")
    void testEquality() {
        // Given
        TimeSlot timeSlot1 = new TimeSlot(2, 3);
        TimeSlot timeSlot2 = new TimeSlot(2, 3);

        // Then
        assertEquals(timeSlot1, timeSlot2);
        assertEquals(timeSlot2, timeSlot1); // Symmetric
    }

    @Test
    @DisplayName("TimeSlot should be equal to itself")
    void testEqualityReflexive() {
        // Given
        TimeSlot timeSlot = new TimeSlot(1, 2);

        // Then
        assertEquals(timeSlot, timeSlot);
    }

    @Test
    @DisplayName("Two TimeSlots with different days should not be equal")
    void testInequalityDifferentDay() {
        // Given
        TimeSlot timeSlot1 = new TimeSlot(1, 3);
        TimeSlot timeSlot2 = new TimeSlot(2, 3);

        // Then
        assertNotEquals(timeSlot1, timeSlot2);
    }

    @Test
    @DisplayName("Two TimeSlots with different periods should not be equal")
    void testInequalityDifferentPeriod() {
        // Given
        TimeSlot timeSlot1 = new TimeSlot(2, 1);
        TimeSlot timeSlot2 = new TimeSlot(2, 2);

        // Then
        assertNotEquals(timeSlot1, timeSlot2);
    }

    @Test
    @DisplayName("TimeSlot should not equal null")
    void testNotEqualToNull() {
        // Given
        TimeSlot timeSlot = new TimeSlot(1, 2);

        // Then
        assertNotEquals(null, timeSlot);
    }

    @Test
    @DisplayName("TimeSlot should not equal object of different type")
    void testNotEqualToDifferentType() {
        // Given
        TimeSlot timeSlot = new TimeSlot(1, 2);
        String differentType = "Not a TimeSlot";

        // Then
        assertNotEquals(timeSlot, differentType);
    }

    @Test
    @DisplayName("Equal TimeSlots should have equal hash codes")
    void testHashCodeConsistency() {
        // Given
        TimeSlot timeSlot1 = new TimeSlot(2, 3);
        TimeSlot timeSlot2 = new TimeSlot(2, 3);

        // Then
        assertEquals(timeSlot1.hashCode(), timeSlot2.hashCode());
    }

    @Test
    @DisplayName("Different TimeSlots should likely have different hash codes")
    void testHashCodeDifference() {
        // Given
        TimeSlot timeSlot1 = new TimeSlot(0, 0);
        TimeSlot timeSlot2 = new TimeSlot(4, 5);

        // Then
        assertNotEquals(timeSlot1.hashCode(), timeSlot2.hashCode());
    }

    @Test
    @DisplayName("toString should include day name and period")
    void testToString() {
        // Given
        TimeSlot timeSlot = new TimeSlot(1, 2);

        // When
        String result = timeSlot.toString();

        // Then
        assertTrue(result.contains("Tuesday"));
        assertTrue(result.contains("Period 3")); // Period is 0-indexed, displayed as +1
    }

    @ParameterizedTest
    @DisplayName("toString should format all periods correctly")
    @CsvSource({
        "0, 0, 'Monday - Period 1'",
        "2, 3, 'Wednesday - Period 4'",
        "4, 5, 'Friday - Period 6'"
    })
    void testToStringFormatting(int day, int period, String expected) {
        // Given
        TimeSlot timeSlot = new TimeSlot(day, period);

        // When
        String result = timeSlot.toString();

        // Then
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("TimeSlots at boundary values should work correctly")
    void testBoundaryValues() {
        // Given - minimum values
        TimeSlot minTimeSlot = new TimeSlot(0, 0);

        // Given - maximum values (based on school config)
        TimeSlot maxTimeSlot = new TimeSlot(4, 5);

        // Then
        assertEquals("Monday", minTimeSlot.getDayName());
        assertEquals("Friday", maxTimeSlot.getDayName());
        assertNotEquals(minTimeSlot, maxTimeSlot);
    }
}
