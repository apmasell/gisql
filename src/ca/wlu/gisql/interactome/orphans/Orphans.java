package ca.wlu.gisql.interactome.orphans;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Orphans implements Interactome {
	public static final Parseable descriptor = new OrphansDescriptor();

	private final Interactome source;

	public Orphans(Interactome source) {
		this.source = source;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (GisQL.isPresent(membership)) {
			for (Interaction interaction : gene.getInteractions()) {
				if (GisQL.isPresent(source.calculateMembership(interaction)))
					return GisQL.Missing;
			}
			return membership;
		}
		return GisQL.Missing;
	}

	public double calculateMembership(Interaction interaction) {
		return source.calculateMembership(interaction);
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
		print.print(" : orphans");
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
