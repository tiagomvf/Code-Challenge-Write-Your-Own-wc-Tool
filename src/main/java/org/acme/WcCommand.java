package org.acme;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

    private void countBytes(File file){
        int byteCount = 0;
        int charCount = 0;
        int lineCount = 0;
        int wordCount = 0;
        try (Scanner scanner = new Scanner(file)) {
           while (scanner.hasNextLine()) {
               lineCount++;
               String currentLine = scanner.nextLine();
               // todo: fix do handle cases when new line string has size other then 2 (eg. Unix)
               byteCount += currentLine.getBytes().length + 2;
               // todo: fix do handle cases when new line string has size other then 2 (eg. Unix)
               charCount += currentLine.length() + 2;
               wordCount += getWordCount(currentLine);
           }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); // TODO: handle exception
        }

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
