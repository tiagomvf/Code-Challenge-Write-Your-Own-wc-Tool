package org.acme;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
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
        Wc[] list =
            Arrays.stream((files == null ? new File[0] : files))
                .map(this::wc)
                .toArray(Wc[]::new);

        printResult(list);
    }

    private void printResult(Wc[] wc){
        Wc[] resultArray;
        if(wc.length > 1){
            resultArray = Arrays.copyOf(wc, wc.length + 1);
            resultArray[wc.length] = getTotals(wc);
        }else{
           resultArray = Arrays.copyOf(wc, wc.length);
        }

        int maxLineCount = 0;
        int maxWordCount = 0;
        int maxCharCount = 0;
        int maxByteCount = 0;
        for (Wc value : resultArray) {
            maxLineCount = (int) Math.max(maxLineCount, Math.log10(value.lineCount) + 1);
            maxWordCount = (int) Math.max(maxWordCount, Math.log10(value.wordCount) + 1);
            maxCharCount = (int) Math.max(maxCharCount, Math.log10(value.charCount) + 1);
            maxByteCount = (int) Math.max(maxByteCount, Math.log10(value.byteCount) + 1);
        }

        for (Wc w: resultArray) {
            String result =
                (printLineCount ? String.format("  %"+(maxLineCount)+"d", w.lineCount) : "") +
                (printWordCount ? String.format("\t%"+(maxWordCount)+"d", w.wordCount) : "") +
                (printCharCount ? String.format("\t%"+(maxCharCount)+"d", w.charCount) : "") +
                (printByteCount ? String.format("\t%"+(maxByteCount)+"d", w.byteCount) : "") +
                " " + w.file;
            System.out.println(result);
        }
    }

    private static Wc getTotals(Wc[] list) {
        return Arrays.stream(list).reduce(
            new Wc("total", 0, 0, 0, 0),
            (x, y) -> new Wc(
                x.file,
                x.lineCount + y.lineCount,
                x.wordCount + y.wordCount,
                x.charCount + y.charCount,
                x.byteCount + y.byteCount
            )
        );
    }


    private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r\u2028\u2029\u0085]";
    private static final String LINE_PATTERN = ".*("+LINE_SEPARATOR_PATTERN+")|.+$";

    private Wc wc(File file){
        try (Scanner scanner = new Scanner(file)) {
            var counting =
                scanner
                    .findAll(Pattern.compile(LINE_PATTERN))
                    .map(MatchResult::group)
                    .map(s -> new int[]{getWordCount(s), s.length(),s.getBytes().length})
                    .reduce(
                        new int[]{0,0,0,0},
                        (x, y) -> new int[]{
                            x[0] + 1,    // lineCount
                            x[1] + y[0], // wordCount
                            x[2] + y[1], // charCount
                            x[3] + y[2], // byteCount
                        }
                    );
            return new Wc(file.getPath(), counting[0], counting[1], counting[2], counting[3]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); // TODO: handle exception
        }

    }

    private void printResult(Wc wc) {
        String result =
            (printLineCount ? wc.lineCount + " " : "") +
                (printWordCount ? wc.wordCount + " " : "") +
                (printCharCount ? wc.charCount + " " : "") +
                (printByteCount ? wc.byteCount + " " : "") +
                wc.file;
        System.out.println(result);
    }

    private void printResult(String file, int lineCount, int wordCount, int charCount, int byteCount) {
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

    record Wc(String file, int lineCount, int wordCount, int charCount, int byteCount){ };
}
