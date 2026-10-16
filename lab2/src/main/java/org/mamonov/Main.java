package org.mamonov;

import org.mamonov.entity.Device;
import org.mamonov.repository.DeviceRepository;
import org.mamonov.repository.impl.DeviceRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("--- Старт приложения ---");

        DeviceRepository repository = new DeviceRepositoryImpl();

        // 1. Создаем таблицу (если ее нет)
        repository.createTableIfNotExists();

        // 2. Проверка save (Create)
        Device phone = new Device("iPhone 15", 85);
        int phoneId = repository.save(phone);

        Device tablet = new Device("iPad Air", 50);
        int tabletId = repository.save(tablet);

        // 3. Проверка findById (Read)
        Device foundById = repository.findById(phoneId);
        log.info("Результат findById({}): {}", phoneId, foundById);

        // 4. Проверка findByModel (Read по второму полю)
        Device foundByModel = repository.findByModel("iPad Air");
        log.info("Результат findByModel('iPad Air'): {}", foundByModel);

        // 5. Проверка findAll (Read)
        List<Device> all = repository.findAll();
        log.info("Все устройства в БД: {}", all);

        // 6. Проверка update (Update)
        if (foundById != null) {
            foundById.setBatteryLevel(100);
            foundById.setModel("iPhone 15 Pro");
            boolean updated = repository.update(foundById);
            log.info("Обновлено: {}, новое состояние: {}", updated, repository.findById(phoneId));
        }

        // 7. Проверка deleteById (Delete)
        repository.deleteById(tabletId);
        log.info("После удаления iPad Air: {}", repository.findById(tabletId));

        // Идемпотентность delete: повторный вызов не падает с ошибкой
        repository.deleteById(tabletId);
        log.info("Повторное удаление успешно (идемпотентно)");

        log.info("--- Все операции успешно выполнены ---");
    }
}