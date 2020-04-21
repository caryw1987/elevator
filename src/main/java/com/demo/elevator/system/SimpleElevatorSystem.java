package com.demo.elevator.system;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.demo.elevator.model.Elevator;
import com.demo.elevator.model.ImmutableElevator;

@Component
public class SimpleElevatorSystem extends AbstractElevatorSystem {

    @Value("${elevator.count}")
    private int elevatorCount;

    /**
     * init elevators and set target floor distributed
     */
    @PostConstruct
    public void initElevators() {
        for (int i = 0; i < elevatorCount; i++) {
            elevators.add(new Elevator(i + 1, 1, 1 + (i * 5)));
        }
    }

    /**
     * Sample way to handle idled elevators, if not waiting user exist set target floor same as
     * current floor.
     * 
     * Or will base on current floor number, if in higher stair the target floor will go down to
     * floor 1, otherwise will to top floor
     */
    @Override
    protected int nextFloorDecision4IdleElevator(ImmutableElevator immutableELevator) {
        int currentFloor = immutableELevator.getCurrentFloor();

        if (totalWaitingCount() == 0) {
            return immutableELevator.getCurrentFloor();
        }

        return currentFloor > topFloor / 2 ? 1 : topFloor;
    }
}
