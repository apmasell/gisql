package ca.wlu.gisql.interactome.coreicity;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class DeltaCoreicity implements Interactome {

	public static final Parseable descriptor = new DeltaCoreicityDescriptor();
	private final int delta;
	private final Interactome source;
	private final NumericComparison comparison;

	public DeltaCoreicity(Interactome source, NumericComparison comparison,
			int delta) {
		this.source = source;
		this.comparison = comparison;
		this.delta = delta;
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!GisQL.isMissing(membership)) {
			int value = Math.abs(interaction.getGene1().getCoreicity()
					- interaction.getGene2().getCoreicity());
			if (comparison.compare(value, delta))
				return membership;
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
		print.print(" :deltacore ");
		print.print(comparison);
		print.print(' ');
		print.print(delta);
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}

}
