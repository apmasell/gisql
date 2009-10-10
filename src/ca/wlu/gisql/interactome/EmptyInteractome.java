package ca.wlu.gisql.interactome;

import static ca.wlu.gisql.parser.Parser.PREC_LITERAL;

import java.util.Set;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class EmptyInteractome implements Interactome {
	public static final EmptyInteractome self = new EmptyInteractome();

	private EmptyInteractome() {
		super();
	}

	public double calculateMembership(Gene gene) {
		return 0;
	}

	public double calculateMembership(Interaction interaction) {
		return 0;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		return set;
	}

	public Construction getConstruction() {
		return Construction.Species;
	}

	public int getPrecedence() {
		return PREC_LITERAL;
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public boolean postpare() {
		return true;
	}

	public boolean prepare() {
		return true;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("null");
	}
}
