package ca.wlu.gisql.interactome;

import java.util.Iterator;

import org.apache.log4j.Logger;

import ca.wlu.gisql.gene.CompositeGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.CompositeInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.interaction.TranslatedInteraction;

public abstract class ArithmeticInteractome extends AbstractInteractome {
    static final Logger log = Logger.getLogger(ArithmeticInteractome.class);

    protected Interactome left, right;

    protected String symbol = "?";

    public ArithmeticInteractome(Interactome left, Interactome right) {
	this.left = left;
	this.right = right;
    }

    protected abstract double calculateGeneMembership(Gene gene, Gene ortholog);

    protected abstract double calculateMembership(Interaction interaction,
	    Interaction orthoaction);

    public Gene findOrtholog(Gene gene) {
	/*
         * We seek to put orthlogs in the reference point of interactome #1 where possible. If there exists an orthlog in that genome, use it preferentially.
         */
	Gene ortholog = left.findOrtholog(gene);
	return (ortholog != null ? ortholog : right.findOrtholog(gene));
    }

    public int numGenomes() {
	return left.numGenomes() + left.numGenomes();
    }

    protected void prepareInteractions() {
	Iterator<Interaction> itLeftInteraction = left.iterator();
	Iterator<Interaction> itRightInteraction = right.iterator();

	log.info("Computing left genes");
	for (Gene gene : left.genes()) {
	    Gene ortholog = right.findOrtholog(gene);
	    if (ortholog == null) {
		addGene(processLoneGene(gene, true));
	    } else {
		double membership = calculateGeneMembership(gene, ortholog);
		addGene(new CompositeGene(gene, ortholog, membership));
	    }
	}
	log.info("Computing right genes");
	for (Gene gene : right.genes()) {
	    if (left.findOrtholog(gene) == null) {
		addGene(processLoneGene(gene, false));
	    }
	}

	log.info("Computing left interactions");

	while (itLeftInteraction.hasNext()) {
	    Interaction interaction = itLeftInteraction.next();

	    Interaction orthoaction = right.getInteraction(right
		    .findOrtholog(interaction.getGene1()), right
		    .findOrtholog(interaction.getGene2()));
	    if (orthoaction == null) {
		addInteraction(processLoneInteraction(interaction, true));
	    } else {
		double membership = calculateMembership(interaction,
			orthoaction);
		Interaction i = new CompositeInteraction(this, interaction,
			orthoaction, membership);
		addInteraction(i);

	    }
	}
	log.info("Computing right interactions");
	while (itRightInteraction.hasNext()) {
	    Interaction interaction = itRightInteraction.next();
	    Gene ortholog1 = right.findOrtholog(interaction.getGene1());
	    Gene ortholog2 = right.findOrtholog(interaction.getGene2());
	    if (ortholog1 != null) {
		ortholog1 = interaction.getGene1();
	    }
	    if (ortholog2 != null) {
		ortholog2 = interaction.getGene2();
	    }
	    if (left.getInteraction(ortholog1, ortholog2) == null) {
		interaction = new TranslatedInteraction(left, interaction,
			ortholog1, ortholog2);
		addInteraction(processLoneInteraction(interaction, false));
	    }
	}
	log.info("Set operation complete");
    }

    protected abstract Gene processLoneGene(Gene gene, boolean left);

    protected abstract Interaction processLoneInteraction(
	    Interaction interaction, boolean left);

    public StringBuilder show(StringBuilder sb) {
	sb.append("(");
	left.show(sb);
	sb.append(" ").append(symbol).append(" ");
	right.show(sb);
	sb.append(")");
	return sb;
    }
}