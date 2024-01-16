package org.acme;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileNotFoundException;
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
            wc(file);
        }
    }

    private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";
    private static final String LINE_PATTERN = ".*("+LINE_SEPARATOR_PATTERN+")|.+$";

    private void wc(File file){
        try (Scanner scanner = new Scanner(file)) {
            var list =
                scanner
                    .findAll(Pattern.compile(LINE_PATTERN))
                    .map(MatchResult::group)
                    .map(s -> new int[]{s.getBytes().length, s.length(), getWordCount(s)})
                    .reduce(
                        new int[]{0,0,0,0},
                        (x, y) -> new int[]{
                            x[0] + 1,    // lineCount
                            x[1] + y[0], // byteCount
                            x[2] + y[1], // charCount
                            x[3] + y[2], // wordCount
                        }
                    );
            printResult(file.getPath(), list[0], list[1], list[2], list[3]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); // TODO: handle exception
        }

    }

    private void printResult(String file, int lineCount, int byteCount, int charCount, int wordCount) {
        String result =
            (printLineCount ? lineCount + " " : "") +
            (printWordCount ? wordCount + " " : "") +
            (printCharCount ? charCount + " " : "") +
            (printByteCount ? byteCount + " " : "") +
            file;
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
