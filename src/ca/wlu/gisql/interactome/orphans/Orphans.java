package ca.wlu.gisql.interactome.orphans;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Orphans implements Interactome {
	public static final Function function = new OrphansFunction();

	private final Interactome source;

	public Orphans(Interactome source) {
		this.source = source;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (Membership.isPresent(membership)) {
			for (Interaction interaction : gene.getInteractions()) {
				if (Membership.isPresent(source
						.calculateMembership(interaction))) {
					return Membership.Missing;
				}
			}
			return membership;
		}
		return Membership.Missing;
	}

	public double calculateMembership(Interaction interaction) {
		return source.calculateMembership(interaction);
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public Construction getConstruction() {
		return Construction.Computed;
	}

	public int getPrecedence() {
		return function.getPrecedence();
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

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}
}
