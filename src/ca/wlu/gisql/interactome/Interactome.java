package ca.wlu.gisql.interactome;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Show;

public interface Interactome extends Show {
	public enum Type {
		Computed, Mutable, Species
	}

	public abstract double calculateMembership(Gene gene);

	public abstract double calculateMembership(Interaction interaction);

	public abstract Type getType();

	public abstract double membershipOfUnknown();

	public abstract int numGenomes();

	public abstract boolean postpare();

	public abstract boolean prepare();
};
