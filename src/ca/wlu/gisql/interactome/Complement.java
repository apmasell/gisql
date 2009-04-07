package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.interaction.UniversalInteraction;
import ca.wlu.gisql.util.Parseable;

public class Complement extends AbstractInteractome {

    public final static Parseable descriptor = new Parseable() {

	public Interactome construct(Environment environment, List<Object> params) {
	    Interactome interactome = (Interactome) params.get(0);
	    return new Complement(interactome);
	}

	public int getNestingLevel() {
	    return 5;
	}

	public boolean isMatchingOperator(char c) {
	    return c == '!' || c == '¬';
	}

	public boolean isPrefixed() {
	    return true;
	}

	public PrintStream show(PrintStream print) {
	    print.print("Complement (1-Ax)\t¬A, !A");
	    return print;
	}

	public StringBuilder show(StringBuilder sb) {
	    sb.append("Complement (1-Ax)\t¬A, !A");
	    return sb;
	}

	public NextTask[] tasks() {
	    return new NextTask[] { NextTask.SubExpression };
	}

    };

    Interactome interactome;

    public Complement(Interactome i) {
	unknownGeneMembership = 1;
	this.interactome = i;
    }

    public Gene findOrtholog(Gene gene) {
	return interactome.findOrtholog(gene);
    }

    protected Interaction getEmptyInteraction(Gene gene1, Gene gene2) {
	return new UniversalInteraction(this, gene1, gene2);
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
