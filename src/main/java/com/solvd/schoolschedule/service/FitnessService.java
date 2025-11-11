package com.solvd.schoolschedule.service;

import com.solvd.schoolschedule.model.*;
import java.util.*;

/**
 * Service for evaluating timetable fitness based on scheduling constraints.
 */
public class FitnessService {

    private final PopulationService populationService;

    public FitnessService(PopulationService populationService) {
        this.populationService = populationService;
    }

    /**
     * Calculate fitness for a timetable
     * Higher fitness = better timetable (fewer constraint violations)
     * @param timetable the timetable to evaluate
     * @return fitness score
     */
    public double calculateFitness(Timetable timetable) {
        double fitness = 2000.0; // Base fitness (increased for more evolution room)

        // Penalize constraint violations with balanced enforcement of rules
        fitness -= calculateRoomConflicts(timetable) * 30;           // Room conflicts
        fitness -= calculateGroupGaps(timetable) * 50;               // Group gaps (no gaps rule)
        fitness -= calculateTeacherGaps(timetable) * 50;             // Teacher gaps (no gaps rule)
        fitness -= calculateMaxLessonsPerDayViolations(timetable) * 40; // Max 6 lessons/day
        fitness -= calculateTeacherLessonLimitViolations(timetable) * 80; // 2-3 lessons/day limit
        fitness -= calculateInvalidAssignments(timetable) * 100;     // Invalid assignments

        return Math.max(0, fitness); // Ensure non-negative fitness
    }

    /**
     * Count room conflicts (multiple lessons in same room at same time)
     * @param timetable the timetable
     * @return number of conflicts
     */
    private int calculateRoomConflicts(Timetable timetable) {
        int conflicts = 0;
        Map<TimeSlot, Set<Classroom>> timeSlotRooms = new HashMap<>();

        for (Lesson lesson : timetable.getLessons()) {
            TimeSlot timeSlot = lesson.getTimeSlot();
            Classroom classroom = lesson.getClassroom();

            timeSlotRooms.computeIfAbsent(timeSlot, k -> new HashSet<>());
            if (!timeSlotRooms.get(timeSlot).add(classroom)) {
                conflicts++;
            }
        }

        return conflicts;
    }

    /**
     * Calculate gaps in group schedules
     * Groups should have consecutive lessons without gaps
     * @param timetable the timetable
     * @return penalty score for gaps
     */
    private int calculateGroupGaps(Timetable timetable) {
        int totalGaps = 0;

        for (Group group : populationService.getGroups()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
                if (!dayLessons.isEmpty()) {
                    totalGaps += calculateGapsInDay(dayLessons);
                }
            }
        }

        return totalGaps;
    }

    /**
     * Calculate gaps in teacher schedules
     * Teachers should have consecutive lessons without gaps
     * @param timetable the timetable
     * @return penalty score for gaps
     */
    private int calculateTeacherGaps(Timetable timetable) {
        int totalGaps = 0;

        for (Teacher teacher : populationService.getTeachers()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);
                if (!dayLessons.isEmpty()) {
                    totalGaps += calculateGapsInDay(dayLessons);
                }
            }
        }

        return totalGaps;
    }

    /**
     * Calculate gaps in a day's lessons
     * @param dayLessons lessons for one day, sorted by period
     * @return number of gaps
     */
    private int calculateGapsInDay(List<Lesson> dayLessons) {
        if (dayLessons.size() <= 1) return 0;

        int gaps = 0;
        int previousPeriod = -1;

        for (Lesson lesson : dayLessons) {
            int currentPeriod = lesson.getTimeSlot().getPeriod();
            if (previousPeriod != -1 && currentPeriod > previousPeriod + 1) {
                gaps += currentPeriod - previousPeriod - 1;
            }
            previousPeriod = currentPeriod;
        }

        return gaps;
    }

    /**
     * Count violations of maximum lessons per day
     * @param timetable the timetable
     * @return number of violations
     */
    private int calculateMaxLessonsPerDayViolations(Timetable timetable) {
        int violations = 0;

        for (Group group : populationService.getGroups()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
                if (dayLessons.size() > SchoolConfig.MAX_PERIODS_PER_DAY) {
                    violations += dayLessons.size() - SchoolConfig.MAX_PERIODS_PER_DAY;
                }
            }
        }

        return violations;
    }

    /**
     * Count violations of teacher lesson limits (2-3 lessons per day)
     * @param timetable the timetable
     * @return number of violations
     */
    private int calculateTeacherLessonLimitViolations(Timetable timetable) {
        int violations = 0;

        for (Teacher teacher : populationService.getTeachers()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);
                int lessonCount = dayLessons.size();
                if (lessonCount < 2 || lessonCount > 3) {
                    violations++;
                }
            }
        }

        return violations;
    }

    /**
     * Count invalid assignments (teacher-subject mismatches, room-subject incompatibilities)
     * @param timetable the timetable
     * @return number of invalid assignments
     */
    private int calculateInvalidAssignments(Timetable timetable) {
        int invalid = 0;

        for (Lesson lesson : timetable.getLessons()) {
            // Check teacher-subject match
            if (lesson.getTeacher().getSubject() != lesson.getSubject()) {
                invalid++;
            }

            // Check room-subject compatibility
            if (!lesson.getClassroom().canAccommodate(lesson.getSubject())) {
                invalid++;
            }
        }

        return invalid;
    }

    /**
     * Evaluate the entire population and set fitness for each timetable
     * @param population the population to evaluate
     */
    public void evaluatePopulation(List<Timetable> population) {
        for (Timetable timetable : population) {
            double fitness = calculateFitness(timetable);
            timetable.setFitness(fitness);
        }
    }
}
