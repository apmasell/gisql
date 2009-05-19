package ca.wlu.gisql.interactome;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Show;

public interface Interactome extends Show {
	public enum Type {
		Species, Computed, Mutable
	}

	public abstract double calculateMembership(Interaction interaction);

	public abstract double calculateMembership(Gene gene);

	public abstract int numGenomes();

	public abstract double membershipOfUnknown();

	public abstract boolean prepare();

	public abstract boolean postpare();

	public abstract Type getType();
};
