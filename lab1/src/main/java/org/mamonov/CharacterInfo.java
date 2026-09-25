package org.mamonov;

import java.time.Instant;

public class CharacterInfo implements Comparable<CharacterInfo> {
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