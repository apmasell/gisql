package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Union extends ArithmeticInteractome {
    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    Union.class, 2, '∪', new char[] { '|' }, "Union (Ax ∨ Bx)");

    public char getSymbol() {
	return descriptor.getSymbol();
    }

    public Union(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.max(gene.getMembership(), ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.max(interaction.getMembership(), orthoaction
		.getMembership());
    }

    protected Gene processLoneGene(Gene gene, boolean left) {
	return gene;
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return interaction;
    }
}
