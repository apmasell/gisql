package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class StrongSymmetricDifference extends ArithmeticInteractome {
    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    StrongSymmetricDifference.class, 4, '∇', new char[] { '%' },
	    "Strong Symmetric Difference (|Ax - Bx|)");

    public StrongSymmetricDifference(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.abs(gene.getMembership() - ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.abs(interaction.getMembership()
		- orthoaction.getMembership());
    }

    public char getSymbol() {
	return descriptor.getSymbol();
    }

    protected Gene processLoneGene(Gene gene, boolean left) {
	return gene;
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return interaction;
    }
}
