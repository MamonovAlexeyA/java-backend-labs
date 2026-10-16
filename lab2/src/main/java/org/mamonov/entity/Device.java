package org.mamonov.entity;

public class Device {
    private Integer id;
    private String model;
    private Integer batteryLevel;

    public Device() {
    }

    // Для создания нового устройства до сохранения в БД (без id)
    public Device(String model, Integer batteryLevel) {
        this.model = model;
        this.batteryLevel = batteryLevel;
    }

    // Для чтения из базы (с уже известным id)
    public Device(Integer id, String model, Integer batteryLevel) {
        this.id = id;
        this.model = model;
        this.batteryLevel = batteryLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String toString() {
        return "Device{id=" + id + ", model='" + model + "', batteryLevel=" + batteryLevel + "}";
    }
}