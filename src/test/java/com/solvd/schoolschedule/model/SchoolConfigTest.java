package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the SchoolConfig class.
 * Tests configuration constants and utility class pattern.
 */
@DisplayName("SchoolConfig Tests")
class SchoolConfigTest {

    @Test
    @DisplayName("Working days per week should be 5")
    void testWorkingDaysPerWeek() {
        assertEquals(5, SchoolConfig.WORKING_DAYS_PER_WEEK);
    }

    @Test
    @DisplayName("Max periods per day should be 6")
    void testMaxPeriodsPerDay() {
        assertEquals(6, SchoolConfig.MAX_PERIODS_PER_DAY);
    }

    @Test
    @DisplayName("Number of classrooms should be 5")
    void testNumberOfClassrooms() {
        assertEquals(5, SchoolConfig.NUM_CLASSROOMS);
    }

    @Test
    @DisplayName("Number of teachers should be 8")
    void testNumberOfTeachers() {
        assertEquals(8, SchoolConfig.NUM_TEACHERS);
    }

    @Test
    @DisplayName("Number of groups should be 4")
    void testNumberOfGroups() {
        assertEquals(4, SchoolConfig.NUM_GROUPS);
    }

    @Test
    @DisplayName("GA population size should be 100")
    void testGAPopulationSize() {
        assertEquals(100, SchoolConfig.GA_POPULATION_SIZE);
    }

    @Test
    @DisplayName("GA max generations should be 400")
    void testGAMaxGenerations() {
        assertEquals(400, SchoolConfig.GA_MAX_GENERATIONS);
    }

    @Test
    @DisplayName("GA mutation rate should be 0.05")
    void testGAMutationRate() {
        assertEquals(0.05, SchoolConfig.GA_MUTATION_RATE, 0.0001);
    }

    @Test
    @DisplayName("GA tournament size should be 5")
    void testGATournamentSize() {
        assertEquals(5, SchoolConfig.GA_TOURNAMENT_SIZE);
    }

    @Test
    @DisplayName("Progress update frequency should be 100")
    void testProgressUpdateFrequency() {
        assertEquals(100, SchoolConfig.PROGRESS_UPDATE_FREQUENCY);
    }

    @Test
    @DisplayName("Day names array should have 5 elements")
    void testDayNamesLength() {
        assertEquals(5, SchoolConfig.DAY_NAMES.length);
    }

    @Test
    @DisplayName("Day names should be in correct order")
    void testDayNamesOrder() {
        assertEquals("Monday", SchoolConfig.DAY_NAMES[0]);
        assertEquals("Tuesday", SchoolConfig.DAY_NAMES[1]);
        assertEquals("Wednesday", SchoolConfig.DAY_NAMES[2]);
        assertEquals("Thursday", SchoolConfig.DAY_NAMES[3]);
        assertEquals("Friday", SchoolConfig.DAY_NAMES[4]);
    }

    @Test
    @DisplayName("All day names should be non-null and non-empty")
    void testDayNamesNotEmpty() {
        for (String dayName : SchoolConfig.DAY_NAMES) {
            assertNotNull(dayName);
            assertFalse(dayName.isEmpty());
        }
    }

    @Test
    @DisplayName("Working days should be positive")
    void testWorkingDaysPositive() {
        assertTrue(SchoolConfig.WORKING_DAYS_PER_WEEK > 0);
    }

    @Test
    @DisplayName("Max periods should be positive")
    void testMaxPeriodsPositive() {
        assertTrue(SchoolConfig.MAX_PERIODS_PER_DAY > 0);
    }

    @Test
    @DisplayName("Number of classrooms should be positive")
    void testNumberOfClassroomsPositive() {
        assertTrue(SchoolConfig.NUM_CLASSROOMS > 0);
    }

    @Test
    @DisplayName("Number of teachers should be positive")
    void testNumberOfTeachersPositive() {
        assertTrue(SchoolConfig.NUM_TEACHERS > 0);
    }

    @Test
    @DisplayName("Number of groups should be positive")
    void testNumberOfGroupsPositive() {
        assertTrue(SchoolConfig.NUM_GROUPS > 0);
    }

    @Test
    @DisplayName("GA population size should be positive")
    void testGAPopulationSizePositive() {
        assertTrue(SchoolConfig.GA_POPULATION_SIZE > 0);
    }

    @Test
    @DisplayName("GA max generations should be positive")
    void testGAMaxGenerationsPositive() {
        assertTrue(SchoolConfig.GA_MAX_GENERATIONS > 0);
    }

