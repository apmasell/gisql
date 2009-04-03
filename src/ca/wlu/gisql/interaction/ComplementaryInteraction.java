package ca.wlu.gisql.interaction;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;

public class ComplementaryInteraction implements Interaction {

    private Interaction interaction;

    public ComplementaryInteraction(Interaction interaction) {
	this.interaction = interaction;
    }

    public Gene getGene1() {
	return interaction.getGene1();
    }

    public Gene getGene2() {
	return interaction.getGene2();
    }

    public double getMembership() {
	return 1 - interaction.getMembership();
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append("¬(");
	interaction.show(sb);
	sb.append(")");
	return sb;
    }

    public Interactome getParent() {
	return interaction.getParent();
    }

}
