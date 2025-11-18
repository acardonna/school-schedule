package com.solvd.schoolschedule.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Unit tests for the Subject enum.
 * Tests enum values, display names, and enum behavior.
 */
@DisplayName("Subject Tests")
class SubjectTest {

    @Test
    @DisplayName("Should have exactly 4 subjects")
    void testNumberOfSubjects() {
        assertEquals(4, Subject.values().length);
    }

    @Test
    @DisplayName("Should have MATH subject")
    void testMathExists() {
        assertNotNull(Subject.MATH);
    }

    @Test
    @DisplayName("Should have PHYSICS subject")
    void testPhysicsExists() {
        assertNotNull(Subject.PHYSICS);
    }

    @Test
    @DisplayName("Should have INFORMATICS subject")
    void testInformaticsExists() {
        assertNotNull(Subject.INFORMATICS);
    }

    @Test
    @DisplayName("Should have PHYSICAL_CULTURE subject")
    void testPhysicalCultureExists() {
        assertNotNull(Subject.PHYSICAL_CULTURE);
    }

    @Test
    @DisplayName("MATH should have correct display name")
    void testMathDisplayName() {
        assertEquals("Mathematics", Subject.MATH.getDisplayName());
    }

    @Test
    @DisplayName("PHYSICS should have correct display name")
    void testPhysicsDisplayName() {
        assertEquals("Physics", Subject.PHYSICS.getDisplayName());
    }

    @Test
    @DisplayName("INFORMATICS should have correct display name")
    void testInformaticsDisplayName() {
        assertEquals("Informatics", Subject.INFORMATICS.getDisplayName());
    }

    @Test
    @DisplayName("PHYSICAL_CULTURE should have correct display name")
    void testPhysicalCultureDisplayName() {
        assertEquals("Physical Culture", Subject.PHYSICAL_CULTURE.getDisplayName());
    }

    @ParameterizedTest
    @DisplayName("All subjects should have non-null display names")
    @EnumSource(Subject.class)
    void testAllDisplayNamesNotNull(Subject subject) {
        assertNotNull(subject.getDisplayName());
    }

    @ParameterizedTest
    @DisplayName("All subjects should have non-empty display names")
    @EnumSource(Subject.class)
    void testAllDisplayNamesNotEmpty(Subject subject) {
        assertFalse(subject.getDisplayName().isEmpty());
    }

    @Test
    @DisplayName("Should retrieve subject by name using valueOf")
    void testValueOf() {
        assertEquals(Subject.MATH, Subject.valueOf("MATH"));
        assertEquals(Subject.PHYSICS, Subject.valueOf("PHYSICS"));
        assertEquals(Subject.INFORMATICS, Subject.valueOf("INFORMATICS"));
        assertEquals(Subject.PHYSICAL_CULTURE, Subject.valueOf("PHYSICAL_CULTURE"));
    }

    @Test
    @DisplayName("valueOf should throw exception for invalid name")
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            Subject.valueOf("INVALID_SUBJECT");
        });
    }

    @Test
    @DisplayName("valueOf should be case-sensitive")
    void testValueOfCaseSensitive() {
        assertThrows(IllegalArgumentException.class, () -> {
            Subject.valueOf("math"); // lowercase should fail
        });
    }

    @Test
    @DisplayName("Subjects should be comparable (enum ordinal)")
    void testEnumOrdering() {
        // Enums maintain declaration order
        Subject[] subjects = Subject.values();

        assertEquals(Subject.MATH, subjects[0]);
        assertEquals(Subject.PHYSICS, subjects[1]);
        assertEquals(Subject.INFORMATICS, subjects[2]);
        assertEquals(Subject.PHYSICAL_CULTURE, subjects[3]);
    }

    @Test
    @DisplayName("Same subject references should be equal")
    void testEqualitySameReference() {
        Subject math1 = Subject.MATH;
        Subject math2 = Subject.MATH;

        assertSame(math1, math2);
        assertEquals(math1, math2);
    }

    @Test
    @DisplayName("Different subjects should not be equal")
    void testEqualityDifferentSubjects() {
        assertNotEquals(Subject.MATH, Subject.PHYSICS);
        assertNotEquals(Subject.INFORMATICS, Subject.PHYSICAL_CULTURE);
    }

    @Test
    @DisplayName("Subject enum should be usable in switch statements")
    void testSwitchStatement() {
        String result = switch (Subject.MATH) {
            case MATH -> "Mathematics";
            case PHYSICS -> "Physics";
            case INFORMATICS -> "Informatics";
            case PHYSICAL_CULTURE -> "Physical Culture";
        };

        assertEquals("Mathematics", result);
    }

    @Test
    @DisplayName("Subject toString should return enum name")
    void testToString() {
        assertEquals("MATH", Subject.MATH.toString());
        assertEquals("PHYSICS", Subject.PHYSICS.toString());
        assertEquals("INFORMATICS", Subject.INFORMATICS.toString());
        assertEquals("PHYSICAL_CULTURE", Subject.PHYSICAL_CULTURE.toString());
    }

    @Test
    @DisplayName("Display names should be properly capitalized")
    void testDisplayNameCapitalization() {
        // Check that display names start with capital letters
        for (Subject subject : Subject.values()) {
            String displayName = subject.getDisplayName();
            assertTrue(Character.isUpperCase(displayName.charAt(0)),
                "Display name should start with capital letter: " + displayName);
        }
    }

    @Test
    @DisplayName("Subject enum should be usable in collections")
    void testUsageInCollections() {
        java.util.Set<Subject> subjects = java.util.Set.of(
            Subject.MATH,
            Subject.PHYSICS,
            Subject.INFORMATICS,
            Subject.PHYSICAL_CULTURE
        );

        assertEquals(4, subjects.size());
        assertTrue(subjects.contains(Subject.MATH));
        assertTrue(subjects.contains(Subject.PHYSICS));
    }

    @Test
    @DisplayName("Each subject should have unique display name")
    void testUniqueDisplayNames() {
        java.util.Set<String> displayNames = new java.util.HashSet<>();

        for (Subject subject : Subject.values()) {
            boolean wasAdded = displayNames.add(subject.getDisplayName());
            assertTrue(wasAdded, "Display name should be unique: " + subject.getDisplayName());
        }

        assertEquals(4, displayNames.size());
    }

    @Test
    @DisplayName("Subject name should match expected naming convention")
    void testNamingConvention() {
        // Verify enum constants follow UPPER_SNAKE_CASE convention
        assertEquals("MATH", Subject.MATH.name());
        assertEquals("PHYSICS", Subject.PHYSICS.name());
        assertEquals("INFORMATICS", Subject.INFORMATICS.name());
        assertEquals("PHYSICAL_CULTURE", Subject.PHYSICAL_CULTURE.name());
    }
}
