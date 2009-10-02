package ca.wlu.gisql.interactome;

import java.util.Set;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;

public interface Interactome extends Prioritizable<Set<Interactome>>,
		Show<Set<Interactome>> {
	public enum Construction {
		Computed, Species
	}

	public abstract double calculateMembership(Gene gene);

	public abstract double calculateMembership(Interaction interaction);

	public abstract Set<Interactome> collectAll(Set<Interactome> set);

	public abstract Construction getConstruction();

	public abstract double membershipOfUnknown();

	public abstract boolean postpare();

	public abstract boolean prepare();
};
