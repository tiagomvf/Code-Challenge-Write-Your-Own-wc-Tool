package org.acme.control;

import org.acme.entity.Counted;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public interface Counter {
    String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";
    String LINE_PATTERN = ".*("+LINE_SEPARATOR_PATTERN+")|.+$";

    static Counted count(String label, InputStream stream) {
        return new Counted(label, count(stream));
    }

     private static int[] count(InputStream scanner) {
         return new Scanner(scanner)
             .findAll(Pattern.compile(LINE_PATTERN))
             .map(MatchResult::group)
             .map(s -> new int[]{getWordCount(s), s.length(), s.getBytes().length})
             .reduce(
                 new int[]{0,0,0,0},
                 (x, y) -> new int[]{
                     x[0] + 1,    // lineCount
                     x[1] + y[0], // wordCount
                     x[2] + y[1], // charCount
                     x[3] + y[2], // byteCount
                 }
             );
    }

    private static int getWordCount(String currentLine) {
        long count = 0L;
        for (String s : currentLine.split("\\s+")) {
            if (!s.isBlank()) {
                count++;
            }
        }
        return (int) count;
    }

    static Counted getTotals(String label, Counted[] list) {
        int[] sumArr = Arrays.stream(list)
            .map(x -> new int[]{x.lineCount(), x.wordCount(), x.charCount(), x.byteCount()})
            .reduce(
                new int[4],
                (x, y) -> new int[]{x[0]+y[0], x[1]+y[1], x[2]+y[2], x[3]+y[3]}
            );
        return new Counted(label, sumArr[0], sumArr[1], sumArr[2], sumArr[3]);
    }

}
