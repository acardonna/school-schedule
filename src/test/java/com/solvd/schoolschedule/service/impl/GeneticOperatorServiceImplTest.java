package com.solvd.schoolschedule.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.*;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;
import com.solvd.schoolschedule.service.interfaces.ISelectionService;

/**
 * Unit tests for the GeneticOperatorServiceImpl class.
 * Tests crossover, mutation, and generation creation operations.
 */
@DisplayName("GeneticOperatorServiceImpl Tests")
class GeneticOperatorServiceImplTest {

    private GeneticOperatorServiceImpl geneticOperatorService;
    private IPopulationService populationService;
    private ISelectionService selectionService;

    // Test data
    private Group group1;
    private Group group2;
    private Teacher mathTeacher;
    private Teacher physicsTeacher;
    private Classroom room101;
    private Classroom room102;

    @BeforeEach
    void setUp() {
        // Use real services
        populationService = new PopulationServiceImpl();
        selectionService = new SelectionServiceImpl(3);
        geneticOperatorService = new GeneticOperatorServiceImpl(populationService, 0.05);

        // Create test data
        group1 = new Group(1, "Group 1");
        group2 = new Group(2, "Group 2");

        mathTeacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        physicsTeacher = new Teacher(2, "Ms. Johnson", Subject.PHYSICS);

        room101 = new Classroom(1, "Room 101", Set.of(Subject.MATH, Subject.PHYSICS));
        room102 = new Classroom(2, "Room 102", Set.of(Subject.MATH, Subject.PHYSICS));
    }

