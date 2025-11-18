package com.solvd.schoolschedule.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.solvd.schoolschedule.model.*;

/**
 * Unit tests for the PopulationServiceImpl class.
 * Tests population initialization, data setup, and timetable generation.
 */
@DisplayName("PopulationServiceImpl Tests")
class PopulationServiceImplTest {

    private PopulationServiceImpl populationService;

    @BeforeEach
    void setUp() {
        populationService = new PopulationServiceImpl();
    }

    @Test
    @DisplayName("Should initialize with teachers")
    void testInitializeTeachers() {
        // When
        List<Teacher> teachers = populationService.getTeachers();

        // Then
        assertNotNull(teachers);
        assertFalse(teachers.isEmpty(), "Should have teachers");
    }

    @Test
    @DisplayName("Should have teachers for all subjects")
    void testTeachersForAllSubjects() {
        // Given
        List<Teacher> teachers = populationService.getTeachers();

        // Then - should have at least one teacher per subject
        for (Subject subject : Subject.values()) {
            boolean hasTeacherForSubject = teachers.stream()
                .anyMatch(t -> t.getSubject() == subject);
            assertTrue(hasTeacherForSubject,
                "Should have at least one teacher for " + subject);
        }
    }

    @Test
    @DisplayName("Should initialize with classrooms")
    void testInitializeClassrooms() {
        // When
        List<Classroom> classrooms = populationService.getClassrooms();

        // Then
        assertNotNull(classrooms);
        assertFalse(classrooms.isEmpty(), "Should have classrooms");
        assertEquals(SchoolConfig.NUM_CLASSROOMS, classrooms.size());
    }

    @Test
    @DisplayName("Should have classrooms that can accommodate different subjects")
    void testClassroomSubjectAccommodation() {
        // Given
        List<Classroom> classrooms = populationService.getClassrooms();

        // Then - should have classrooms for all subjects
        for (Subject subject : Subject.values()) {
            boolean hasClassroomForSubject = classrooms.stream()
                .anyMatch(c -> c.canAccommodate(subject));
            assertTrue(hasClassroomForSubject,
                "Should have at least one classroom that can accommodate " + subject);
        }
    }

    @Test
    @DisplayName("Should initialize with groups")
    void testInitializeGroups() {
        // When
        List<Group> groups = populationService.getGroups();

        // Then
        assertNotNull(groups);
        assertFalse(groups.isEmpty(), "Should have groups");
        assertEquals(SchoolConfig.NUM_GROUPS, groups.size());
    }

    @Test
    @DisplayName("Groups should have sequential IDs")
    void testGroupsHaveSequentialIds() {
        // Given
        List<Group> groups = populationService.getGroups();

        // Then
        for (int i = 0; i < groups.size(); i++) {
            assertEquals(i + 1, groups.get(i).getId(),
                "Group should have ID " + (i + 1));
        }
    }

    @Test
    @DisplayName("Should initialize population with correct size")
    void testInitializePopulationSize() {
        // Given
        int populationSize = 10;

        // When
        List<Timetable> population = populationService.initializePopulation(populationSize);

        // Then
        assertNotNull(population);
        assertEquals(populationSize, population.size());
    }

    @Test
    @DisplayName("Each timetable in population should have lessons")
    void testPopulationTimetablesHaveLessons() {
        // Given
        int populationSize = 5;

        // When
        List<Timetable> population = populationService.initializePopulation(populationSize);

        // Then
        for (Timetable timetable : population) {
            assertNotNull(timetable);
            assertNotNull(timetable.getLessons());
            assertFalse(timetable.getLessons().isEmpty(),
                "Each timetable should have lessons");
        }
    }

    @Test
    @DisplayName("Generated timetables should have correct total number of lessons")
    void testTimetableTotalLessons() {
        // Given
        int populationSize = 3;
        int expectedLessonsPerGroup = SubjectConfig.getTotalWeeklyLessons();
        int expectedTotalLessons = expectedLessonsPerGroup * SchoolConfig.NUM_GROUPS;

        // When
        List<Timetable> population = populationService.initializePopulation(populationSize);

        // Then
        for (Timetable timetable : population) {
            assertEquals(expectedTotalLessons, timetable.getLessons().size(),
                "Each timetable should have " + expectedTotalLessons + " lessons total");
        }
    }

    @Test
    @DisplayName("Each group should have correct number of lessons per subject")
    void testGroupLessonDistribution() {
        // Given
        List<Timetable> population = populationService.initializePopulation(1);
        Timetable timetable = population.get(0);

        // Then - for each group, check lesson counts
        for (Group group : populationService.getGroups()) {
            List<Lesson> groupLessons = timetable.getLessonsFor(group);

            // Total lessons for group should match config
            assertEquals(SubjectConfig.getTotalWeeklyLessons(), groupLessons.size(),
                "Group should have correct total lessons");

            // Check each subject
            for (Subject subject : Subject.values()) {
                long subjectCount = groupLessons.stream()
                    .filter(l -> l.getSubject() == subject)
                    .count();
                assertEquals(SubjectConfig.getWeeklyLessons(subject), subjectCount,
                    "Group should have correct number of " + subject + " lessons");
            }
        }
    }

    @Test
    @DisplayName("Generated lessons should have valid teachers for subjects")
    void testLessonsHaveCorrectTeachers() {
        // Given
        List<Timetable> population = populationService.initializePopulation(1);
        Timetable timetable = population.get(0);

        // Then
        for (Lesson lesson : timetable.getLessons()) {
            assertEquals(lesson.getSubject(), lesson.getTeacher().getSubject(),
                "Teacher should teach the lesson's subject");
        }
    }

