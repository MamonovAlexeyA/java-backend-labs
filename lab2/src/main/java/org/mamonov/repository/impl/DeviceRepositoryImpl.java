package org.mamonov.repository.impl;

import org.mamonov.entity.Device;
import org.mamonov.exeption.DatabaseException;
import org.mamonov.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DeviceRepositoryImpl implements DeviceRepository {

    private static final Logger log = LoggerFactory.getLogger(DeviceRepositoryImpl.class);

    private final String url;
    private final String username;
    private final String password;

    private Device mapRowToDevice(ResultSet rs) throws SQLException {
        return new Device(
                rs.getInt("id"),
                rs.getString("model"),
                rs.getInt("battery_level")
        );
    }

    // Конструктор без static-блоков: читает application.properties
    public DeviceRepositoryImpl() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new IllegalStateException("Файл application.properties не найден!");
            }
            props.load(in);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
        } catch (Exception e) {
            throw new DatabaseException("Ошибка при чтении application.properties", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS device (
                    id SERIAL PRIMARY KEY,
                    model VARCHAR(255) NOT NULL,
                    battery_level INT NOT NULL
                );
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
            log.info("Таблица device готова к работе");
        } catch (SQLException e) {
            log.error("Ошибка при создании таблицы", e);
            throw new DatabaseException("Не удалось создать таблицу device", e);
        }
    }

    @Override
    public int save(Device device) {
        String sql = "INSERT INTO device (model, battery_level) VALUES (?, ?) RETURNING id;";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, device.getModel());
            ps.setInt(2, device.getBatteryLevel());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt("id");
                device.setId(generatedId);
                log.info("Сохранено устройство: {}", device);
                return generatedId;
            }
            throw new DatabaseException("Не удалось получить сгенерированный id");
        } catch (SQLException e) {
            log.error("Ошибка при сохранении устройства: {}", device, e);
            throw new DatabaseException("Ошибка сохранения устройства", e);
        }
    }

    @Override
    public Device findById(int id) {
        String sql = "SELECT id, model, battery_level FROM device WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToDevice(rs);
            }
            log.info("Устройство с id={} не найдено", id);
            return null;
        } catch (SQLException e) {
            log.error("Ошибка поиска по id: {}", id, e);
            throw new DatabaseException("Ошибка поиска по id", e);
        }
    }

    @Override
    public Device findByModel(String model) {
        String sql = "SELECT id, model, battery_level FROM device WHERE model = ? LIMIT 1;";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, model);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToDevice(rs);
            }
            log.info("Устройство с model='{}' не найдено", model);
            return null;
        } catch (SQLException e) {
            log.error("Ошибка поиска по модели: {}", model, e);
            throw new DatabaseException("Ошибка поиска по модели", e);
        }
    }

    @Override
    public List<Device> findAll() {
        String sql = "SELECT id, model, battery_level FROM device ORDER BY id;";
        List<Device> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Device(rs.getInt("id"), rs.getString("model"), rs.getInt("battery_level")));
            }
            log.info("Найдено устройств: {}", list.size());
            return list;
        } catch (SQLException e) {
            log.error("Ошибка при чтении всех устройств", e);
            throw new DatabaseException("Ошибка получения всех устройств", e);
        }
    }

    @Override
    public boolean update(Device device) {
        if (device.getId() == null) {
            return false;
        }

        String sql = "UPDATE device SET model = ?, battery_level = ? WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, device.getModel());
            ps.setInt(2, device.getBatteryLevel());
            ps.setInt(3, device.getId());

            int affectedRows = ps.executeUpdate();
            boolean success = affectedRows > 0;
            log.info("Обновление устройства id={}: результат={}", device.getId(), success);
            return success;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении устройства: {}", device, e);
            throw new DatabaseException("Ошибка обновления устройства", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM device WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            log.info("Удаление устройства id={} выполнено (идемпотентно)", id);
        } catch (SQLException e) {
            log.error("Ошибка при удалении устройства id: {}", id, e);
            throw new DatabaseException("Ошибка удаления устройства", e);
        }
    }
}