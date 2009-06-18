package ca.wlu.gisql.interactome.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class OutputText extends AbstractOutput {

	private static final int STANDARD_BIN_COUNT = 10;

	private ShowablePrintWriter print = null;

	private Statistics statistics = null;

	OutputText(Interactome source, String name, FileFormat format,
			String filename) {
		super(source, name, format, filename);
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (!GisQL.isMissing(membership)) {
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
		double membership = source.calculateMembership(interaction);

		if (!GisQL.isMissing(membership)) {
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
		if (super.postpare() && source.postpare()) {
			statistics.show(print);
			if (filename != null)
				print.close();
			return true;
		} else {
			return false;
		}
	}

	public boolean prepare() {
		if (source.prepare()) {
			statistics = new Statistics(STANDARD_BIN_COUNT);
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
