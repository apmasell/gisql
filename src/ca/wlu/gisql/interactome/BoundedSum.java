package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class BoundedSum extends ArithmeticInteractome {

    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    BoundedSum.class, 1, '+', null, "Bounded Sum (1 ∧ (Ax + Bx))");

    public BoundedSum(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.min(1, gene.getMembership() + ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.min(1, interaction.getMembership()
		+ orthoaction.getMembership());
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
