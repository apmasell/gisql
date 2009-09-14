package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Fuzziness implements Metrics {
	private double geneFuziness = 0;

	private double interactionFuzziness = 0;

	public Fuzziness() {
		super();
	}

	public void countGene(double membership) {
		if (GisQL.isPresent(membership))
			geneFuziness += 1 - Math.abs(2 * membership - 1);
	}

	public void countInteraction(double membership) {
		if (GisQL.isPresent(membership))
			interactionFuzziness += 1 - Math.abs(2 * membership - 1);
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("# Gene fuzziness: ");
		print.println(geneFuziness);
		print.print("# Interaction fuzziness: ");
		print.println(interactionFuzziness);
	}
}