package org.acme.boundary;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import org.acme.control.Counter;
import org.acme.entity.Counted;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import static org.acme.control.Printer.printResult;

@TopCommand
@Command(name = "wc", mixinStandardHelpOptions = true, versionProvider = VersionProvider.class)
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
    String[] sources;

    private record Source(String label, InputStream inputStream){}

    @Override
    public void run() {
        boolean noPrintFlagsDefined = !(printByteCount || printCharCount || printWordCount || printLineCount);
        if(noPrintFlagsDefined) {
            printLineCount = printWordCount = printByteCount = true;
        }

        Counted[] results = count(sources);
        printResult(results.length > 1 ? addTotalsLine(results) : results,
            System.out, printLineCount, printWordCount, printCharCount, printByteCount);
    }

    private static Counted[] addTotalsLine(Counted[] original) {
        var new_results = Arrays.copyOf(original, original.length + 1);
        new_results[original.length] = Counter.getTotals("totals", original);
        return new_results;
    }

    private Counted[] count(String[] paths) {
        return Arrays
            .stream(paths == null ? new String[]{""} : paths)
            .map(WcCommand::getSource)
            .map(x -> Counter.count(x.label, x.inputStream))
            .toArray(Counted[]::new);
    }

    private static Source getSource(String source) {
        try {
            boolean isDefaultInput = source.isBlank() || source.equals("-");
            return new Source(
                isDefaultInput ? "-" : source,
                isDefaultInput ? System.in : new FileInputStream(source)
            );
        } catch (FileNotFoundException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
            return null;
        }
    }

}
