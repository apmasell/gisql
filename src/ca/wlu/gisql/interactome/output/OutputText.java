package ca.wlu.gisql.interactome.output;

import java.io.FileNotFoundException;
import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.metrics.Fuzziness;
import ca.wlu.gisql.interactome.metrics.Histogram;
import ca.wlu.gisql.interactome.metrics.Metrics;
import ca.wlu.gisql.interactome.metrics.MetricsInteractome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class OutputText extends AbstractOutput {

	private ShowablePrintWriter<Set<Interactome>> print = null;

	OutputText(Interactome source, String name, FileFormat format,
			String filename) {
		super(new MetricsInteractome(source, new Metrics[] { new Fuzziness(),
				new Histogram() }), name, format, filename);
	}

	public double calculateMembership(Gene gene) {
		double membership = gene.getMembership(this);
		if (Membership.isUndefined(membership)) {
			membership = source.calculateMembership(gene);

			if (!Membership.isMissing(membership)) {

				if (format == FileFormat.genetbl) {
					print.print(gene);
					print.print("; ");
					print.print(membership);
					print.println();
				}
			}
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interaction.getMembership(this);
		if (Membership.isUndefined(membership)) {
			membership = source.calculateMembership(interaction);
			interaction.setMembership(this, membership);

			if (!Membership.isMissing(membership)) {
				if (format == FileFormat.interactiontbl) {
					print.print(interaction.getGene1());
					print.print("; ");
					print.print(interaction.getGene2());
					print.print("; ");
					print.print(membership);
					print.println();
				}
			}
		}
		return membership;
	}

	@Override
	public boolean postpare() {
		if (super.postpare() && source.postpare()) {
			for (Metrics metric : (MetricsInteractome) source) {
				print.print(metric);
			}
			print.close();
			return true;
		} else {
			return false;
		}
	}

	public boolean prepare() {
		if (source.prepare()) {
			try {
				Set<Interactome> interactomes = Membership.collectAll(this);
				print = new ShowablePrintWriter<Set<Interactome>>(filename,
						true, interactomes);
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
