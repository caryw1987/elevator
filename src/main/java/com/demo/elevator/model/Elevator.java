package com.demo.elevator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.demo.elevator.exception.CapacityExceededException;

public class Elevator {

    public static final int capacity = 20;

    private int id;
    private int currentFloor = 1;
    private int targetFloor = 1;
    
    /*
     *  floorUserMap to maintain user in elevator, the key is user endFloor, value is list of users in same endFloor
     */
    private Map<Integer, List<User>> floorUserMap = new HashMap<>();
    
    /*
     * if elevator handled user in and out in current step will set isPendingToMove true
     */
    private boolean isPendingToMove = false;

    public Elevator(int id, int currentFloor, int targetFloor) {
        this.id = id;
        this.currentFloor = currentFloor;
        this.targetFloor = targetFloor;
    }

    public List<String> listElevatorUsers() {
        return floorUserMap.values().stream().flatMap(x -> x.stream()).map(user -> user.getUser())
                .collect(Collectors.toList());
    }

    /**
     * Handle a user enter elevator action
     * @param userToEnter
     * @throws CapacityExceededException if elevator reach the capacity size, will throw exception
     */
    public void userEnterAction(User userToEnter) throws CapacityExceededException {
        if (getUserCount() < capacity) {
            int userEndFloor = userToEnter.getEndFloor();
            List<User> users = floorUserMap.get(userEndFloor);
            if (users == null) {
                users = new ArrayList<User>();
                floorUserMap.put(userEndFloor, users);
            }
            users.add(userToEnter);
            updateTargetFloor(userEndFloor);
        } else {
            throw new CapacityExceededException("Elevator capacity limit is reached");
        }
    }

    /**
     * Handle users enter elevator action
     * @param userToEnter list
     * @throws CapacityExceededException if elevator reach the capacity size, will throw exception
     */
    public void userEnterAction(List<User> usersToEnter) throws CapacityExceededException {
        if (usersToEnter.size() <= capacity - getUserCount()) {
            usersToEnter.forEach(userToEnter -> {
                try {
                    userEnterAction(userToEnter);
                } catch (CapacityExceededException e) {
                    // TODO log
                }
            });
        } else {
            throw new CapacityExceededException("Elevator capacity limit is reached");
        }
    }

    /**
     * @return if there is user to step out in current floor 
     */
    public boolean hasUserArrived() {
        List<User> userArrived = floorUserMap.get(currentFloor);
        return userArrived != null && !userArrived.isEmpty();
    }

    /**
     * Handle users whose endfloor equals current floor
     */
    public void userArrivedAction() {
        List<User> userToLeave = floorUserMap.get(currentFloor);
        if (userToLeave != null) {
            userToLeave.clear();
        }
    }

    /**
     * elevator go down or up
     */
    public void nextMove() {
        if (getStatus() == ElevatorStatus.Up) {
            currentFloor++;
        } else if (getStatus() == ElevatorStatus.Down) {
            currentFloor--;
        }
    }

    /**
     * Reset elevators
     */
    public void reset() {
        currentFloor = 1;
        targetFloor = 1;
        floorUserMap.clear();
        isPendingToMove = false;
    }


    /**
     * User count in elevator
     * @return
     */
    public int getUserCount() {
        int count = 0;
        for (List<User> users : floorUserMap.values()) {
            count += users.size();
        }
        return count;
    }

    /**
     * new User added in elevator, will need to update the target floor
     * @param userEndFloor
     */
    private void updateTargetFloor(int userEndFloor) {
        if (getStatus() == ElevatorStatus.Up) {
            targetFloor = targetFloor >= userEndFloor ? targetFloor : userEndFloor;
        } else if (getStatus() == ElevatorStatus.Down) {
            targetFloor = targetFloor <= userEndFloor ? targetFloor : userEndFloor;
        } else {
            targetFloor = userEndFloor;
        }
    }

    public ElevatorStatus getStatus() {
        if (targetFloor > currentFloor) {
            return ElevatorStatus.Up;
        } else if (targetFloor < currentFloor) {
            return ElevatorStatus.Down;
        } else {
            return ElevatorStatus.Idle;
        }
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int setTargetFloor(int targetFloor) {
        return this.targetFloor = targetFloor;
    }
    
    public int getTargetFloor() {
        return targetFloor;
    }

    public boolean isPendingToMove() {
        return isPendingToMove;
    }

    public void setPendingToMove(boolean isPendingToMove) {
        this.isPendingToMove = isPendingToMove;
    }

}
