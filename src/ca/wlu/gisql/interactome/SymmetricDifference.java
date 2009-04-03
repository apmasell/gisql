package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;

public class SymmetricDifference extends ArithmeticInteractome {

    public SymmetricDifference(Interactome left, Interactome right) {
	super(left, right);
	symbol = "∆";
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

    protected Gene processLoneGene(Gene gene, boolean left) {
	return gene;
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return interaction;
    }

    private double symmetricMembership(double m1, double m2) {
	return Math.min(Math.min(m1, m2), 1 - Math.max(m1, m2));
    }
}
