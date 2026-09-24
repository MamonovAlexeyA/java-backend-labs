package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CRUD {

    public static void main(String[] args) {
        String filePath = "src/main/resources/characters.csv";
        CharacterRepository repo = new CharacterRepository(filePath);

        try {
            String newCharacter = "21,Super Rick,Alive,Human,,Earth,Earth,url,2026-09-24T12:00:00.000Z";
            repo.addCharacter(newCharacter);

            String updatedCharacter = "21,Super Rick,Dead,Human,,Earth,Earth,url,2026-09-24T12:00:00.000Z";
            repo.updateCharacter("1", updatedCharacter);

            repo.deleteCharacter("2");
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        }
    }
}

class CharacterRepository {
    private final String filePath;

    public CharacterRepository(String filePath) {
        this.filePath = filePath;
    }

    public void addCharacter(String csvLine) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.newLine();
            bw.write(csvLine);
        }
    }

    public void updateCharacter(String id, String newCsvLine) throws IOException {
        List<String> lines = readAllLines();
        boolean isUpdated = false;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(id + ",")) {
                lines.set(i, newCsvLine);
                isUpdated = true;
                break;
            }
        }

        if (isUpdated) {
            writeAllLines(lines);
        } else {
            System.out.println("Персонаж с ID " + id + " не найден для обновления.");
        }
    }

    public void deleteCharacter(String id) throws IOException {
        List<String> lines = readAllLines();

        boolean isRemoved = lines.removeIf(line -> line.startsWith(id + ","));

        if (isRemoved) {
            writeAllLines(lines);
        } else {
            System.out.println("Персонаж с ID " + id + " не найден для удаления.");
        }
    }

    private List<String> readAllLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private void writeAllLines(List<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < lines.size(); i++) {
                bw.write(lines.get(i));
                if (i < lines.size() - 1) {
                    bw.newLine();
                }
            }
        }
    }
}