package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class SymmetricDifference extends ArithmeticInteractome {

    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    SymmetricDifference.class, 4, '∆', new char[] { '^' },
	    "Symmetric Difference ((Ax ∧ 1-Bx) ∨ (Bx ∧ 1-Ax))");

    public SymmetricDifference(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return symmetricMembership(gene.getMembership(), ortholog
		.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return symmetricMembership(interaction.getMembership(), orthoaction
		.getMembership());
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

    private double symmetricMembership(double m1, double m2) {
	return Math.max(Math.min(m1, 1 - m2), Math.min(m2, 1 - m1));
    }
}
