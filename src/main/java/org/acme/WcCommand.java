package org.acme;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Command(name = "wc", mixinStandardHelpOptions = true)
public class WcCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--bytes"}, arity = "0", description = "print the byte counts")
    boolean printByteCount;
    @CommandLine.Option(names = {"-m", "--chars"}, arity = "0", description = "print the character counts")
    boolean printCharCount;
    @CommandLine.Option(names = {"-l", "--lines"}, arity = "0", description = "print the newline counts")
    boolean printLineCount;
    @CommandLine.Option(names = {"-w", "--words"}, arity = "0", description = "print the word counts")
    boolean printWordCount;

    @CommandLine.Parameters()
    File[] files;

    @Override
    public void run() {
        if(!(printByteCount || printCharCount || printWordCount || printLineCount)) {
            printLineCount = printWordCount = printByteCount = true;
        }
        for (File file : files == null ? new File[0] : files) {
            countBytes(file);
        }
    }

    private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";
    private static final String LINE_PATTERN = ".*("+LINE_SEPARATOR_PATTERN+")|.+$";

    private void countBytes(File file){
        int byteCount = 0, charCount = 0, lineCount = 0 , wordCount = 0;
        try (Scanner scanner = new Scanner(file)) {
            var list =
                scanner
                    .findAll(Pattern.compile(LINE_PATTERN))
                    .map(MatchResult::group)
                    .toList();
            for(String currentLine: list){
               lineCount++;
               byteCount += currentLine.getBytes().length;
               charCount += currentLine.length();
               wordCount += getWordCount(currentLine);
           }
           printResult(file, lineCount, wordCount, charCount, byteCount);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); // TODO: handle exception
        }

    }

    private void printResult(File file, int lineCount, int wordCount, int charCount, int byteCount) {
        String result =
            (printLineCount ? lineCount + " " : "") +
            (printWordCount ? wordCount + " " : "") +
            (printCharCount ? charCount + " " : "") +
            (printByteCount ? byteCount + " " : "") +
            file.getName();
        System.out.println(result);
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
}
