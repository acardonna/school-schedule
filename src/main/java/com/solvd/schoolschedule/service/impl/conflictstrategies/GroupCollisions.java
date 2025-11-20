package com.solvd.schoolschedule.service.impl.conflictstrategies;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.service.interfaces.IConflictStrategy;
import com.solvd.schoolschedule.service.interfaces.IPopulationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GroupCollisions implements IConflictStrategy {
    private ConflictType conflictType = ConflictType.GROUP_COLLISIONS;
    private IPopulationService populationService;

    public GroupCollisions(IPopulationService populationService) {
        this.populationService = populationService;
    }

    @Override
    public ConflictType getConflictType() {
        return conflictType;
    }

    @Override
    public int calculateConflicts(Timetable timetable) {
        int totalCollisions = 0;

        for (Group group : populationService.getGroups()) {
            for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
                List<Lesson> dayLessons = timetable.getLessonsOnDayFor(group, day);
                if (!dayLessons.isEmpty()) {
                    totalCollisions += calculateCollisionsInDay(dayLessons);
                }
            }
        }

        return totalCollisions;
    }

    /**
     * Count invalid number of lessons at the same timeslot for one day
     *
     * @param dayLessons list of lessons for the day
     * @return number of lesson collisions
     */
    private int calculateCollisionsInDay(List<Lesson> dayLessons) {
        if (dayLessons.size() <= 1) return 0;

        int collisions = 0;

        ArrayList<Integer> collisionsList = new ArrayList<>(6);
        IntStream.range(0, 6).forEach(i -> collisionsList.add(0));
        for (Lesson lesson : dayLessons) {
            int period = lesson.getTimeSlot().getPeriod();
            int accumulated = collisionsList.get(period);
            collisionsList.set(period, accumulated + 1);
        }

        for (Integer i : collisionsList) {
            if (i > 1) collisions += i - 1;
        }

        for (Lesson lesson : dayLessons) {
            int period = lesson.getTimeSlot().getPeriod();
            if (collisionsList.get(period) > 1) {
                lesson.setConflicted(true);
            }
        }


        return collisions;
    }
}
