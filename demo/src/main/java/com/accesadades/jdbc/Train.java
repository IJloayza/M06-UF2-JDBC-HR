package com.accesadades.jdbc;

public class Train {

    private int id; // EMPLOYEE_ID
    private String name; // FIRST_NAME
    private int capacity;

    // Constructor completo
    public Train(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    // Getters y Setters
    public int getTrainId() {
        return id;
    }

    public void setEmployeeId(int trainId) {
        this.id = trainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // toString para depuración y visualización
    @Override
    public String toString() {
        return "Tren{" +
                "id=" + id +
                ", Name='" + name + '\'' +
                ", Capaciyt='" + capacity +
                '}';
    }
}