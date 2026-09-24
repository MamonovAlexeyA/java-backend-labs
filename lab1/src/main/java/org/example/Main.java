package org.example;

import java.io.*;
import java.time.Instant;
import java.util.PriorityQueue;

public class Main {

    static class CharacterInfo implements Comparable<CharacterInfo> {
        String rawCsvLine;
        Instant createdDate;

        public CharacterInfo(String rawCsvLine, Instant createdDate) {
            this.rawCsvLine = rawCsvLine;
            this.createdDate = createdDate;
        }

        @Override
        public int compareTo(CharacterInfo other) {
            return this.createdDate.compareTo(other.createdDate);
        }
    }

    public static void main(String[] args) {
        String inputFile = "src/main/resources/characters.csv";
        String outputFile = "top5_oldest_characters.csv";

        PriorityQueue<CharacterInfo> queue = new PriorityQueue<>();
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String header = br.readLine();

            String[] headers = header.split(",");
            int createdIndex = 8;

            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");

                if (columns.length > createdIndex) {
                    String dateStr = columns[createdIndex];
                    Instant created = Instant.parse(dateStr);
                    queue.add(new CharacterInfo(line, created));
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
                bw.write(header);
                bw.newLine();

                while (count < 5) {
                    CharacterInfo oldest = queue.poll();
                    bw.write(oldest.rawCsvLine);
                    bw.newLine();
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка при работе с файлами: " + e.getMessage());
        }
    }
}