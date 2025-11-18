package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Unit tests for the SubjectConfig class.
 * Tests weekly lesson configuration, totals, and averages.
 */
@DisplayName("SubjectConfig Tests")
class SubjectConfigTest {

    @Test
    @DisplayName("Should return correct weekly lessons for Math")
    void testGetWeeklyLessonsForMath() {
        // When
        int weeklyLessons = SubjectConfig.getWeeklyLessons(Subject.MATH);

        // Then
        assertEquals(5, weeklyLessons);
    }

    @Test
    @DisplayName("Should return correct weekly lessons for Physics")
    void testGetWeeklyLessonsForPhysics() {
        // When
        int weeklyLessons = SubjectConfig.getWeeklyLessons(Subject.PHYSICS);

        // Then
        assertEquals(4, weeklyLessons);
    }

    @Test
    @DisplayName("Should return correct weekly lessons for Informatics")
    void testGetWeeklyLessonsForInformatics() {
        // When
        int weeklyLessons = SubjectConfig.getWeeklyLessons(Subject.INFORMATICS);

        // Then
        assertEquals(3, weeklyLessons);
    }

    @Test
    @DisplayName("Should return correct weekly lessons for Physical Culture")
    void testGetWeeklyLessonsForPhysicalCulture() {
        // When
        int weeklyLessons = SubjectConfig.getWeeklyLessons(Subject.PHYSICAL_CULTURE);

        // Then
        assertEquals(2, weeklyLessons);
    }

    @ParameterizedTest
    @DisplayName("All subjects should have at least 1 lesson per week")
    @EnumSource(Subject.class)
    void testAllSubjectsHaveLessons(Subject subject) {
        // When
        int weeklyLessons = SubjectConfig.getWeeklyLessons(subject);

        // Then
        assertTrue(weeklyLessons >= 1,
            "Subject " + subject + " should have at least 1 lesson per week");
    }

    @ParameterizedTest
    @DisplayName("All subjects should have reasonable lesson counts (not more than 6 per day)")
    @EnumSource(Subject.class)
    void testReasonableLessonCounts(Subject subject) {
        // When
        int weeklyLessons = SubjectConfig.getWeeklyLessons(subject);

        // Then
        assertTrue(weeklyLessons <= 6,
            "Subject " + subject + " should not exceed 6 lessons per week (max 1 per day + 1 extra)");
    }

    @Test
    @DisplayName("Should calculate correct total weekly lessons")
    void testGetTotalWeeklyLessons() {
        // When
        int totalLessons = SubjectConfig.getTotalWeeklyLessons();

        // Then
        // Math(5) + Physics(4) + Informatics(3) + Physical Culture(2) = 14
        assertEquals(14, totalLessons);
    }

    @Test
    @DisplayName("Total weekly lessons should match sum of individual subjects")
    void testTotalMatchesIndividualSum() {
        // Given
        int expectedTotal = 0;
        for (Subject subject : Subject.values()) {
            expectedTotal += SubjectConfig.getWeeklyLessons(subject);
        }

        // When
        int actualTotal = SubjectConfig.getTotalWeeklyLessons();

        // Then
        assertEquals(expectedTotal, actualTotal);
    }

    @Test
    @DisplayName("Should calculate correct average lessons per day")
    void testGetAverageLessonsPerDay() {
        // When
        double averageLessons = SubjectConfig.getAverageLessonsPerDay();

        // Then
        // 14 total lessons / 5 days = 2.8
        assertEquals(2.8, averageLessons, 0.001);
    }

    @Test
    @DisplayName("Average lessons per day should be total divided by 5")
    void testAverageLessonsCalculation() {
        // Given
        int totalLessons = SubjectConfig.getTotalWeeklyLessons();
        double expectedAverage = totalLessons / 5.0;

        // When
        double actualAverage = SubjectConfig.getAverageLessonsPerDay();

        // Then
        assertEquals(expectedAverage, actualAverage, 0.001);
    }

    @Test
    @DisplayName("Average lessons per day should be reasonable (not exceed max periods)")
    void testAverageIsReasonable() {
        // When
        double averageLessons = SubjectConfig.getAverageLessonsPerDay();

        // Then
        assertTrue(averageLessons > 0, "Average should be positive");
        assertTrue(averageLessons <= SchoolConfig.MAX_PERIODS_PER_DAY,
            "Average should not exceed maximum periods per day");
    }

    @Test
    @DisplayName("Total lessons should be positive")
    void testTotalLessonsIsPositive() {
        // When
        int totalLessons = SubjectConfig.getTotalWeeklyLessons();

        // Then
        assertTrue(totalLessons > 0, "Total lessons should be positive");
    }

    @Test
    @DisplayName("Total lessons for all groups should fit in a week")
    void testTotalLessonsFitInWeek() {
        // Given
        int totalLessonsPerGroup = SubjectConfig.getTotalWeeklyLessons();
        int maxLessonsPerWeek = SchoolConfig.WORKING_DAYS_PER_WEEK * SchoolConfig.MAX_PERIODS_PER_DAY;

        // Then
        assertTrue(totalLessonsPerGroup <= maxLessonsPerWeek,
            "Total lessons per group (" + totalLessonsPerGroup + ") should fit in a week (" + maxLessonsPerWeek + " slots)");
    }
}
