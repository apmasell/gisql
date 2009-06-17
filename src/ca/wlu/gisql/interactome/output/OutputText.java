package ca.wlu.gisql.interactome.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class OutputText extends AbstractOutput {

	private static final int STANDARD_BIN_COUNT = 10;

	private ShowablePrintWriter print = null;

	private Statistics statistics = null;

	OutputText(Interactome source, String name, double lowerbound,
			double upperbound, FileFormat format, String filename) {
		super(source, name, lowerbound, upperbound, format, filename);
	}

	public double calculateMembership(Gene gene) {
		double membership = super.calculateMembership(gene);
		if (membership >= lowerbound && membership <= upperbound) {
			statistics.countGene(membership);

			if (format == FileFormat.genome) {
				gene.show(print);
				print.print("; ");
				print.print(membership);
				print.println();
			}
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = super.calculateMembership(interaction);

		if (membership >= lowerbound && membership <= upperbound) {
			statistics.countInteraction(membership);
			if (format == FileFormat.interactome) {
				interaction.getGene1().show(print);
				print.print("; ");
				interaction.getGene2().show(print);
				print.print("; ");
				print.print(membership);
				print.println();
			}
		}
		return membership;
	}

	public boolean postpare() {
		if (super.postpare()) {
			statistics.show(print);
			if (filename != null)
				print.close();
			return true;
		} else {
			return false;
		}
	}

	public boolean prepare() {
		if (super.prepare()) {
			statistics = new Statistics(STANDARD_BIN_COUNT, lowerbound,
					upperbound);
			try {
				print = (filename == null ? new ShowablePrintWriter(System.out)
						: new ShowablePrintWriter(new FileOutputStream(
								filename, true)));
				print.print("# ");
				print.print(source);
				print.println();
				return true;
			} catch (FileNotFoundException e) {
				log.error("Cannot open output file.", e);
				return false;
			}
		} else {
			return false;
		}
	}

}
