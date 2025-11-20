package com.solvd.schoolschedule.model;

public class Conflict {
    private ConflictType conflictType;
    private int number;

    public Conflict(ConflictType conflictType, int number) {
        this.conflictType = conflictType;
        this.number = number;
    }

    public ConflictType getConflictType() {
        return conflictType;
    }

    public int getNumber() {
        return number;
    }

    public void setConflictType(ConflictType conflictType) {
        this.conflictType = conflictType;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
