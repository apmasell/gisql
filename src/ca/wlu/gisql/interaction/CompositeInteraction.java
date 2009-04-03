package ca.wlu.gisql.interaction;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;

public class CompositeInteraction implements Interaction {
    private Interaction interaction;

    private Interaction orthoaction;

    double membership;

    private Interactome parent;

    public CompositeInteraction(Interactome parent, Interaction interaction,
	    Interaction orthoaction, double membership) {
	this.parent = parent;
	this.interaction = interaction;
	this.orthoaction = orthoaction;
	this.membership = membership;
    }

    public Gene getGene1() {
	return interaction.getGene1();
    }

    public Gene getGene2() {
	return interaction.getGene2();
    }

    public double getMembership() {
	return membership;
    }

    public Interactome getParent() {
	return parent;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("(");
	interaction.show(sb);
	sb.append(" ≈ ");
	orthoaction.show(sb);
	sb.append(") : ").append(membership);
	return sb;
    }

}
