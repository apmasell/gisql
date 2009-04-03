package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;

public class Difference extends ArithmeticInteractome {

    public Difference(Interactome left, Interactome right) {
	super(left, right);
	symbol = "∖";
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.min(gene.getMembership(), ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.min(interaction.getMembership(), 1 - orthoaction
		.getMembership());
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
