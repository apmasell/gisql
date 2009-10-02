package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Totals implements Metrics {

	private int genes = 0;

	private int interactions = 0;

	public void countGene(double membership) {
		if (Membership.isPresent(membership)) {
			genes++;
		}
	}

	public void countInteraction(double membership) {
		if (Membership.isPresent(membership)) {
			interactions++;
		}
	}

	public int getGeneCount() {
		return genes;
	}

	public int getInteractionCount() {
		return interactions;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("# Gene count: ");
		print.print(genes);
		print.println();
		print.print("# Interaction count: ");
		print.print(interactions);
		print.println();
	}

}
