package com.demo.elevator.model;

public class ImmutableElevator {
    private Elevator elevator;

    public ImmutableElevator(Elevator elevator) {
        this.elevator = elevator;
    }

    public int getId() {
        return elevator.getId();
    }

    public int getCurrentFloor() {
        return elevator.getCurrentFloor();
    }

    public int getTargetFloor() {
        return elevator.getTargetFloor();
    }

    public ElevatorStatus getStatus() {
        return elevator.getStatus();
    }

    public int getUserCount() {
        return elevator.getUserCount();
    }

    public boolean isPendingToMove() {
        return elevator.isPendingToMove();
    }
}
