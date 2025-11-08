package com.solvd.schoolschedule.model;

/**
 * Represents a time slot (day, period) in the school schedule.
 */
public class TimeSlot {
    private final int day; // 0-4 (Monday to Friday)
    private final int period; // 0-5 (periods 1-6)

    public TimeSlot(int day, int period) {
        this.day = day;
        this.period = period;
    }

    public int getDay() {
        return day;
    }

    public int getPeriod() {
        return period;
    }

    public String getDayName() {
        return switch (day) {
            case 0 -> "Monday";
            case 1 -> "Tuesday";
            case 2 -> "Wednesday";
            case 3 -> "Thursday";
            case 4 -> "Friday";
            default -> "Unknown";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return day == timeSlot.day && period == timeSlot.period;
    }

    @Override
    public int hashCode() {
        return 31 * day + period;
    }

    @Override
    public String toString() {
        return getDayName() + " - Period " + (period + 1);
    }
}