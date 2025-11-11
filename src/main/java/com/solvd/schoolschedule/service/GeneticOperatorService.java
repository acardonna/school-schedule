package com.solvd.schoolschedule.service;

import com.solvd.schoolschedule.model.*;
import java.util.*;

/**
 * Service for genetic operators: crossover and mutation.
 */
public class GeneticOperatorService {

    private final PopulationService populationService;
    private final Random random;
    private final double mutationRate;

    public GeneticOperatorService(PopulationService populationService, double mutationRate) {
        this.populationService = populationService;
        this.random = new Random();
        this.mutationRate = mutationRate;
    }

    /**
     * Perform crossover between two parent timetables
     * @param parent1 first parent
     * @param parent2 second parent
     * @return offspring timetable
     */
    public Timetable crossover(Timetable parent1, Timetable parent2) {
        List<Lesson> offspringLessons = new ArrayList<>();

        // For each group, take lessons from one parent or the other
        for (Group group : populationService.getGroups()) {
            List<Lesson> parent1Lessons = parent1.getLessonsForGroup(group);
            List<Lesson> parent2Lessons = parent2.getLessonsForGroup(group);

            // Randomly choose which parent's lessons to take for this group
            List<Lesson> selectedLessons = random.nextBoolean() ? parent1Lessons : parent2Lessons;
            offspringLessons.addAll(selectedLessons);
        }

        Timetable offspring = new Timetable(offspringLessons);
        return offspring;
    }

    /**
     * Perform mutation on a timetable
     * @param timetable the timetable to mutate
     * @return mutated timetable (or original if no mutation)
     */
    public Timetable mutate(Timetable timetable) {
        List<Lesson> lessons = new ArrayList<>(timetable.getLessons());
        boolean mutated = false;

        for (int i = 0; i < lessons.size(); i++) {
            if (random.nextDouble() < mutationRate) {
                Lesson originalLesson = lessons.get(i);
                Lesson mutatedLesson = mutateLesson(originalLesson);
                lessons.set(i, mutatedLesson);
                mutated = true;
            }
        }

        if (mutated) {
            return new Timetable(lessons);
        } else {
            return timetable; // Return original if no mutation occurred
        }
    }

    /**
     * Mutate a single lesson by changing its time slot or classroom
     * @param lesson the lesson to mutate
     * @return mutated lesson
     */
    private Lesson mutateLesson(Lesson lesson) {
        // Randomly choose what to mutate
        int mutationType = random.nextInt(3);

        switch (mutationType) {
            case 0: // Change time slot
                TimeSlot newTimeSlot = getRandomTimeSlot();
                return new Lesson(lesson.getSubject(), lesson.getTeacher(),
                                lesson.getClassroom(), newTimeSlot, lesson.getGroup());

            case 1: // Change classroom (if possible)
                Classroom newClassroom = getRandomClassroomForSubject(lesson.getSubject());
                return new Lesson(lesson.getSubject(), lesson.getTeacher(),
                                newClassroom, lesson.getTimeSlot(), lesson.getGroup());

            case 2: // Change both time slot and classroom
                TimeSlot newTimeSlot2 = getRandomTimeSlot();
                Classroom newClassroom2 = getRandomClassroomForSubject(lesson.getSubject());
                return new Lesson(lesson.getSubject(), lesson.getTeacher(),
                                newClassroom2, newTimeSlot2, lesson.getGroup());

            default:
                return lesson;
        }
    }

    /**
     * Get a random time slot
     * @return random time slot
     */
    private TimeSlot getRandomTimeSlot() {
        int day = random.nextInt(SchoolConfig.WORKING_DAYS_PER_WEEK);
        int period = random.nextInt(SchoolConfig.MAX_PERIODS_PER_DAY);
        return new TimeSlot(day, period);
    }

    /**
     * Get a random classroom suitable for the subject
     * @param subject the subject
     * @return suitable classroom
     */
    private Classroom getRandomClassroomForSubject(Subject subject) {
        List<Classroom> suitableClassrooms = populationService.getClassrooms().stream()
                .filter(c -> c.canAccommodate(subject))
                .toList();

        if (suitableClassrooms.isEmpty()) {
            throw new IllegalStateException("No classroom available for subject: " + subject);
        }

        return suitableClassrooms.get(random.nextInt(suitableClassrooms.size()));
    }

    /**
     * Create a new generation through selection, crossover, and mutation
     * @param population current population
     * @param selectionService selection service
     * @return new generation
     */
    public List<Timetable> createNewGeneration(List<Timetable> population, SelectionService selectionService) {
        List<Timetable> newGeneration = new ArrayList<>();

        // Elitism: keep the best individual
        Timetable best = population.stream()
                .max(Comparator.comparingDouble(Timetable::getFitness))
                .orElseThrow();
        newGeneration.add(best);

        // Fill the rest with offspring
        while (newGeneration.size() < population.size()) {
            // Select parents
            Timetable[] parents = selectionService.selectParents(population);

            // Crossover
            Timetable offspring = crossover(parents[0], parents[1]);

            // Mutation
            offspring = mutate(offspring);

            // Repair obvious constraint violations
            offspring = repairConstraints(offspring);

            newGeneration.add(offspring);
        }

        return newGeneration;
    }

    /**
     * Repair obvious constraint violations in a timetable
     * @param timetable the timetable to repair
     * @return repaired timetable
     */
    private Timetable repairConstraints(Timetable timetable) {
        List<Lesson> lessons = new ArrayList<>(timetable.getLessons());
        boolean repaired = false;

        // Fix teacher lesson count violations (keep only 2-3 lessons per day per teacher)
        Map<Teacher, Map<Integer, List<Lesson>>> teacherDayLessons = new HashMap<>();
        for (Lesson lesson : lessons) {
            teacherDayLessons.computeIfAbsent(lesson.getTeacher(), k -> new HashMap<>())
                    .computeIfAbsent(lesson.getTimeSlot().getDay(), k -> new ArrayList<>())
                    .add(lesson);
        }

        List<Lesson> repairedLessons = new ArrayList<>();
        for (Lesson lesson : lessons) {
            Teacher teacher = lesson.getTeacher();
            int day = lesson.getTimeSlot().getDay();
            List<Lesson> dayLessons = teacherDayLessons.get(teacher).get(day);

            // If teacher has more than 3 lessons this day, skip excess lessons
            if (dayLessons.size() > 3) {
                // Keep only the first 3 lessons for this teacher-day
                if (dayLessons.indexOf(lesson) >= 3) {
                    repaired = true;
                    continue; // Skip this lesson
                }
            }
            repairedLessons.add(lesson);
        }

        if (repaired) {
            return new Timetable(repairedLessons);
        } else {
            return timetable;
        }
    }
}
