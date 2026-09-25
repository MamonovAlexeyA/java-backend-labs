package org.mamonov;

import java.io.*;
import java.time.Instant;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {
        String inputFile = "src/main/resources/characters.csv";
        String outputFile = "top5_oldest_characters.csv";
        Main(inputFile, outputFile);
        CRUD(inputFile);
    }

    private static void Main(String inputFile, String outputFile) {
        PriorityQueue<CharacterInfo> queue = new PriorityQueue<>();
        String header = null;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            header = br.readLine();
            if (header == null) {
                System.out.println("Файл пуст!");
                return;
            }

            String[] headers = header.split(",");
            int createdIndex = 8;

            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length > createdIndex) {
                    String dateStr = columns[createdIndex].trim();
                    Instant created = Instant.parse(dateStr);
                    queue.add(new CharacterInfo(line, created));
                }
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка при чтении файла: " + e.getMessage());
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write(header);
            bw.newLine();

            int count = 0;
            while (!queue.isEmpty() && count < 5) {
                CharacterInfo oldest = queue.poll();
                bw.write(oldest.rawCsvLine);
                bw.newLine();
                count++;
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка при записи файла: " + e.getMessage());
        }
    }

    private static void CRUD(String inputFile) {
        CharacterRepository repo = new CharacterRepository(inputFile);

        try {
            String newCharacter = "21,Super Rick,Alive,Human,,Earth,Earth,url,2026-09-24T12:00:00.000Z";
            repo.addCharacter(newCharacter);

            String updatedCharacter = "21,Super Rick,Dead,Human,,Earth,Earth,url,2026-09-24T12:00:00.000Z";
            repo.updateCharacter("21", updatedCharacter);

            repo.deleteCharacter("2");

        } catch (IOException e) {
            System.err.println("Ошибка при выполнении CRUD операций: " + e.getMessage());
        }
    }
}