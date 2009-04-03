package ca.wlu.gisql.interaction;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;

public class TranslatedInteraction implements Interaction {

    private Gene ortholog1;

    private Gene ortholog2;

    private Interactome destination;

    private Interaction source;

    public TranslatedInteraction(Interactome destination, Interaction source,
	    Gene ortholog1, Gene ortholog2) {
	this.destination = destination;
	this.source = source;
	this.ortholog1 = ortholog1;
	this.ortholog2 = ortholog2;
    }

    public Gene getGene1() {
	return ortholog1;
    }

    public Gene getGene2() {
	return ortholog2;
    }

    public double getMembership() {
	return source.getMembership();
    }

    public Interactome getParent() {
	return destination;
    }

    public StringBuilder show(StringBuilder sb) {
	source.show(sb).append(" → ");
	ortholog1.show(sb);
	sb.append(" ⇌ ");
	ortholog2.show(sb);
	sb.append("[");
	destination.show(sb).append("]");
	return sb;
    }
}
