package com.demo.elevator.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.elevator.model.Elevator;
import com.demo.elevator.model.User;
import com.demo.elevator.model.view.ElevatorView;
import com.demo.elevator.service.ElevatorService;

@RestController
public class ElevatorController {

    @Autowired
    private ElevatorService elevatorService;

    @PostMapping("/reset")
    public List<ElevatorView> reset() {
        List<ElevatorView> view = new ArrayList<ElevatorView>();
        List<Elevator> elevators = elevatorService.reset();
        elevators.forEach(elevator -> view.add(toElevatorView(elevator)));
        return view;
    }

    @PostMapping("/workload")
    public List<ElevatorView> workload(@RequestBody List<User> users) {
        List<ElevatorView> view = new ArrayList<ElevatorView>();
        List<Elevator> elevators = elevatorService.newWorkLoad(users);
        elevators.forEach(elevator -> view.add(toElevatorView(elevator)));
        return view;
    }

    private ElevatorView toElevatorView(Elevator elevator) {
        ElevatorView elevatorView = null;
        if (elevator != null) {
            elevatorView = new ElevatorView(elevator.getId(), elevator.getCurrentFloor());
            elevatorView.setUsers(elevator.listElevatorUsers());
        }
        return elevatorView;
    }

}
