package org.mamonov;

public class Main {
    public static void main(String[] args) {
        String inputFile = "src/main/resources/characters.csv";

        String outputFile = "grouped_locations.txt";

        LocationGrouper grouper = new LocationGrouper();
        grouper.process(inputFile, outputFile);
    }
}