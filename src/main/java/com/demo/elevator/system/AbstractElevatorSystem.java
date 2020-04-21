package com.demo.elevator.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo.elevator.exception.CapacityExceededException;
import com.demo.elevator.model.Elevator;
import com.demo.elevator.model.ElevatorStatus;
import com.demo.elevator.model.ImmutableElevator;
import com.demo.elevator.model.User;

/**
 * AbstractElevatorSystem provide basic operation between waiting users and elevators
 * 
 * @author Cary
 *
 */
public abstract class AbstractElevatorSystem implements ElevatorSystem {
    protected final int topFloor = 11;

    /*
     * A Map maintains users who want to go down stair, MapKey user start floor, value List of users
     * with startFloor as map key
     */
    protected Map<Integer, List<User>> goDownStairUserMap = new HashMap<Integer, List<User>>();
    /*
     * A Map maintains users who want to go up stair, MapKey user start floor, value List of users
     * with startFloor as map key
     */
    protected Map<Integer, List<User>> goUpStairUserMap = new HashMap<Integer, List<User>>();

    protected List<Elevator> elevators = new ArrayList<>();

    /**
     * Logic to handle new workLoad with users input 1. parse and insert users to goDownStairUserMap
     * and goUpStairUserMap 2. for each elevator it will handle arrived users and new users to enter
     * to elevator
     * 
     * The method is set to synchronized to prevent AbstractElevatorSystem and Elevator internal status mass under multi-thread.
     * the elevator system will not face high concurrency situation in real work, make the api serialized is reasonable
     * 
     * @param users users in new workload
     * @return elevators list
     */
    @Override
    public synchronized List<Elevator> actionWithWorkLoad(List<User> users) {
        if (!users.isEmpty()) {
            updateWaitingUsers(users);
        }

        for (Elevator elevator : elevators) {
            actionOnElevator(elevator);
        }

        return elevators;
    }

    /**
     * Reset the AbstractElevatorSystem status and elevator status, clear waiting users collection
     * and elevation users data and flags.
     * 
     * The method is set to synchronized to prevent AbstractElevatorSystem and Elevator internal status mass under multi-thread.
     * the elevator system will not face high concurrency situation in real work, make the api serialized is reasonable
     * 
     */
    @Override
    public synchronized List<Elevator> reset() {
        goDownStairUserMap.clear();
        goUpStairUserMap.clear();
        elevators.forEach(elevator -> elevator.reset());
        return elevators;
    }

    /**
     * Get elevator count in idle status
     * 
     * @return elevator count in idle status
     */
    protected long idleElevatorCount() {
        return elevators.stream().filter(elevator -> elevator.getStatus() == ElevatorStatus.Idle)
                .count();
    }

    /**
     * @return user count waiting To go down stair
     */
    protected int userCountWaitingToGoDownStair() {
        int count = 0;
        for (List<User> list : goDownStairUserMap.values()) {
            count += list.size();
        }
        return count;
    }

    /**
     * @return user count waiting To go up stair
     */
    protected int userCountWaitingToGoUpStair() {
        int count = 0;
        for (List<User> list : goUpStairUserMap.values()) {
            count += list.size();
        }
        return count;
    }

    /**
     * 
     * @return total waiting user count
     */
    protected int totalWaitingCount() {
        return userCountWaitingToGoDownStair() + userCountWaitingToGoUpStair();
    }

    /**
     * abstract method used for design customized logic to decide the idled elevator next move. the
     * method require a target floor return value, so the idle elevator could decide go up or down
     * with it current floor
     * 
     * @param immutableELevator a immutable ELevator instance, data and status could be used to
     *        calculated a target floor.
     * 
     * @return target floor value
     */
    abstract protected int nextFloorDecision4IdleElevator(ImmutableElevator immutableELevator);


    private void updateWaitingUsers(List<User> users) {
        users.forEach(user -> addUserToWaitingMap(user));
    }

    private void addUserToWaitingMap(User user) {
        Map<Integer, List<User>> waitingMap;
        if (user.getEndFloor() > user.getStartFloor()) {
            waitingMap = goUpStairUserMap;
        } else {
            waitingMap = goDownStairUserMap;
        }

        List<User> users = waitingMap.get(user.getStartFloor());
        if (users == null) {
            users = new ArrayList<User>();
            waitingMap.put(user.getStartFloor(), users);
        }
        users.add(user);
    }

    /**
     * Elevator action to handle step out users and new users entered for each floor
     * 
     * @param elevator
     */
    private void actionOnElevator(Elevator elevator) {

        if (elevator.isPendingToMove()) {
            // elevator handle user in and out in last step will not process user in current step,
            // directly move to next step
            elevator.setPendingToMove(false);
            elevator.nextMove();
        } else {
            int currentFloor = elevator.getCurrentFloor();
            boolean hasUserArrived = elevator.hasUserArrived();
            boolean hasUserEntered = false;

            // process current floor arrived users
            if (hasUserArrived) {
                elevator.userArrivedAction();
            }

            List<User> userToEnter = null;

            idleElevatorAction(elevator);

            // process current floor users to enter
            if (elevator.getStatus() == ElevatorStatus.Up) {
                userToEnter = goUpStairUserMap.get(currentFloor);
            } else if (elevator.getStatus() == ElevatorStatus.Down) {
                userToEnter = goDownStairUserMap.get(currentFloor);
            }

            if (userToEnter != null && !userToEnter.isEmpty()) {
                if (userEnterProcess(userToEnter, elevator) != 0) {
                    hasUserEntered = true;
                }
            }

            // if no enter or left action elevator will move to next step, else will set elevator to
            // pending status and move to next in another round
            if (hasUserEntered || hasUserArrived) {
                elevator.setPendingToMove(true);
            } else {
                elevator.nextMove();
            }
        }
    }

    /**
     * for idle elevator will decide keep idle or move up or down base on method
     * nextFloorDecision4IdleElevator()
     * 
     * @param elevator
     */
    private void idleElevatorAction(Elevator elevator) {
        if (elevator.getStatus() == ElevatorStatus.Idle) {
            if (totalWaitingCount() == 0 && elevator.getUserCount() == 0) {
                return;
            } else {
                ImmutableElevator immutableELevator = new ImmutableElevator(elevator);
                int targetFloor = nextFloorDecision4IdleElevator(immutableELevator);
                elevator.setTargetFloor(targetFloor);
            }
        }

    }


    private int userEnterProcess(List<User> userToEnter, Elevator elevator) {
        int enterUserCount = 0;
        try {
            int capacity = Elevator.capacity - elevator.getUserCount();
            int end = userToEnter.size() <= capacity ? userToEnter.size() : capacity + 1;
            List<User> enteredUser = userToEnter.subList(0, end);
            elevator.userEnterAction(enteredUser);
            enterUserCount = enteredUser.size();
            userToEnter.removeAll(enteredUser);
        } catch (CapacityExceededException e) {
            // TODO LOG
        }
        return enterUserCount;
    }


}