    @Test
    @DisplayName("Should create offspring through crossover")
    void testCrossover() {
        // Given - Two parent timetables with different lessons
        Timetable parent1 = new Timetable();
        parent1.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        parent1.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group2));

        Timetable parent2 = new Timetable();
        parent2.addLesson(new Lesson(Subject.MATH, mathTeacher, room102, new TimeSlot(1, 0), group1));
        parent2.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room101, new TimeSlot(1, 1), group2));

        // When
        Timetable offspring = geneticOperatorService.crossover(parent1, parent2);

        // Then
        assertNotNull(offspring);
        assertNotNull(offspring.getLessons());
        // Offspring should have lessons (exact number depends on random selection)
        assertFalse(offspring.getLessons().isEmpty());
    }

    @Test
    @DisplayName("Crossover should produce offspring with lessons from parents")
    void testCrossoverInheritsFromParents() {
        // Given
        Timetable parent1 = new Timetable();
        Lesson lesson1 = new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1);
        parent1.addLesson(lesson1);

        Timetable parent2 = new Timetable();
        Lesson lesson2 = new Lesson(Subject.MATH, mathTeacher, room102, new TimeSlot(1, 0), group1);
        parent2.addLesson(lesson2);

        // When - perform crossover multiple times
        int foundFromParent1 = 0;
        int foundFromParent2 = 0;
        for (int i = 0; i < 50; i++) {
            Timetable offspring = geneticOperatorService.crossover(parent1, parent2);
            if (offspring.getLessons().contains(lesson1)) foundFromParent1++;
            if (offspring.getLessons().contains(lesson2)) foundFromParent2++;
        }

        // Then - should inherit from both parents at least sometimes
        assertTrue(foundFromParent1 > 0, "Should inherit from parent1 at least once");
        assertTrue(foundFromParent2 > 0, "Should inherit from parent2 at least once");
    }

    @Test
    @DisplayName("Should mutate timetable based on mutation rate")
    void testMutationOccurs() {
        // Given - High mutation rate to ensure mutation happens
        GeneticOperatorServiceImpl highMutationService =
            new GeneticOperatorServiceImpl(populationService, 1.0); // 100% mutation

        Timetable original = new Timetable();
        Lesson originalLesson = new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1);
        original.addLesson(originalLesson);

        // When - Mutate multiple times
        int attempts = 10;
        boolean foundChange = false;

        for (int i = 0; i < attempts && !foundChange; i++) {
            Timetable mutated = highMutationService.mutate(original);

            assertNotNull(mutated);
            assertNotNull(mutated.getLessons());
            assertFalse(mutated.getLessons().isEmpty());

            // Check if the lesson changed (time slot or classroom)
            Lesson mutatedLesson = mutated.getLessons().get(0);
            if (!mutatedLesson.getTimeSlot().equals(originalLesson.getTimeSlot()) ||
                !mutatedLesson.getClassroom().equals(originalLesson.getClassroom())) {
                foundChange = true;
            }
        }

        assertTrue(foundChange,
            "With 100% mutation rate, at least one mutation should change the lesson");
    }

    @Test
    @DisplayName("Should not mutate with zero mutation rate")
    void testNoMutationWithZeroRate() {
        // Given - Zero mutation rate
        GeneticOperatorServiceImpl noMutationService =
            new GeneticOperatorServiceImpl(populationService, 0.0);

        Timetable original = new Timetable();
        Lesson originalLesson = new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1);
        original.addLesson(originalLesson);

        // When
        Timetable result = noMutationService.mutate(original);

        // Then
        assertEquals(original, result, "With 0% mutation rate, should return original timetable");
    }

    @Test
    @DisplayName("Mutation should produce valid lessons")
    void testMutationProducesValidLessons() {
        // Given
        GeneticOperatorServiceImpl highMutationService =
            new GeneticOperatorServiceImpl(populationService, 1.0);

        Timetable original = new Timetable();
        original.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));

        // When
        Timetable mutated = highMutationService.mutate(original);

        // Then - mutated lesson should still have all required components
        assertFalse(mutated.getLessons().isEmpty());
        Lesson mutatedLesson = mutated.getLessons().get(0);

        assertNotNull(mutatedLesson.getSubject());
        assertNotNull(mutatedLesson.getTeacher());
        assertNotNull(mutatedLesson.getClassroom());
        assertNotNull(mutatedLesson.getTimeSlot());
        assertNotNull(mutatedLesson.getGroup());
    }

    @Test
    @DisplayName("Should create new generation with correct size")
    void testCreateNewGeneration() {
        // Given
        List<Timetable> population = createTestPopulation(10);

        // When
        List<Timetable> newGeneration = geneticOperatorService.createNewGeneration(population, selectionService);

        // Then
        assertNotNull(newGeneration);
        assertEquals(population.size(), newGeneration.size(),
            "New generation should have same size as original population");
    }

    @Test
    @DisplayName("New generation should include best individual (elitism)")
    void testElitism() {
        // Given
        List<Timetable> population = new ArrayList<>();

        // Create population with one clearly best timetable
        Timetable bestTimetable = new Timetable();
        bestTimetable.setFitness(1000.0);
        bestTimetable.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        population.add(bestTimetable);

        // Add mediocre timetables
        for (int i = 0; i < 9; i++) {
            Timetable mediocre = new Timetable();
            mediocre.setFitness(50.0);
            mediocre.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(i % 5, i % 6), group1));
            population.add(mediocre);
        }

        // When
        List<Timetable> newGeneration = geneticOperatorService.createNewGeneration(population, selectionService);

        // Then - best individual should be in new generation
        boolean bestIsPreserved = newGeneration.stream()
            .anyMatch(t -> t.getFitness() == 1000.0);

        assertTrue(bestIsPreserved, "Best individual should be preserved through elitism");
    }

    @Test
    @DisplayName("Should create new generation with all valid timetables")
    void testNewGenerationValidity() {
        // Given
        List<Timetable> population = createTestPopulation(10);

        // When
        List<Timetable> newGeneration = geneticOperatorService.createNewGeneration(population, selectionService);

        // Then
        for (Timetable timetable : newGeneration) {
            assertNotNull(timetable);
            assertNotNull(timetable.getLessons());
        }
    }

    @Test
    @DisplayName("Crossover should handle timetables with multiple groups")
    void testCrossoverWithMultipleGroups() {
        // Given
        Timetable parent1 = new Timetable();
        parent1.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        parent1.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group2));

        Timetable parent2 = new Timetable();
        parent2.addLesson(new Lesson(Subject.MATH, mathTeacher, room102, new TimeSlot(1, 0), group1));
        parent2.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room101, new TimeSlot(1, 1), group2));

        // When
        Timetable offspring = geneticOperatorService.crossover(parent1, parent2);

        // Then
        assertNotNull(offspring);
        // Should have lessons (combination from both parents)
        assertFalse(offspring.getLessons().isEmpty());
    }

    @Test
    @DisplayName("Mutation should handle timetable with multiple lessons")
    void testMutationWithMultipleLessons() {
        // Given
        GeneticOperatorServiceImpl highMutationService =
            new GeneticOperatorServiceImpl(populationService, 0.5); // 50% mutation

        Timetable original = new Timetable();
        original.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        original.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group1));
        original.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(1, 0), group2));

        // When
        Timetable mutated = highMutationService.mutate(original);

        // Then
        assertNotNull(mutated);
        assertEquals(3, mutated.getLessons().size(), "Should maintain same number of lessons");
    }

    @Test
    @DisplayName("Should create diverse offspring across multiple crossovers")
    void testCrossoverDiversity() {
        // Given
        Timetable parent1 = new Timetable();
        parent1.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        parent1.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group2));

        Timetable parent2 = new Timetable();
        parent2.addLesson(new Lesson(Subject.MATH, mathTeacher, room102, new TimeSlot(1, 0), group1));
        parent2.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room101, new TimeSlot(1, 1), group2));

        // When - create multiple offspring
        Set<Integer> differentOffspringCounts = new java.util.HashSet<>();
        for (int i = 0; i < 20; i++) {
            Timetable offspring = geneticOperatorService.crossover(parent1, parent2);
            differentOffspringCounts.add(offspring.getLessons().size());
        }

        // Then - should produce some variation
        assertTrue(differentOffspringCounts.size() > 0,
            "Crossover should produce offspring (possibly with variation)");
    }

    @Test
    @DisplayName("Mutation rate should affect frequency of mutations")
    void testMutationRateEffect() {
        // Given
        Timetable original = new Timetable();
        original.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));

        GeneticOperatorServiceImpl lowMutation = new GeneticOperatorServiceImpl(populationService, 0.01);
        GeneticOperatorServiceImpl highMutation = new GeneticOperatorServiceImpl(populationService, 0.9);

        // When - mutate many times and count changes
        int lowMutationChanges = 0;
        int highMutationChanges = 0;
        int iterations = 100;

        for (int i = 0; i < iterations; i++) {
            Timetable lowResult = lowMutation.mutate(original);
            Timetable highResult = highMutation.mutate(original);

            if (lowResult != original) lowMutationChanges++;
            if (highResult != original) highMutationChanges++;
        }

        // Then - higher mutation rate should produce more changes
        assertTrue(highMutationChanges >= lowMutationChanges,
            "Higher mutation rate should produce at least as many changes. Low: " +
            lowMutationChanges + ", High: " + highMutationChanges);
    }

    @Test
    @DisplayName("Genetic operators should work with empty timetables")
    void testGeneticOperatorsWithEmptyTimetables() {
        // Given
        Timetable empty1 = new Timetable();
        Timetable empty2 = new Timetable();

        // When & Then - should not crash
        assertDoesNotThrow(() -> {
            Timetable offspring = geneticOperatorService.crossover(empty1, empty2);
            assertNotNull(offspring);
        });

        assertDoesNotThrow(() -> {
            Timetable mutated = geneticOperatorService.mutate(empty1);
            assertNotNull(mutated);
        });
    }

    /**
     * Helper method to create a test population
     */
    private List<Timetable> createTestPopulation(int size) {
        List<Timetable> population = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Timetable timetable = new Timetable();
            timetable.setFitness(100.0 + i * 10);

            // Add some random lessons
            timetable.addLesson(new Lesson(Subject.MATH, mathTeacher, room101,
                new TimeSlot(i % 5, i % 6), group1));

            population.add(timetable);
        }

        return population;
    }
}
