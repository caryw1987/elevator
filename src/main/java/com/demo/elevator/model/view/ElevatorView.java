package com.demo.elevator.model.view;

import java.util.ArrayList;
import java.util.List;

public class ElevatorView {

    private Integer id;
    private int floor;
    private List<String> users = new ArrayList<String>();

    public ElevatorView(Integer id, int floor) {
        super();
        this.id = id;
        this.floor = floor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
