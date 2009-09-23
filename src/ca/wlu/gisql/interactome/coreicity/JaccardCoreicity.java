package ca.wlu.gisql.interactome.coreicity;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class JaccardCoreicity implements Interactome {

	public static final Parseable descriptor = new JaccardCoreicityDescriptor();
	private final Interactome source;
	private final Set<Interactome> interactomes;

	public JaccardCoreicity(Interactome source) {
		this.source = source;
		this.interactomes = GisQL.collectSpecies(this);
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!GisQL.isMissing(membership)) {
			int intersection = 0;
			int union = 0;
			for (Interactome species : interactomes) {
				boolean has1 = GisQL.isPresent(interaction.getGene1()
						.getMembership(species));
				boolean has2 = GisQL.isPresent(interaction.getGene2()
						.getMembership(species));

				if (has1 && has2)
					intersection++;
				if (has1 || has2)
					union++;
			}
			if (union == 0)
				return GisQL.Missing;
			else
				return ((double) intersection) / union;
		}
		return GisQL.Missing;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, getPrecedence());
		print.print(" :jaccardcore");
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}

}
