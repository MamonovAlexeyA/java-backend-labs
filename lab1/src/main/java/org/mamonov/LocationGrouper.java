package org.mamonov;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationGrouper {

    public void process(String inputFilePath, String outputFilePath) {
        Map<String, List<String>> groupedData = readAndGroup(inputFilePath);

        writeToFile(groupedData, outputFilePath);
    }

    private Map<String, List<String>> readAndGroup(String inputFilePath) {
        Map<String, List<String>> locationMap = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFilePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return locationMap;
            }

            int nameIndex = 1;
            int locationIndex = 7;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String name = parts[nameIndex].trim();
                String location = parts[locationIndex].trim();

                locationMap.computeIfAbsent(location, k -> new ArrayList<>()).add(name);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + inputFilePath + ": " + e.getMessage());
        }

        return locationMap;
    }

    private void writeToFile(Map<String, List<String>> map, String outputFilePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String line = entry.getKey() + " -> " + entry.getValue();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }
}