package org.mamonov.repository;

import org.mamonov.entity.Device;

import java.util.List;

public interface  DeviceRepository {

    // Создание таблицы при старте (требование задания п. 2)
    void createTableIfNotExists();

    // Create (возвращает id созданной записи)
    int save(Device device);

    // Read (возвращает null, если запись не найдена)
    Device findById(int id);

    // Read по второму параметру сущности (model)
    Device findByModel(String model);

    List<Device> findAll();

    // Update (возвращает false, если запись не найдена)
    boolean update(Device device);

    // Delete
    void deleteById(int id);
}