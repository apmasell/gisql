package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Cardinality implements Metrics {

	private double geneCardinality = 0;

	private double interactionCardinality = 0;

	public void countGene(double membership) {
		if (GisQL.isPresent(membership))
			geneCardinality += membership;
	}

	public void countInteraction(double membership) {
		if (GisQL.isPresent(membership))
			interactionCardinality += membership;
	}

	public double getGeneSize() {
		return geneCardinality;
	}

	public double getInteractionSize() {
		return interactionCardinality;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("# Gene cardinality: ");
		print.println(geneCardinality);
		print.print("# Interaction cardinality: ");
		print.println(interactionCardinality);

	}

}
