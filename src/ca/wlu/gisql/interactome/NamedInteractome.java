package ca.wlu.gisql.interactome;

import java.util.Set;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * An interactome whose memberships are all defined (named) in the
 * {@link Ubergraph}.
 */
public class NamedInteractome implements Interactome {

	private final double membershipOfUnknown;

	protected final String name;

	private final Construction type;

	public NamedInteractome(String name, double membershipOfUnknown,
			Construction type, boolean zeroInteractionsWithOrthologs) {
		super();
		this.name = name;
		this.membershipOfUnknown = membershipOfUnknown;
		this.type = type;
	}

	public final double calculateMembership(Gene gene) {
		return gene.getMembership(this);
	}

	public final double calculateMembership(Interaction interaction) {
		return interaction.getMembership(this);
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return set;
	}

	public final Construction getConstruction() {
		return type;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	public final double membershipOfUnknown() {
		return membershipOfUnknown;
	}

	public boolean postpare() {
		return true;
	}

	public boolean prepare() {
		return true;
	}

	public final void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(name);
	}

	@Override
	public final String toString() {
		return name;
	}
}