    @Test
    @DisplayName("Generated lessons should have suitable classrooms")
    void testLessonsHaveSuitableClassrooms() {
        // Given
        List<Timetable> population = populationService.initializePopulation(1);
        Timetable timetable = population.get(0);

        // Then
        for (Lesson lesson : timetable.getLessons()) {
            assertTrue(lesson.getClassroom().canAccommodate(lesson.getSubject()),
                "Classroom should be able to accommodate the subject: " + lesson.getSubject());
        }
    }

    @Test
    @DisplayName("Generated lessons should have valid time slots")
    void testLessonsHaveValidTimeSlots() {
        // Given
        List<Timetable> population = populationService.initializePopulation(1);
        Timetable timetable = population.get(0);

        // Then
        for (Lesson lesson : timetable.getLessons()) {
            TimeSlot timeSlot = lesson.getTimeSlot();
            assertNotNull(timeSlot);

            assertTrue(timeSlot.getDay() >= 0 && timeSlot.getDay() < SchoolConfig.WORKING_DAYS_PER_WEEK,
                "Day should be between 0 and " + (SchoolConfig.WORKING_DAYS_PER_WEEK - 1));

            assertTrue(timeSlot.getPeriod() >= 0 && timeSlot.getPeriod() < SchoolConfig.MAX_PERIODS_PER_DAY,
                "Period should be between 0 and " + (SchoolConfig.MAX_PERIODS_PER_DAY - 1));
        }
    }

    @Test
    @DisplayName("Should generate different random timetables")
    void testRandomnessInPopulation() {
        // Given
        int populationSize = 10;

        // When
        List<Timetable> population = populationService.initializePopulation(populationSize);

        // Then - check if different timetables have different arrangements
        // We'll compare first lessons' time slots
        boolean foundDifference = false;
        Lesson firstLesson = population.get(0).getLessons().get(0);

        for (int i = 1; i < population.size(); i++) {
            Lesson otherFirstLesson = population.get(i).getLessons().get(0);
            if (!firstLesson.getTimeSlot().equals(otherFirstLesson.getTimeSlot()) ||
                !firstLesson.getClassroom().equals(otherFirstLesson.getClassroom())) {
                foundDifference = true;
                break;
            }
        }

        assertTrue(foundDifference,
            "Population should contain different random timetables");
    }

    @Test
    @DisplayName("Should handle small population size")
    void testSmallPopulationSize() {
        // When
        List<Timetable> population = populationService.initializePopulation(1);

        // Then
        assertEquals(1, population.size());
        assertNotNull(population.get(0));
        assertFalse(population.get(0).getLessons().isEmpty());
    }

    @Test
    @DisplayName("Should handle large population size")
    void testLargePopulationSize() {
        // Given
        int largeSize = 100;

        // When
        List<Timetable> population = populationService.initializePopulation(largeSize);

        // Then
        assertEquals(largeSize, population.size());
        // Verify all are valid
        for (Timetable timetable : population) {
            assertNotNull(timetable);
            assertFalse(timetable.getLessons().isEmpty());
        }
    }

    @Test
    @DisplayName("Teachers list should be consistent across calls")
    void testTeachersConsistency() {
        // When
        List<Teacher> teachers1 = populationService.getTeachers();
        List<Teacher> teachers2 = populationService.getTeachers();

        // Then
        assertEquals(teachers1.size(), teachers2.size());
        assertSame(teachers1, teachers2, "Should return same list reference");
    }

    @Test
    @DisplayName("Classrooms list should be consistent across calls")
    void testClassroomsConsistency() {
        // When
        List<Classroom> classrooms1 = populationService.getClassrooms();
        List<Classroom> classrooms2 = populationService.getClassrooms();

        // Then
        assertEquals(classrooms1.size(), classrooms2.size());
        assertSame(classrooms1, classrooms2, "Should return same list reference");
    }

    @Test
    @DisplayName("Groups list should be consistent across calls")
    void testGroupsConsistency() {
        // When
        List<Group> groups1 = populationService.getGroups();
        List<Group> groups2 = populationService.getGroups();

        // Then
        assertEquals(groups1.size(), groups2.size());
        assertSame(groups1, groups2, "Should return same list reference");
    }

    @Test
    @DisplayName("All initialized teachers should be unique")
    void testTeachersUniqueness() {
        // Given
        List<Teacher> teachers = populationService.getTeachers();

        // Then
        long uniqueIds = teachers.stream()
            .map(Teacher::getId)
            .distinct()
            .count();

        assertEquals(teachers.size(), uniqueIds, "All teachers should have unique IDs");
    }

    @Test
    @DisplayName("All initialized classrooms should be unique")
    void testClassroomsUniqueness() {
        // Given
        List<Classroom> classrooms = populationService.getClassrooms();

        // Then
        long uniqueIds = classrooms.stream()
            .map(Classroom::getId)
            .distinct()
            .count();

        assertEquals(classrooms.size(), uniqueIds, "All classrooms should have unique IDs");
    }

    @Test
    @DisplayName("All initialized groups should be unique")
    void testGroupsUniqueness() {
        // Given
        List<Group> groups = populationService.getGroups();

        // Then
        long uniqueIds = groups.stream()
            .map(Group::getId)
            .distinct()
            .count();

        assertEquals(groups.size(), uniqueIds, "All groups should have unique IDs");
    }

    @Test
    @DisplayName("Population service should work with standard GA configuration")
    void testWithStandardConfiguration() {
        // Given - using actual GA config
        int populationSize = SchoolConfig.GA_POPULATION_SIZE;

        // When
        List<Timetable> population = populationService.initializePopulation(populationSize);

        // Then
        assertEquals(populationSize, population.size());

        // All timetables should be valid
        for (Timetable timetable : population) {
            assertNotNull(timetable);
            assertTrue(timetable.getLessons().size() > 0);
        }
    }
}
