package ca.wlu.gisql.interactome.output;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;

class Statistics implements Show {
	private final double binwidth;

	private final int[] geneBins;

	private double geneFuziness = 0;

	private int genes = 0;

	private final int[] interactionBins;

	private double interactionFuzziness = 0;

	private int interactions = 0;

	private final int maxbin;

	private final double offset;

	protected Statistics(int bincount, double lowerbound, double upperbound) {
		super();
		maxbin = bincount - 1;
		binwidth = bincount / (upperbound - lowerbound);
		offset = binwidth * lowerbound;
		geneBins = new int[bincount];
		interactionBins = new int[bincount];
	}

	private int calculateBin(double membership) {
		return Math.min((int) (membership * binwidth - offset), maxbin);
	}

	protected void countGene(double membership) {
		if (GisQL.isMissing(membership))
			return;
		genes++;
		geneFuziness += 1 - Math.abs(2 * membership - 1);
		geneBins[calculateBin(membership)]++;
	}

	protected void countInteraction(double membership) {
		if (GisQL.isMissing(membership))
			return;
		interactions++;
		interactionFuzziness += 1 - Math.abs(2 * membership - 1);
		interactionBins[calculateBin(membership)]++;
	}

	public void show(ShowablePrintWriter print) {
		print.print("# ");
		print.print(genes);
		print.print(" genes in ");
		print.print(interactions);
		print.print(" interactions. Gene fuzziness: ");
		print.print(geneFuziness);
		print.print(" Interaction fuzziness: ");
		print.print(interactionFuzziness);
		print.print("\n# Gene histogram: ");
		for (int i : geneBins) {
			print.print(i);
			print.print(" ");
		}
		print.print("\n# Interaction histogram: ");
		for (int i : interactionBins) {
			print.print(i);
			print.print(" ");
		}
		print.println();
	}

}
