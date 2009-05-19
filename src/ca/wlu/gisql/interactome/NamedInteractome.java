package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;

public class NamedInteractome implements Interactome {

	private double membershipOfUnknown;

	protected String name;

	private int numGenomes;

	private Type type;

	public NamedInteractome(String name, int numGenomes,
			double membershipOfUnknown, Type type) {
		super();
		this.name = name;
		this.numGenomes = numGenomes;
		this.membershipOfUnknown = membershipOfUnknown;
		this.type = type;
	}

	public final double calculateMembership(Gene gene) {
		return gene.getMembership(this);
	}

	public final double calculateMembership(Interaction interaction) {
		return interaction.getMembership(this);
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
