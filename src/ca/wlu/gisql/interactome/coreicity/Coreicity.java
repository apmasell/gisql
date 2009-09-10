package ca.wlu.gisql.interactome.coreicity;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Coreicity implements Interactome {

	public static final Parseable descriptor = new CoreicityDescriptor();
	private final int threshold;
	private final Interactome source;
	private final NumericComparison comparison;

	public Coreicity(Interactome source, NumericComparison comparison,
			int threshold) {
		this.source = source;
		this.comparison = comparison;
		this.threshold = threshold;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (!GisQL.isMissing(membership)
				&& !comparison.compare(gene.getCoreicity(), threshold)) {
			membership = 0;
		}
		gene.setMembership(this, membership);
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!GisQL.isMissing(membership)
				&& (GisQL.isPresent(interaction.getGene1().getMembership(this)) || GisQL
						.isPresent(interaction.getGene2().getMembership(this)))) {
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
		print.print(" :core ");
		print.print(comparison);
		print.print(' ');
		print.print(threshold);
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}

}
