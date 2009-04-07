package ca.wlu.gisql.interactome;

import ca.wlu.gisql.gene.ComplementaryGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.ComplementaryInteraction;
import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.interaction.UniversalInteraction;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Residuum extends ArithmeticInteractome {
    public final static ParseableBinaryOperation descriptor = new ParseableBinaryOperation(
	    Residuum.class, 2, '⇒', new char[] { '>' },
	    "Residuum (1 ∧ (1 - Ax + Bx))");

    public Residuum(Interactome left, Interactome right) {
	super(left, right);
	unknownGeneMembership = 1;
    }

    protected double calculateGeneMembership(Gene gene, Gene ortholog) {
	return Math.min(1, 1 - gene.getMembership() + ortholog.getMembership());
    }

    protected double calculateMembership(Interaction interaction,
	    Interaction orthoaction) {
	return Math.min(1, 1 - interaction.getMembership()
		+ orthoaction.getMembership());
    }

    protected Interaction getEmptyInteraction(Gene gene1, Gene gene2) {
	return new UniversalInteraction(this, gene1, gene2);
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
