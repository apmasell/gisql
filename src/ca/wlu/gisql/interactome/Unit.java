package ca.wlu.gisql.interactome;

import java.io.PrintStream;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;

public class Unit implements Interactome {

	public double calculateMembership(Gene gene) {
		return 0;
	}

	public double calculateMembership(Interaction interaction) {
		return 0;
	}

	public Interactome fork(Interactome substitute) {
		return this;
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public boolean needsFork() {
		return false;
	}

	public int numGenomes() {
		return 0;
	}

	public boolean postpare() {
		return true;
	}

	public boolean prepare() {
		return true;
	}

	public PrintStream show(PrintStream print) {
		print.print("∅");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("∅");
		return sb;
	}

}
