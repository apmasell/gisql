package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * An interactome for which we compute user-specified statistics (
 * {@link Metrics}).
 */
public class MetricsInteractome extends ProcessableInteractome {

	private final Metrics[] metrics;
	private final Interactome source;

	public MetricsInteractome(Interactome source, Metrics metric) {
		this(source, new Metrics[] { metric });
	}

	public MetricsInteractome(Interactome source, Metrics[] metrics) {
		super();
		this.source = source;
		this.metrics = metrics;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		for (Metrics metric : getMetrics()) {
			metric.countGene(membership);
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		for (Metrics metric : getMetrics()) {
			metric.countInteraction(membership);
		}
		return membership;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public Construction getConstruction() {
		return source.getConstruction();
	}

	public Metrics[] getMetrics() {
		return metrics;
	}

	public int getPrecedence() {
		return source.getPrecedence();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	@Override
	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source);
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}
}
