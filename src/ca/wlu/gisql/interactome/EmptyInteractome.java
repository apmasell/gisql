package ca.wlu.gisql.interactome;

import java.util.Set;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** An interactome which contains no genes or edges. */
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

	public Precedence getPrecedence() {
		return Precedence.Value;
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
