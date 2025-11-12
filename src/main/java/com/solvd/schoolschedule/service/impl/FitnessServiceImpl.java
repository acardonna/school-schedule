package com.solvd.schoolschedule.service.impl;

import java.util.*;
import java.util.stream.IntStream;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.*;

/**
 * Service for evaluating timetable fitness based on scheduling constraints.
 */
public class FitnessServiceImpl implements IFitnessService {

    private final IPopulationService populationService;

    public FitnessServiceImpl(IPopulationService populationService) {
        this.populationService = populationService;
    }

    /**
     * Calculate fitness for a timetable
     * Higher fitness = better timetable (fewer constraint violations)
     * @param timetable the timetable to evaluate
     * @return fitness score
     */
    @Override
    public double calculateFitness(Timetable timetable) {
        double fitness = 2000.0; // Base fitness (increased for more evolution room)

        // Penalize constraint violations with balanced enforcement of rules
        fitness -= calculateRoomConflicts(timetable) * 30;           // Room conflicts
        fitness -= calculateGroupGaps(timetable) * 50;               // Group gaps (no gaps rule)
        fitness -= calculateTeacherGaps(timetable) * 50;             // Teacher gaps (no gaps rule)
        fitness -= calculateMaxLessonsPerDayViolations(timetable) * 40; // Max 6 lessons/day
        fitness -= calculateTeacherLessonLimitViolations(timetable) * 0; // 2-3 lessons/day limit
        fitness -= calculateInvalidAssignments(timetable) * 100;     // Invalid assignments
        fitness -= calculateGroupCollisions(timetable) * 50;        //No 2 lessons at the same time for a group
        fitness -= calculateTeacherCollisions(timetable) * 50;      //No 2 lessons at the same time for a teacher
        fitness -= calculateLastLesson(timetable) * 30;             //Last lesson should always be Physical Culture
        fitness -= calculateAdjustment(timetable) * 100;             //The number of lessons per subject

        // return Math.max(0, fitness); // Ensure non-negative fitness
        return fitness;
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
    @Override
    public void evaluatePopulation(List<Timetable> population) {
        for (Timetable timetable : population) {
            double fitness = calculateFitness(timetable);
            timetable.setFitness(fitness);
        }
    }

    /**
     * Count invalid number of lessons at the same timeslot for groups
     * @param timetable the timetable
     * @return number of lesson collisions
     */
    private int calculateGroupCollisions(Timetable timetable) {
        int totalCollisions = 0;

        for (Group group : populationService.getGroups()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
                if (!dayLessons.isEmpty()) {
                    totalCollisions += calculateCollisionsInDay(dayLessons);
                }
            }
        }

        return totalCollisions;
    }

    /**
     * Count invalid number of lessons at the same timeslot for teachers
     * @param timetable the timetable
     * @return number of lesson collisions
     */
    private int calculateTeacherCollisions(Timetable timetable) {
        int totalCollisions = 0;

        for (Teacher teacher : populationService.getTeachers()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);
                if (!dayLessons.isEmpty()) {
                    totalCollisions += calculateCollisionsInDay(dayLessons);
                }
            }
        }

        return totalCollisions;
    }

    /**
     * Count invalid number of lessons at the same timeslot for one day
     * @param dayLessons list of lessons for the day
     * @return number of lesson collisions
     */
    private int calculateCollisionsInDay(List<Lesson> dayLessons) {
        if (dayLessons.size() <= 1) return 0;

        int collisions = 0;

        ArrayList<Integer> collisionsList=new ArrayList<>(6);
        IntStream.range(0,6).forEach(i->collisionsList.add(0));
        for (Lesson lesson : dayLessons) {
            int period=lesson.getTimeSlot().getPeriod();
            int accumulated=collisionsList.get(period);
            collisionsList.set(period,accumulated+1);
        }

        for (Integer i : collisionsList) {
            if(i>1) collisions+=i-1;
        }

        return collisions;
    }

    /**
     * Count the number of violations of the rule:
     * Last lesson should be Physical Culture
     * @param timetable the timetable
     * @return number of violations of the rule
     */
    private int calculateLastLesson(Timetable timetable) {
        int totalViolations = 0;

        for (Group group : populationService.getGroups()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
                if (!dayLessons.isEmpty()) {
                    totalViolations += calculateLastLessonInDay(dayLessons);
                }
            }
        }

        return totalViolations;
    }

    /**
     * Checks if that day violates the rule:
     * Last lesson should be Physical Culture
     * @param dayLessons list of lessons for the day
     * @return 0 or 1, depending on if that day violates the rule
     */
    private int calculateLastLessonInDay(List<Lesson> dayLessons) {
        if (dayLessons.isEmpty()) return 0;

        boolean hasPhysicalCulture=dayLessons.stream()
                .map(Lesson::getSubject)
                .anyMatch(subject -> subject.equals(Subject.PHYSICAL_CULTURE));

        if (hasPhysicalCulture && !dayLessons.getLast().getSubject().equals(Subject.PHYSICAL_CULTURE)){
            return 1;
        }
        return 0;
    }

    /**
     * Count the number of lessons that the timetable
     * doesn't have yet (compared to the SchoolConfig)
     * @param timetable the timetable
     * @return needed number of lessons to be added
     */
    private int calculateAdjustment(Timetable timetable) {
        int totalViolations = 0;

        for (Group group : populationService.getGroups()) {
            List<Lesson> lessons = timetable.getLessonsForGroup(group);
            totalViolations += calculateGroupAdjustment(lessons);

            }

        return totalViolations;
    }

    /**
     * Count the number of lessons that the group
     * doesn't have yet (compared to the SchoolConfig)
     * @param lessons list of lessons for the group
     * @return needed number of lessons to be added
     */
    @Override
    public int calculateGroupAdjustment(List<Lesson> lessons) {

        int violations = 0;

        for (Subject subject:Subject.values()) {
            int lessonsNumberConfig=SubjectConfig.getWeeklyLessons(subject);

            int lessonsNumber=(int) lessons.stream()
                    .filter(lesson -> lesson.getSubject().equals(subject))
                    .count();

            violations+=Math.abs(lessonsNumberConfig-lessonsNumber);
        }

        return violations;
    }

}
