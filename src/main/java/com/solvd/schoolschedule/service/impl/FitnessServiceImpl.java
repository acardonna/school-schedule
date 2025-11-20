package com.solvd.schoolschedule.service.impl;

import java.util.*;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.impl.conflictstrategies.*;
import com.solvd.schoolschedule.service.interfaces.*;

/**
 * Service for evaluating timetable fitness based on scheduling constraints.
 */
public class FitnessServiceImpl implements IFitnessService {

    private final IPopulationService populationService;
    private final Map<IConflictStrategy, Integer> rules;

    public FitnessServiceImpl(IPopulationService populationService) {
        this.populationService = populationService;
        this.rules= createConflictStrategies();

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

        for(IConflictStrategy conflictStrategy: rules.keySet()){
            fitness-=conflictStrategy.calculateConflicts(timetable)*rules.get(conflictStrategy);
        }

        return fitness;
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

    public Map<IConflictStrategy, Integer> createConflictStrategies(){
        Map<IConflictStrategy, Integer> conflictStrategies=new HashMap<>();
        RoomConflicts roomConflicts =new RoomConflicts();
        RoomAccomodate roomAccomodate=new RoomAccomodate();
        GroupGaps groupGaps=new GroupGaps(populationService);
        TeacherGaps teacherGaps=new TeacherGaps(populationService);
        MaxLessonsPerDay maxLessonsPerDay=new MaxLessonsPerDay(populationService);
        InvalidAssignments invalidAssignments=new InvalidAssignments();
        GroupCollisions groupCollisions=new GroupCollisions(populationService);
        TeacherCollisions teacherCollisions=new TeacherCollisions(populationService);
        LastLesson lastLesson=new LastLesson(populationService);
        Adjustment adjustment =new Adjustment(populationService);

        conflictStrategies.put(roomConflicts,30);       // Room conflicts
        conflictStrategies.put(roomAccomodate,50);      // Special Room accommodation for subjects
        conflictStrategies.put(groupGaps,50);           // Group gaps (no gaps rule)
        conflictStrategies.put(teacherGaps,50);         // Teacher gaps (no gaps rule)
        conflictStrategies.put(maxLessonsPerDay,40);    // Max 6 lessons/day
        conflictStrategies.put(invalidAssignments,100); // Invalid assignments
        conflictStrategies.put(groupCollisions,50);     //No 2 lessons at the same time for a group
        conflictStrategies.put(teacherCollisions,50);   //No 2 lessons at the same time for a teacher
        conflictStrategies.put(lastLesson,30);          //Last lesson should always be Physical Culture
        conflictStrategies.put(adjustment,100);         //The number of lessons per subject

        return conflictStrategies;
    }

}
