package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;

public class Complement extends AbstractInteractome {

    Interactome interactome;

    public Complement(Interactome i) {
	this.interactome = i;
    }

    public Gene findOrtholog(Gene gene) {
	return interactome.findOrtholog(gene);
    }

    public int numGenomes() {
	return interactome.numGenomes();
    }

    protected void prepareInteractions() {
	for (Gene gene : interactome.genes()) {
	    addGene(new ComplementaryGene(gene));
	}

	for (Interaction interaction : interactome) {
	    this.addInteraction(new ComplementaryInteraction(interaction));
	}
    }

    public PrintStream show(PrintStream print) {
	print.print("¬(");
	interactome.show(print);
	print.print(")");
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("¬(");
	interactome.show(sb);
	sb.append(")");
	return sb;
    }
}
