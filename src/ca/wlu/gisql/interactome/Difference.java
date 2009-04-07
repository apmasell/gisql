package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Difference extends ArithmeticInteractome {
    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    Difference.class, 1, '∖', new char[] { '-', '\\' },
	    "Difference (Ax ∨ 1-Bx)");

    public Difference(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.min(gene.getMembership(), 1 - ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.min(interaction.getMembership(), 1 - orthoaction
		.getMembership());
    }

    public char getSymbol() {
	return descriptor.getSymbol();
    }

    protected Gene processLoneGene(Gene gene, boolean left) {
	if (left)
	    return gene;
	else
	    return new ComplementaryGene(gene);
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	if (left)
	    return interaction;
	else
	    return new ComplementaryInteraction(interaction);
    }
}
