package com.solvd.schoolschedule.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.solvd.schoolschedule.model.Timetable;

/**
 * Unit tests for TimetableGeneratorServiceImpl.
 * Tests the core logic without database operations.
 */
@DisplayName("TimetableGeneratorServiceImpl Tests")
class TimetableGeneratorServiceImplTest {

    private TimetableGeneratorServiceImpl generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new TimetableGeneratorServiceImpl();
    }

    @Test
    @DisplayName("Should find timetable with best fitness from population")
    void testFindBestTimetable() throws Exception {
        // Given
        List<Timetable> population = new ArrayList<>();

        Timetable low = new Timetable();
        low.setFitness(0.3);
        population.add(low);

        Timetable medium = new Timetable();
        medium.setFitness(0.6);
        population.add(medium);

        Timetable high = new Timetable();
        high.setFitness(0.9);
        population.add(high);

        Timetable another = new Timetable();
        another.setFitness(0.5);
        population.add(another);

        // When - use reflection to access private method
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);
        Timetable best = (Timetable) findBestMethod.invoke(generatorService, population);

        // Then
        assertNotNull(best);
        assertEquals(0.9, best.getFitness(), 0.001);
        assertSame(high, best);
    }

    @Test
    @DisplayName("Should find best when all have same fitness")
    void testFindBestWithEqualFitness() throws Exception {
        // Given
        List<Timetable> population = new ArrayList<>();

        Timetable t1 = new Timetable();
        t1.setFitness(0.5);
        population.add(t1);

        Timetable t2 = new Timetable();
        t2.setFitness(0.5);
        population.add(t2);

        Timetable t3 = new Timetable();
        t3.setFitness(0.5);
        population.add(t3);

        // When
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);
        Timetable best = (Timetable) findBestMethod.invoke(generatorService, population);

        // Then
        assertNotNull(best);
        assertEquals(0.5, best.getFitness(), 0.001);
    }

    @Test
    @DisplayName("Should find best with single timetable")
    void testFindBestWithSingleTimetable() throws Exception {
        // Given
        List<Timetable> population = new ArrayList<>();
        Timetable only = new Timetable();
        only.setFitness(0.7);
        population.add(only);

        // When
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);
        Timetable best = (Timetable) findBestMethod.invoke(generatorService, population);

        // Then
        assertNotNull(best);
        assertSame(only, best);
        assertEquals(0.7, best.getFitness(), 0.001);
    }

    @Test
    @DisplayName("Should throw exception when population is empty")
    void testFindBestWithEmptyPopulation() throws Exception {
        // Given
        List<Timetable> emptyPopulation = new ArrayList<>();

        // When/Then
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            findBestMethod.invoke(generatorService, emptyPopulation);
        });

        // Verify it's the wrapped IllegalStateException
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertTrue(exception.getCause().getMessage().contains("Population is empty"));
    }

    @Test
    @DisplayName("Should handle negative fitness values")
    void testFindBestWithNegativeFitness() throws Exception {
        // Given
        List<Timetable> population = new ArrayList<>();

        Timetable negative = new Timetable();
        negative.setFitness(-0.5);
        population.add(negative);

        Timetable lessNegative = new Timetable();
        lessNegative.setFitness(-0.1);
        population.add(lessNegative);

        Timetable mostNegative = new Timetable();
        mostNegative.setFitness(-0.9);
        population.add(mostNegative);

        // When
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);
        Timetable best = (Timetable) findBestMethod.invoke(generatorService, population);

        // Then - should pick the least negative (highest value)
        assertNotNull(best);
        assertEquals(-0.1, best.getFitness(), 0.001);
        assertSame(lessNegative, best);
    }

    @Test
    @DisplayName("Should handle zero fitness values")
    void testFindBestWithZeroFitness() throws Exception {
        // Given
        List<Timetable> population = new ArrayList<>();

        Timetable zero = new Timetable();
        zero.setFitness(0.0);
        population.add(zero);

        Timetable negative = new Timetable();
        negative.setFitness(-0.1);
        population.add(negative);

        // When
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);
        Timetable best = (Timetable) findBestMethod.invoke(generatorService, population);

        // Then
        assertNotNull(best);
        assertEquals(0.0, best.getFitness(), 0.001);
        assertSame(zero, best);
    }

    @Test
    @DisplayName("Should find best with very close fitness values")
    void testFindBestWithCloseValues() throws Exception {
        // Given
        List<Timetable> population = new ArrayList<>();

        Timetable t1 = new Timetable();
        t1.setFitness(0.8001);
        population.add(t1);

        Timetable t2 = new Timetable();
        t2.setFitness(0.8003);
        population.add(t2);

        Timetable t3 = new Timetable();
        t3.setFitness(0.8002);
        population.add(t3);

        // When
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);
        Timetable best = (Timetable) findBestMethod.invoke(generatorService, population);

        // Then
        assertNotNull(best);
        assertEquals(0.8003, best.getFitness(), 0.0001);
        assertSame(t2, best);
    }

    @Test
    @DisplayName("Should find best with large population")
    void testFindBestWithLargePopulation() throws Exception {
        // Given
        List<Timetable> population = new ArrayList<>();
        Timetable expected = null;

        for (int i = 0; i < 100; i++) {
            Timetable t = new Timetable();
            t.setFitness(Math.random());
            population.add(t);

            // Create one with max fitness
            if (i == 50) {
                expected = new Timetable();
                expected.setFitness(1.0);
                population.add(expected);
            }
        }

        // When
        Method findBestMethod = TimetableGeneratorServiceImpl.class
            .getDeclaredMethod("findBestTimetable", List.class);
        findBestMethod.setAccessible(true);
        Timetable best = (Timetable) findBestMethod.invoke(generatorService, population);

        // Then
        assertNotNull(best);
        assertEquals(1.0, best.getFitness(), 0.001);
        assertSame(expected, best);
    }

    @Test
    @DisplayName("Generator service should be created successfully")
    void testServiceCreation() {
        // Then
        assertNotNull(generatorService);
    }

    @Test
    @DisplayName("Service should implement ITimetableGeneratorService interface")
    void testServiceImplementsInterface() {
        // Then
        assertTrue(generatorService instanceof com.solvd.schoolschedule.service.interfaces.ITimetableGeneratorService);
    }
}
