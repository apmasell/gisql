package ca.wlu.gisql.interactome.output;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;

public class OutputText extends AbstractOutput {

	private static final int STANDARD_BIN_COUNT = 10;

	private PrintStream print = null;

	private Statistics statistics = new Statistics(STANDARD_BIN_COUNT,
			lowerbound, upperbound);

	OutputText(Interactome source, double lowerbound, double upperbound,
			FileFormat format, String filename) {
		super(source, lowerbound, upperbound, format, filename);
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
		statistics.show(print);
		if (filename != null)
			print.close();
		return true;
	}

	public boolean prepare() {
		if (super.prepare()) {
			try {
				print = (filename == null ? System.out : new PrintStream(
						filename));
				print.print("# ");
				source.show(print);
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
