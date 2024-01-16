package org.acme;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Command(name = "wc", mixinStandardHelpOptions = true)
public class WcCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--bytes"}, arity = "0", description = "print the byte counts")
    boolean printBytesCount;
    @CommandLine.Option(names = {"-l", "--lines"}, arity = "0", description = "print the newline counts")
    boolean printLinesCount;
    @CommandLine.Option(names = {"-w", "--words"}, arity = "0", description = "print the word counts")
    boolean printWordsCount;

    @CommandLine.Parameters()
    File[] files;

    @Override
    public void run() {
        if(!(printBytesCount || printWordsCount || printLinesCount)) {
            printLinesCount = printWordsCount = printBytesCount = true;
        }
        for (File file : files == null ? new File[0] : files) {
            countBytes(file);
        }
    }

    private void countBytes(File file){
        int byteCount = 0;
        int linesCount = 0;
        int wordCount = 0;
        try (Scanner scanner = new Scanner(file)) {
           while (scanner.hasNextLine()) {
               linesCount++;
               String currentLine = scanner.nextLine();
               byteCount+= currentLine.getBytes().length + 2;
               wordCount+= getWordCount(currentLine);
           }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); // TODO: handle exception
        }

        String result =
            (printLinesCount ? linesCount + " " : "") +
            (printWordsCount ? wordCount + " " : "") +
            (printBytesCount ? byteCount + " " : "") +
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
