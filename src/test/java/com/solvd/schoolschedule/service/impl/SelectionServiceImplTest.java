package com.solvd.schoolschedule.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.solvd.schoolschedule.model.Timetable;

/**
 * Unit tests for the SelectionServiceImpl class.
 * Tests tournament selection algorithm for parent selection in genetic algorithm.
 */
@DisplayName("SelectionServiceImpl Tests")
class SelectionServiceImplTest {

    private SelectionServiceImpl selectionService;
    private List<Timetable> population;

    @BeforeEach
    void setUp() {
        // Create selection service with tournament size of 3
        selectionService = new SelectionServiceImpl(3);

        // Create a population of timetables with different fitness values
        population = createTestPopulation();
    }

    /**
     * Helper method to create a test population with varying fitness
     */
    private List<Timetable> createTestPopulation() {
        List<Timetable> pop = new ArrayList<>();

        // Create 10 timetables with fitness values from 10 to 100
        for (int i = 1; i <= 10; i++) {
            Timetable timetable = new Timetable();
            timetable.setFitness(i * 10.0); // Fitness: 10, 20, 30, ..., 100
            pop.add(timetable);
        }

        return pop;
    }

    @Test
    @DisplayName("Should select a parent from population")
    void testSelectParent() {
        // When
        Timetable selected = selectionService.selectParent(population);

        // Then
        assertNotNull(selected);
        assertTrue(population.contains(selected));
    }

    @Test
    @DisplayName("Should select parent with higher fitness more often")
    void testSelectionBiasTowardsFitness() {
        // Given - create population with one very fit individual
        List<Timetable> skewedPopulation = new ArrayList<>();
        Timetable bestTimetable = new Timetable();
        bestTimetable.setFitness(1000.0);
        skewedPopulation.add(bestTimetable);

        // Add many poor individuals
        for (int i = 0; i < 20; i++) {
            Timetable poor = new Timetable();
            poor.setFitness(1.0);
            skewedPopulation.add(poor);
        }

        // When - run selection many times
        int bestSelectedCount = 0;
        int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            Timetable selected = selectionService.selectParent(skewedPopulation);
            if (selected == bestTimetable) {
                bestSelectedCount++;
            }
        }

