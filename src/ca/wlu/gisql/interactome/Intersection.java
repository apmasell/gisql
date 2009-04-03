package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;

public class Intersection extends ArithmeticInteractome {

    public Intersection(Interactome left, Interactome right) {
	super(left, right);
	symbol = "\u2229";
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.min(interaction.getMembership(), orthoaction
		.getMembership());
    }

    protected Interaction processLoneInteraction(Interaction interaction,
	    boolean left) {
	return new ComplementaryInteraction(interaction);
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.min(gene.getMembership(), ortholog.getMembership());
    }

    protected Gene processLoneGene(Gene interaction, boolean left) {
	return new ComplementaryGene(interaction);
    }

}
