package ca.wlu.gisql.gene;

import java.io.PrintStream;

import ca.wlu.gisql.interactome.Interactome;

public class DbGene implements Gene {

    private long identifier;

    private String name;

    private Interactome parent;

    public DbGene(Interactome parent, long identifier, String name) {
	this.parent = parent;
	this.identifier = identifier;
	this.name = name;
    }

    public long getId() {
	return identifier;
    }

    public double getMembership() {
	return 1;
    }

    public String getName() {
	return name;
    }

    public int getNumberOfOrthologies() {
	return 1;
    }

    public PrintStream show(PrintStream print) {
	print.print(identifier);
	print.print(" ");
	parent.show(print);
	print.print(" [");
	print.print(name);
	print.print("]");
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append(identifier).append(" ");
	parent.show(sb).append(" [").append(name).append("]");
	return sb;
    }

    public String toString() {
	return show(new StringBuilder()).toString();
    }

    public int countOrthologs(Interactome right) {
	return right.countOrthologs(this);
    }
}