        // Then - best individual should be selected more often than random (>5%)
        double selectionRate = (double) bestSelectedCount / iterations;
        assertTrue(selectionRate > 0.05,
            "Best individual should be selected more than random chance (5%). Actual: " + selectionRate);
    }

    @Test
    @DisplayName("Should select two parents")
    void testSelectParents() {
        // When
        Timetable[] parents = selectionService.selectParents(population);

        // Then
        assertNotNull(parents);
        assertEquals(2, parents.length);
        assertNotNull(parents[0]);
        assertNotNull(parents[1]);
        assertTrue(population.contains(parents[0]));
        assertTrue(population.contains(parents[1]));
    }

    @Test
    @DisplayName("Should select different parents when population size > 1")
    void testSelectDifferentParents() {
        // Given - small population to increase chance of getting same parent
        List<Timetable> smallPopulation = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Timetable timetable = new Timetable();
            timetable.setFitness(i * 10.0);
            smallPopulation.add(timetable);
        }

        // When - run multiple times to check if different parents are selected
        int differentParentsCount = 0;
        int iterations = 50;
        for (int i = 0; i < iterations; i++) {
            Timetable[] parents = selectionService.selectParents(smallPopulation);
            if (parents[0] != parents[1]) {
                differentParentsCount++;
            }
        }

        // Then - most of the time, parents should be different
        assertTrue(differentParentsCount > iterations * 0.8,
            "Parents should be different most of the time. Different in " +
            differentParentsCount + "/" + iterations + " iterations");
    }

    @Test
    @DisplayName("Should handle population with single timetable")
    void testSelectParentsFromSingletonPopulation() {
        // Given
        List<Timetable> singletonPopulation = new ArrayList<>();
        Timetable onlyTimetable = new Timetable();
        onlyTimetable.setFitness(50.0);
        singletonPopulation.add(onlyTimetable);

        // When
        Timetable[] parents = selectionService.selectParents(singletonPopulation);

        // Then
        assertNotNull(parents);
        assertEquals(2, parents.length);
        assertEquals(onlyTimetable, parents[0]);
        assertEquals(onlyTimetable, parents[1]);
    }

    @Test
    @DisplayName("Should select multiple parents")
    void testSelectMultipleParents() {
        // Given
        int numberOfParents = 5;

        // When
        List<Timetable> selectedParents = selectionService.selectParents(population, numberOfParents);

        // Then
        assertNotNull(selectedParents);
        assertEquals(numberOfParents, selectedParents.size());

        // All selected parents should be from the population
        for (Timetable parent : selectedParents) {
            assertTrue(population.contains(parent));
        }
    }

    @Test
    @DisplayName("Should select correct number of parents")
    void testSelectVariableNumberOfParents() {
        // When & Then
        assertEquals(1, selectionService.selectParents(population, 1).size());
        assertEquals(3, selectionService.selectParents(population, 3).size());
        assertEquals(10, selectionService.selectParents(population, 10).size());
    }

    @Test
    @DisplayName("Tournament selection should favor higher fitness")
    void testTournamentSelectionFavorsFitness() {
        // Given - create population with clear fitness hierarchy
        List<Timetable> testPopulation = new ArrayList<>();
        Timetable low = new Timetable();
        low.setFitness(10.0);
        Timetable medium = new Timetable();
        medium.setFitness(50.0);
        Timetable high = new Timetable();
        high.setFitness(100.0);

        testPopulation.add(low);
        testPopulation.add(medium);
        testPopulation.add(high);

        // When - select many times
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;
        int iterations = 300;

        for (int i = 0; i < iterations; i++) {
            Timetable selected = selectionService.selectParent(testPopulation);
            if (selected == high) highCount++;
            else if (selected == medium) mediumCount++;
            else if (selected == low) lowCount++;
        }

        // Then - higher fitness should be selected more often
        assertTrue(highCount > mediumCount,
            "High fitness (" + highCount + ") should be selected more than medium (" + mediumCount + ")");
        assertTrue(mediumCount > lowCount,
            "Medium fitness (" + mediumCount + ") should be selected more than low (" + lowCount + ")");
    }

    @Test
    @DisplayName("Should work with different tournament sizes")
    void testDifferentTournamentSizes() {
        // Given
        SelectionServiceImpl smallTournament = new SelectionServiceImpl(2);
        SelectionServiceImpl largeTournament = new SelectionServiceImpl(5);

        // When
        Timetable selectedSmall = smallTournament.selectParent(population);
        Timetable selectedLarge = largeTournament.selectParent(population);

        // Then
        assertNotNull(selectedSmall);
        assertNotNull(selectedLarge);
        assertTrue(population.contains(selectedSmall));
        assertTrue(population.contains(selectedLarge));
    }

    @Test
    @DisplayName("Larger tournament should select fitter individuals more often")
    void testTournamentSizeEffect() {
        // Given
        SelectionServiceImpl smallTournament = new SelectionServiceImpl(2);
        SelectionServiceImpl largeTournament = new SelectionServiceImpl(5);

        // Get the best timetable (fitness 100)
        Timetable bestTimetable = population.stream()
            .max((t1, t2) -> Double.compare(t1.getFitness(), t2.getFitness()))
            .orElseThrow();

        // When - run selections multiple times
        int smallTournamentBestCount = 0;
        int largeTournamentBestCount = 0;
        int iterations = 200;

        for (int i = 0; i < iterations; i++) {
            if (smallTournament.selectParent(population) == bestTimetable) {
                smallTournamentBestCount++;
            }
            if (largeTournament.selectParent(population) == bestTimetable) {
                largeTournamentBestCount++;
            }
        }

        // Then - larger tournament should select best individual more often
        assertTrue(largeTournamentBestCount >= smallTournamentBestCount,
            "Larger tournament (" + largeTournamentBestCount + ") should select best at least as often as small tournament (" + smallTournamentBestCount + ")");
    }

    @Test
    @DisplayName("Should never return null parent")
    void testNeverReturnsNull() {
        // When - select many times
        for (int i = 0; i < 100; i++) {
            Timetable parent = selectionService.selectParent(population);

            // Then
            assertNotNull(parent, "Selection should never return null");
        }
    }

    @Test
    @DisplayName("Selected parents should have valid fitness values")
    void testSelectedParentsHaveValidFitness() {
        // When
        Timetable[] parents = selectionService.selectParents(population);

        // Then
        assertTrue(parents[0].getFitness() >= 0, "Parent 1 fitness should be valid");
        assertTrue(parents[1].getFitness() >= 0, "Parent 2 fitness should be valid");
    }

    @Test
    @DisplayName("Selection should work with population of all equal fitness")
    void testSelectionWithEqualFitness() {
        // Given
        List<Timetable> equalPopulation = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Timetable timetable = new Timetable();
            timetable.setFitness(50.0); // All have same fitness
            equalPopulation.add(timetable);
        }

        // When
        Timetable selected = selectionService.selectParent(equalPopulation);

        // Then
        assertNotNull(selected);
        assertEquals(50.0, selected.getFitness());
        assertTrue(equalPopulation.contains(selected));
    }
}
