package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class BoundedDifference extends ArithmeticInteractome {
    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    BoundedDifference.class, 1, '⊝', new char[] { '~' },
	    "Bounded Difference (0 ∨ (Ax - Bx))");

    public BoundedDifference(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.max(0, gene.getMembership() - ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.max(0, interaction.getMembership()
		- orthoaction.getMembership());
    }

    public char getSymbol() {
	return descriptor.getSymbol();
    }

    protected Gene processLoneGene(Gene gene, boolean left) {
	return (left ? gene : null);
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return (left ? interaction : null);
    }
}
