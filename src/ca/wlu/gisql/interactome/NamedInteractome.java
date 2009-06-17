package ca.wlu.gisql.interactome;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;

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

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	public final Type getType() {
		return type;
	}

	public final double membershipOfUnknown() {
		return membershipOfUnknown;
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

	public final void show(ShowablePrintWriter print) {
		print.print(name);
	}

	public final String toString() {
		return name;
	}
}
