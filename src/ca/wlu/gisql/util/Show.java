package ca.wlu.gisql.util;

import java.io.PrintStream;

public interface Show {
    public PrintStream show(PrintStream print);

    public StringBuilder show(StringBuilder sb);
}
