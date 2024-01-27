package org.acme.control;

import org.acme.entity.Counted;

import java.io.PrintStream;

public interface Printer {
    static void printResult(Counted[] counted, PrintStream out, boolean printLineCount, boolean printWordCount,
                            boolean printCharCount, boolean printByteCount){

        int numberLength = ("" + counted[counted.length-1].byteCount()).length() ;
        for (Counted w: counted) {
            String result =
                (printLineCount ? String.format("%"+numberLength+"d ", w.lineCount()) : "") +
                (printWordCount ? String.format("%"+numberLength+"d ", w.wordCount()) : "") +
                (printCharCount ? String.format("%"+numberLength+"d ", w.charCount()) : "") +
                (printByteCount ? String.format("%"+numberLength+"d ", w.byteCount()) : "") +
                w.label();
            out.println(result);
        }
    }
}
