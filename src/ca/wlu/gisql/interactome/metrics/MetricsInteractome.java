package ca.wlu.gisql.interactome.metrics;

import java.util.Iterator;
import java.util.Set;

import name.masella.iterator.ArrayIterator;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * An interactome for which we compute user-specified statistics (
 * {@link Metrics}).
 */
public class MetricsInteractome extends ProcessableInteractome implements
		Iterable<Metrics> {

	private final Metrics[] metrics;
	private final Interactome source;

	public MetricsInteractome(Interactome source, Metrics metric) {
		this(source, new Metrics[] { metric });
	}

	public MetricsInteractome(Interactome source, Metrics[] metrics) {
		super();
		this.source = source;
		this.metrics = metrics.clone();
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		for (Metrics metric : this) {
			metric.countGene(membership);
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		for (Metrics metric : this) {
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

	public Precedence getPrecedence() {
		return source.getPrecedence();
	}

	@Override
	public Iterator<Metrics> iterator() {
		return new ArrayIterator<Metrics>(metrics);
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
}
