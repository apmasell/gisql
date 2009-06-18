package ca.wlu.gisql.interactome;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class NamedInteractome implements Interactome {

	private final double membershipOfUnknown;

	protected final String name;

	private final Type type;

	public NamedInteractome(String name, double membershipOfUnknown, Type type,
			boolean zeroInteractionsWithOrthologs) {
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

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	public final Type getType() {
		return type;
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

	public final void show(ShowablePrintWriter print) {
		print.print(name);
	}

	public final String toString() {
		return name;
	}
}
