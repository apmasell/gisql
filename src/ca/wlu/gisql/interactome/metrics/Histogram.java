package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Histogram implements Metrics {
	private final int bincount;

	private final int[] geneBins;

	private final int[] interactionBins;

	public Histogram() {
		this(10);
	}

	public Histogram(int bincount) {
		super();
		this.bincount = bincount;
		geneBins = new int[bincount];
		interactionBins = new int[bincount];
	}

	private int calculateBin(double membership) {
		return Math.min((int) (membership * bincount), bincount - 1);
	}

	public void countGene(double membership) {
		if (!Membership.isMissing(membership)) {
			geneBins[calculateBin(membership)]++;
		}
	}

	public void countInteraction(double membership) {
		if (!Membership.isMissing(membership)) {
			interactionBins[calculateBin(membership)]++;
		}
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("# Gene histogram: ");
		for (int i : geneBins) {
			print.print(i);
			print.print(" ");
		}
		print.println();
		print.print("# Interaction histogram: ");
		for (int i : interactionBins) {
			print.print(i);
			print.print(" ");
		}
		print.println();
	}

}
