package com.demo.elevator.system;

import java.util.List;

import com.demo.elevator.model.Elevator;
import com.demo.elevator.model.User;

public interface ElevatorSystem {
    
    /**
     * Operations for ElevatorSystem reset
     * @return elevator list
     */
    public List<Elevator> reset();
    
    /**
     * Operations for ElevatorSystem handle with work load
     * @param users
     * @return elevator list
     */
    public List<Elevator> actionWithWorkLoad(List<User> users);
}
