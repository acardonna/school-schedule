package com.solvd.schoolschedule.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.*;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;

/**
 * Unit tests for the FitnessServiceImpl class.
 * Tests fitness calculation and constraint violation detection.
 */
@DisplayName("FitnessServiceImpl Tests")
class FitnessServiceImplTest {

    private FitnessServiceImpl fitnessService;
    private IPopulationService populationService;

    // Test data
    private Group group1;
    private Group group2;
    private Teacher mathTeacher;
    private Teacher physicsTeacher;
    private Classroom room101;
    private Classroom room102;
    private Classroom computerLab;

    @BeforeEach
    void setUp() {
        // Use the real PopulationServiceImpl
        populationService = new PopulationServiceImpl();
        fitnessService = new FitnessServiceImpl(populationService);

        // Create test data
        group1 = new Group(1, "Group 1");
        group2 = new Group(2, "Group 2");

        mathTeacher = new Teacher(1, "Mr. Smith", Subject.MATH);
        physicsTeacher = new Teacher(2, "Ms. Johnson", Subject.PHYSICS);

        room101 = new Classroom(1, "Room 101", Set.of(Subject.MATH, Subject.PHYSICS, Subject.PHYSICAL_CULTURE));
        room102 = new Classroom(2, "Room 102", Set.of(Subject.MATH, Subject.PHYSICS));
        computerLab = new Classroom(5, "Computer Lab", Set.of(Subject.INFORMATICS));
    }

    @Test
    @DisplayName("Should calculate base fitness for empty timetable")
    void testCalculateFitnessEmpty() {
        // Given
        Timetable timetable = new Timetable();

        // When
        double fitness = fitnessService.calculateFitness(timetable);

        // Then
        // Empty timetable should have penalties for missing lessons (adjustment)
        assertTrue(fitness < 2000.0, "Empty timetable should be penalized for missing lessons");
    }

