package com.demo.elevator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.elevator.model.Elevator;
import com.demo.elevator.model.User;
import com.demo.elevator.system.ElevatorSystem;

/**
 * ElevatorService 
 * @author Administrator
 *
 */
@Service
public class ElevatorService {
    
    @Autowired
    private ElevatorSystem elevatorSystem;

    /**
     * reset service
     * @return elevator list with each elevator status after reset operation
     */
    public List<Elevator> reset() {
        return elevatorSystem.reset();
    }

    /**
     * Handle new user load
     * @param new users
     * @return elevator list with each elevator status after this operation
     */
    public List<Elevator> newWorkLoad(List<User> users) {
        return elevatorSystem.actionWithWorkLoad(users);
    }
}
