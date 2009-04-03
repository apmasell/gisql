package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;

public class Union extends ArithmeticInteractome {

    public Union(Interactome left, Interactome right) {
	super(left, right);
	symbol = "\u222A";
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.max(gene.getMembership(), ortholog.getMembership());
    }

    protected double calculateMembership(Interaction j1, Interaction j2) {
	return Math.max(j1.getMembership(), j2.getMembership());
    }

    protected Gene processLoneGene(Gene gene, boolean left) {
	return gene;
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return interaction;
    }

}
