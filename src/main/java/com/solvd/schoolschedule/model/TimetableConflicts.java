package com.solvd.schoolschedule.model;

import java.util.List;

public class TimetableConflicts {
    private int generation;
    private double fitness;
    private List<Conflict> conflicts;

    public int getGeneration() {
        return generation;
    }

    public double getFitness() {
        return fitness;
    }

    public List<Conflict> getConflicts() {
        return conflicts;
    }

    public TimetableConflicts(Timetable timetable) {
        this.generation = timetable.getGeneration();
        this.fitness = timetable.getFitness();
        this.conflicts = timetable.getConflicts();
    }
}
