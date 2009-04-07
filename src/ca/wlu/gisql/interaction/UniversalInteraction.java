package ca.wlu.gisql.interaction;

import java.io.PrintStream;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;

public class UniversalInteraction implements Interaction {

    private Gene gene1;

    private Gene gene2;

    private Interactome parent;

    public UniversalInteraction(Interactome parent, Gene gene1, Gene gene2) {
	super();
	this.parent = parent;
	this.gene1 = gene1;
	this.gene2 = gene2;
    }

    public Gene getGene1() {
	return gene1;
    }

    public Gene getGene2() {
	return gene2;
    }

    public double getMembership() {
	return 1;
    }

    public Interactome getParent() {
	return parent;
    }

    public PrintStream show(PrintStream print) {
	print.print("1/∃");
	gene1.show(print);
	print.print(" ⇌ ");
	gene2.show(print);
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("1/∃ ");
	gene1.show(sb);
	sb.append(" ⇌ ");
	gene2.show(sb);
	return sb;
    }

}
