package ca.wlu.gisql.interactome;

import java.util.Set;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;

/**
 * Interactomes are groups of interactions and genes. Each interaction and gene
 * has a particular membership in an interactome. All {@link Interaction} and
 * {@link Gene} objects come from the {@link Ubergraph} and an interactome
 * simply calculates the membership.
 */
public interface Interactome extends Prioritizable<Set<Interactome>>,
		Show<Set<Interactome>> {
	public enum Construction {
		Computed, Species
	}

	/**
	 * Calculate the membership of a gene. A caller must call {@link #prepare()}
	 * before visiting any genes. Moreover, all genes <b>must</b> be visited
	 * before any interactions are visited, but a caller may calculate a gene
	 * membership again after interaction calculations have begun. This method
	 * may be invoked multiple times on the same gene.
	 */
	public abstract double calculateMembership(Gene gene);

	/**
	 * Calculate the membership of a gene. A caller must call {@link #prepare()}
	 * and visit all genes before visiting any interactions. This method may be
	 * invoked multiple times on the same interaction.
	 */
	public abstract double calculateMembership(Interaction interaction);

	/**
	 * Collect all interactome nodes represented by this object. Effectively,
	 * add yourself to the set and call on your children.
	 */
	public abstract Set<Interactome> collectAll(Set<Interactome> set);

	/**
	 * Return whether this interactome is a leaf (a species) or some kind of
	 * computation.
	 */
	public abstract Construction getConstruction();

	/**
	 * The value of a gene or interaction that does not belong to this
	 * interactome. That is, the membership of a value outside the universe of
	 * discourse. Usually, zero.
	 */
	public abstract double membershipOfUnknown();

	/**
	 * Always called after {@link #prepare()}, gene visiting, and edge visiting.
	 * The interactome may perform any clean up it requires. If there is an
	 * error, return false, otherwise true. It must invoke this on its children.
	 * If the children return false, return false.
	 */
	public abstract boolean postpare();

	/**
	 * Always called before gene visiting and edge visiting. The interactome may
	 * perform any set up it requires. If there is an error, return false and
	 * the processing must be aborted, otherwise true. It must invoke this on
	 * its children. If the children return false, return false.
	 */
	public abstract boolean prepare();
};
