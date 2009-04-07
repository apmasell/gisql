package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class BoldIntersection extends ArithmeticInteractome {

    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    BoldIntersection.class, 3, '⊗', new char[] { '*' },
	    "Bold Intersection (0 ∨ (Ax + Bx - 1))");

    public BoldIntersection(Interactome left, Interactome right) {
	super(left, right);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.max(0, gene.getMembership() + ortholog.getMembership() - 1);
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.max(0, interaction.getMembership()
		+ orthoaction.getMembership() - 1);
    }

    public char getSymbol() {
	return descriptor.getSymbol();
    }

    protected Gene processLoneGene(Gene gene, boolean left) {
	return new ComplementaryGene(gene);
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return new ComplementaryInteraction(interaction);
    }
}
