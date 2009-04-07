package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Intersection extends ArithmeticInteractome {
    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    Intersection.class, 3, '∩', new char[] { '&' },
	    "Intersection (Ax ∧ Bx)");

    public Intersection(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.min(gene.getMembership(), ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.min(interaction.getMembership(), orthoaction
		.getMembership());
    }

    public char getSymbol() {
	return descriptor.getSymbol();
    }

    protected Gene processLoneGene(Gene interaction, boolean left) {
	return new ComplementaryGene(interaction);
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return new ComplementaryInteraction(interaction);
    }

}