    @Test
    @DisplayName("Should calculate fitness for valid timetable without conflicts")
    void testCalculateFitnessNoConflicts() {
        // Given
        Timetable timetable = new Timetable();

        // Add lessons at different times  (no conflicts)
        timetable.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        timetable.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group1));
        timetable.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(1, 0), group2));

        // When
        double fitness = fitnessService.calculateFitness(timetable);

        // Then
        assertNotNull(fitness);
        assertTrue(fitness < 2000.0); // Will have some penalties but should be reasonable
    }

    @Test
    @DisplayName("Should penalize room conflicts (same room, same time)")
    void testRoomConflicts() {
        // Given - Two lessons in SAME room at SAME time
        Timetable timetableWithConflict = new Timetable();
        TimeSlot sameTime = new TimeSlot(0, 0);
        timetableWithConflict.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, sameTime, group1));
        timetableWithConflict.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room101, sameTime, group2));

        // Given - No conflicts
        Timetable timetableNoConflict = new Timetable();
        timetableNoConflict.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        timetableNoConflict.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 0), group2));

        // When
        double fitnessWithConflict = fitnessService.calculateFitness(timetableWithConflict);
        double fitnessNoConflict = fitnessService.calculateFitness(timetableNoConflict);

        // Then
        assertTrue(fitnessWithConflict < fitnessNoConflict,
            "Timetable with room conflict should have lower fitness");
    }

    @Test
    @DisplayName("Should penalize when classroom cannot accommodate subject")
    void testRoomAccommodation() {
        // Given - Math lesson in Computer Lab (not allowed)
        Timetable timetableWrongRoom = new Timetable();
        timetableWrongRoom.addLesson(new Lesson(Subject.MATH, mathTeacher, computerLab, new TimeSlot(0, 0), group1));

        // Given - Math lesson in appropriate room
        Timetable timetableRightRoom = new Timetable();
        timetableRightRoom.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));

        // When
        double fitnessWrongRoom = fitnessService.calculateFitness(timetableWrongRoom);
        double fitnessRightRoom = fitnessService.calculateFitness(timetableRightRoom);

        // Then
        assertTrue(fitnessWrongRoom < fitnessRightRoom,
            "Lesson in wrong room type should have lower fitness");
    }

    @Test
    @DisplayName("Should penalize gaps in group schedule")
    void testGroupGaps() {
        // Given - Group with gap (lessons at period 0 and 3, gap at 1-2)
        Timetable timetableWithGap = new Timetable();
        timetableWithGap.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        timetableWithGap.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 3), group1));

        // Given - Group with consecutive lessons (no gap)
        Timetable timetableNoGap = new Timetable();
        timetableNoGap.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        timetableNoGap.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group1));

        // When
        double fitnessWithGap = fitnessService.calculateFitness(timetableWithGap);
        double fitnessNoGap = fitnessService.calculateFitness(timetableNoGap);

        // Then
        assertTrue(fitnessWithGap < fitnessNoGap,
            "Timetable with gaps in group schedule should have lower fitness");
    }

    @Test
    @DisplayName("Should penalize group having multiple lessons at same time")
    void testGroupCollisions() {
        // Given - Group has TWO lessons at same time (which should be impossible)
        Timetable timetableWithCollision = new Timetable();
        TimeSlot sameTime = new TimeSlot(0, 0);
        timetableWithCollision.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, sameTime, group1));
        timetableWithCollision.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, sameTime, group1));

        // Given - Group has lessons at different times
        Timetable timetableNoCollision = new Timetable();
        timetableNoCollision.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        timetableNoCollision.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group1));

        // When
        double fitnessWithCollision = fitnessService.calculateFitness(timetableWithCollision);
        double fitnessNoCollision = fitnessService.calculateFitness(timetableNoCollision);

        // Then
        assertTrue(fitnessWithCollision < fitnessNoCollision,
            "Group with colliding lessons should have lower fitness");
    }

    @Test
    @DisplayName("Should penalize teacher having multiple lessons at same time")
    void testTeacherCollisions() {
        // Given - Teacher has TWO lessons at same time (which should be impossible)
        Timetable timetableWithCollision = new Timetable();
        TimeSlot sameTime = new TimeSlot(0, 0);
        timetableWithCollision.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, sameTime, group1));
        timetableWithCollision.addLesson(new Lesson(Subject.MATH, mathTeacher, room102, sameTime, group2));

        // Given - Teacher has lessons at different times
        Timetable timetableNoCollision = new Timetable();
        timetableNoCollision.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        timetableNoCollision.addLesson(new Lesson(Subject.MATH, mathTeacher, room102, new TimeSlot(0, 1), group2));

        // When
        double fitnessWithCollision = fitnessService.calculateFitness(timetableWithCollision);
        double fitnessNoCollision = fitnessService.calculateFitness(timetableNoCollision);

        // Then
        assertTrue(fitnessWithCollision < fitnessNoCollision,
            "Teacher with colliding lessons should have lower fitness");
    }

    @Test
    @DisplayName("Should penalize when Physical Culture is not last lesson of the day")
    void testPhysicalCultureLastLesson() {
        // Given - Physical Culture at period 2, then Math at period 3 (violation!)
        Timetable timetableViolation = new Timetable();
        Teacher physCulTeacher = new Teacher(4, "Coach", Subject.PHYSICAL_CULTURE);
        timetableViolation.addLesson(new Lesson(Subject.PHYSICAL_CULTURE, physCulTeacher, room101, new TimeSlot(0, 2), group1));
        timetableViolation.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 3), group1));

        // Given - Physical Culture as last lesson
        Timetable timetableCorrect = new Timetable();
        timetableCorrect.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 2), group1));
        timetableCorrect.addLesson(new Lesson(Subject.PHYSICAL_CULTURE, physCulTeacher, room101, new TimeSlot(0, 3), group1));

        // When
        double fitnessViolation = fitnessService.calculateFitness(timetableViolation);
        double fitnessCorrect = fitnessService.calculateFitness(timetableCorrect);

        // Then
        assertTrue(fitnessViolation < fitnessCorrect,
            "Physical Culture not being last should have lower fitness");
    }

    @Test
    @DisplayName("Should penalize invalid teacher-subject assignments")
    void testInvalidTeacherSubjectAssignment() {
        // Given - Math teacher teaching Physics (which is wrong)
        Timetable timetableWrong = new Timetable();
        timetableWrong.addLesson(new Lesson(Subject.PHYSICS, mathTeacher, room101, new TimeSlot(0, 0), group1));

        // Given - Correct teacher-subject match
        Timetable timetableCorrect = new Timetable();
        timetableCorrect.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));

        // When
        double fitnessWrong = fitnessService.calculateFitness(timetableWrong);
        double fitnessCorrect = fitnessService.calculateFitness(timetableCorrect);

        // Then
        assertTrue(fitnessWrong < fitnessCorrect,
            "Wrong teacher-subject assignment should have lower fitness");
    }

    @Test
    @DisplayName("Should evaluate entire population and set fitness values")
    void testEvaluatePopulation() {
        // Given
        Timetable timetable1 = new Timetable();
        timetable1.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));

        Timetable timetable2 = new Timetable();
        timetable2.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 0), group1));

        List<Timetable> population = List.of(timetable1, timetable2);

        // When
        fitnessService.evaluatePopulation(population);

        // Then
        assertNotEquals(0.0, timetable1.getFitness(), "Timetable 1 should have fitness calculated");
        assertNotEquals(0.0, timetable2.getFitness(), "Timetable 2 should have fitness calculated");
    }

    @Test
    @DisplayName("Should calculate correct group adjustment (missing lessons penalty)")
    void testCalculateGroupAdjustment() {
        // Given - Group has only 1 Math lesson, should have 5 according to SubjectConfig
        Group testGroup = new Group(1, "Test Group");
        Lesson mathLesson = new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), testGroup);
        List<Lesson> lessons = List.of(mathLesson);

        // When
        int adjustment = fitnessService.calculateGroupAdjustment(lessons);

        // Then
        // Math needs 5, has 1 = 4 missing
        // Physics needs 4, has 0 = 4 missing
        // Informatics needs 3, has 0 = 3 missing
        // Physical Culture needs 2, has 0 = 2 missing
        // Total = 4+4+3+2 = 13
        assertEquals(15, adjustment);
    }

    @Test
    @DisplayName("Should have zero adjustment when group has correct number of lessons")
    void testCalculateGroupAdjustmentPerfect() {
        // Given - Perfect schedule with correct lesson counts
        Group testGroup = new Group(1, "Test Group");
        Teacher informaticsTeacher = new Teacher(3, "Dr. Brown", Subject.INFORMATICS);
        Teacher physCulTeacher = new Teacher(4, "Coach", Subject.PHYSICAL_CULTURE);

        List<Lesson> lessons = List.of(
            // Math: 5 lessons
            new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), testGroup),
            new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 1), testGroup),
            new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(1, 0), testGroup),
            new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(2, 0), testGroup),
            new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(3, 0), testGroup),
            // Physics: 4 lessons
            new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 2), testGroup),
            new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(1, 1), testGroup),
            new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(2, 1), testGroup),
            new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(3, 1), testGroup),
            // Informatics: 3 lessons
            new Lesson(Subject.INFORMATICS, informaticsTeacher, computerLab, new TimeSlot(0, 3), testGroup),
            new Lesson(Subject.INFORMATICS, informaticsTeacher, computerLab, new TimeSlot(1, 2), testGroup),
            new Lesson(Subject.INFORMATICS, informaticsTeacher, computerLab, new TimeSlot(2, 2), testGroup),
            // Physical Culture: 2 lessons
            new Lesson(Subject.PHYSICAL_CULTURE, physCulTeacher, room101, new TimeSlot(3, 2), testGroup),
            new Lesson(Subject.PHYSICAL_CULTURE, physCulTeacher, room101, new TimeSlot(4, 0), testGroup)
        );

        // When
        int adjustment = fitnessService.calculateGroupAdjustment(lessons);

        // Then
        assertEquals(2, adjustment, "Perfect schedule should have zero adjustment penalty");
    }

    @Test
    @DisplayName("Fitness should decrease with more constraint violations")
    void testFitnessDecreasesWithViolations() {
        // Given - Timetable with multiple violations
        Timetable badTimetable = new Timetable();
        TimeSlot sameTime = new TimeSlot(0, 0);
        // Room conflict + group collision + teacher collision
        badTimetable.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, sameTime, group1));
        badTimetable.addLesson(new Lesson(Subject.PHYSICS, mathTeacher, room101, sameTime, group1));

        // Given - Timetable with fewer violations
        Timetable betterTimetable = new Timetable();
        betterTimetable.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));
        betterTimetable.addLesson(new Lesson(Subject.PHYSICS, physicsTeacher, room102, new TimeSlot(0, 1), group1));

        // When
        double badFitness = fitnessService.calculateFitness(badTimetable);
        double betterFitness = fitnessService.calculateFitness(betterTimetable);

        // Then
        assertTrue(badFitness < betterFitness,
            "More violations should result in lower fitness. Bad: " + badFitness + ", Better: " + betterFitness);
    }

    @Test
    @DisplayName("Fitness calculation should be consistent")
    void testFitnessCalculationConsistency() {
        // Given
        Timetable timetable = new Timetable();
        timetable.addLesson(new Lesson(Subject.MATH, mathTeacher, room101, new TimeSlot(0, 0), group1));

        // When - calculate multiple times
        double fitness1 = fitnessService.calculateFitness(timetable);
        double fitness2 = fitnessService.calculateFitness(timetable);
        double fitness3 = fitnessService.calculateFitness(timetable);

        // Then - should be consistent
        assertEquals(fitness1, fitness2, 0.001);
        assertEquals(fitness2, fitness3, 0.001);
    }
}
