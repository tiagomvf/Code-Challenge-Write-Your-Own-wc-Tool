package org.acme.entity;

public record Counted(String label, int lineCount, int wordCount, int charCount, int byteCount) {
    public Counted(String label, int[] count) {
        this(label, count[0], count[1], count[2], count[3]);
    }
}
