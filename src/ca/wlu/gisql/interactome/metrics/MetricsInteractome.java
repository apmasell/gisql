package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

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

	public Metrics[] getMetrics() {
		return metrics;
	}

	public int getPrecedence() {
		return source.getPrecedence();
	}

	public Type getType() {
		return source.getType();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source);
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
