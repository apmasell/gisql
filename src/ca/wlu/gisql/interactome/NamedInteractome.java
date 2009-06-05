package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;

public class NamedInteractome implements Interactome {

	private final double membershipOfUnknown;

	protected final String name;

	private final int numGenomes;

	private final Type type;

	private final boolean zeroInteractionsWithOrthologs;

	public NamedInteractome(String name, int numGenomes,
			double membershipOfUnknown, Type type,
			boolean zeroInteractionsWithOrthologs) {
		super();
		this.name = name;
		this.numGenomes = numGenomes;
		this.membershipOfUnknown = membershipOfUnknown;
		this.type = type;
		this.zeroInteractionsWithOrthologs = zeroInteractionsWithOrthologs;
	}

	public final double calculateMembership(Gene gene) {
		return gene.getMembership(this);
	}

	public final double calculateMembership(Interaction interaction) {
		double membership = interaction.getMembership(this);
		if (zeroInteractionsWithOrthologs && GisQL.isMissing(membership)
				&& !GisQL.isMissing(interaction.getGene1().getMembership(this))
				&& !GisQL.isMissing(interaction.getGene2().getMembership(this))) {
			return 0;

		}
		return membership;
	}

	public Interactome fork(Interactome substitute) {
		return this;
	}

	public int getPrecedence() {
		return Integer.MAX_VALUE;
	}

	public final Type getType() {
		return type;
	}

	public final double membershipOfUnknown() {
		return membershipOfUnknown;
	}

	public boolean needsFork() {
		return false;
	}

	public final int numGenomes() {
		return numGenomes;
	}

	public boolean postpare() {
		return true;
	}

	public boolean prepare() {
		return true;
	}

	public final PrintStream show(PrintStream print) {
		print.print(name);
		return print;
	}

	public final StringBuilder show(StringBuilder sb) {
		sb.append(name);
		return sb;
	}

	public final String toString() {
		return name;
	}
}
