package ca.wlu.gisql.interactome;

import static ca.wlu.gisql.parser.Parser.PREC_LITERAL;

import java.util.Set;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** An interactome that contains all genes and interactions known to the system. */
public class CompleteInteractome implements Interactome {
	public static final CompleteInteractome self = new CompleteInteractome();

	private CompleteInteractome() {
		super();
	}

	public double calculateMembership(Gene gene) {
		return 1;
	}

	public double calculateMembership(Interaction interaction) {
		return 1;
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
		return 1;
	}

	public boolean postpare() {
		return true;
	}

	public boolean prepare() {
		return true;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("universe");
	}
}