    @Test
    @DisplayName("GA mutation rate should be between 0 and 1")
    void testGAMutationRateValid() {
        assertTrue(SchoolConfig.GA_MUTATION_RATE >= 0.0);
        assertTrue(SchoolConfig.GA_MUTATION_RATE <= 1.0);
    }

    @Test
    @DisplayName("GA tournament size should be positive")
    void testGATournamentSizePositive() {
        assertTrue(SchoolConfig.GA_TOURNAMENT_SIZE > 0);
    }

    @Test
    @DisplayName("GA tournament size should not exceed population size")
    void testGATournamentSizeReasonable() {
        assertTrue(SchoolConfig.GA_TOURNAMENT_SIZE <= SchoolConfig.GA_POPULATION_SIZE);
    }

    @Test
    @DisplayName("Progress update frequency should be positive")
    void testProgressUpdateFrequencyPositive() {
        assertTrue(SchoolConfig.PROGRESS_UPDATE_FREQUENCY > 0);
    }

    @Test
    @DisplayName("Progress update frequency should not exceed max generations")
    void testProgressUpdateFrequencyReasonable() {
        assertTrue(SchoolConfig.PROGRESS_UPDATE_FREQUENCY <= SchoolConfig.GA_MAX_GENERATIONS);
    }

    @Test
    @DisplayName("Total weekly slots should be sufficient for lessons")
    void testSufficientWeeklySlots() {
        // Given
        int totalSlotsPerWeek = SchoolConfig.WORKING_DAYS_PER_WEEK * SchoolConfig.MAX_PERIODS_PER_DAY;
        int totalLessonsPerWeek = SubjectConfig.getTotalWeeklyLessons();

        // Then
        assertTrue(totalSlotsPerWeek >= totalLessonsPerWeek,
            "Weekly slots (" + totalSlotsPerWeek + ") should be sufficient for lessons (" + totalLessonsPerWeek + ")");
    }

    @Test
    @DisplayName("Number of teachers should be at least number of subjects")
    void testSufficientTeachers() {
        // Given
        int numberOfSubjects = Subject.values().length;

        // Then
        assertTrue(SchoolConfig.NUM_TEACHERS >= numberOfSubjects,
            "Number of teachers (" + SchoolConfig.NUM_TEACHERS + ") should be at least the number of subjects (" + numberOfSubjects + ")");
    }

    @Test
    @DisplayName("Constructor should throw exception when instantiated (utility class)")
    void testConstructorThrowsException() {
        // This test verifies that SchoolConfig is designed as a utility class
        // and cannot be instantiated. Reflection wraps the AssertionError in InvocationTargetException
        Exception exception = assertThrows(Exception.class, () -> {
            // Use reflection to access private constructor
            var constructor = SchoolConfig.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // Verify the cause is AssertionError (wrapped by InvocationTargetException)
        assertTrue(exception.getCause() instanceof AssertionError,
            "Constructor should throw AssertionError to prevent instantiation");
    }

    @Test
    @DisplayName("Configuration constants should be reasonable for scheduling")
    void testReasonableConfiguration() {
        // Working days should be 5 (standard school week)
        assertEquals(5, SchoolConfig.WORKING_DAYS_PER_WEEK);

        // Max periods should be reasonable (typically 4-8)
        assertTrue(SchoolConfig.MAX_PERIODS_PER_DAY >= 4);
        assertTrue(SchoolConfig.MAX_PERIODS_PER_DAY <= 8);

        // At least one classroom per group
        assertTrue(SchoolConfig.NUM_CLASSROOMS >= SchoolConfig.NUM_GROUPS);
    }

    @Test
    @DisplayName("Genetic algorithm parameters should be reasonable")
    void testReasonableGAParameters() {
        // Population should be large enough for diversity
        assertTrue(SchoolConfig.GA_POPULATION_SIZE >= 20);

        // Enough generations for evolution
        assertTrue(SchoolConfig.GA_MAX_GENERATIONS >= 100);

        // Mutation rate typically between 0.01 and 0.1
        assertTrue(SchoolConfig.GA_MUTATION_RATE >= 0.01);
        assertTrue(SchoolConfig.GA_MUTATION_RATE <= 0.1);

        // Tournament size should be smaller than population
        assertTrue(SchoolConfig.GA_TOURNAMENT_SIZE < SchoolConfig.GA_POPULATION_SIZE / 2);
    }
}
