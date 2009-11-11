package ca.wlu.gisql;

import java.util.HashSet;
import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Construction;

/**
 * Helper functions to manipulate membership values. Normally, memberships are ∈
 * [0,1], however, special negative values indicate that members are missing.
 * Missing memberships are simply unknown in the target, while undefined ones
 * have not yet been computed. In most cases, Missing is sufficient. Undefined
 * is used when traversing a structure multiple times to ensure the same node is
 * not considered twice (i.e., it is initially Undefined, but may become
 * Missing).
 */
public class Membership {

	public static final double Missing = -1;
	public static final double Undefined = -2;

	/** Create a set of all interactomes rooted at a particular interactome. */
	public static Set<Interactome> collectAll(Interactome root) {
		return root.collectAll(new HashSet<Interactome>());
	}

	/**
	 * Create a set of leaf (species) interactomes rooted at a particular
	 * interactome.
	 */
	public static Set<Interactome> collectSpecies(Interactome root) {
		Set<Interactome> all = root.collectAll(new HashSet<Interactome>());
		Set<Interactome> result = new HashSet<Interactome>();
		for (Interactome interactome : all) {
			if (interactome.getConstruction() == Construction.Species) {
				result.add(interactome);
			}
		}
		return result;
	}

	public static boolean isMissing(double membership) {
		return membership < 0;
	}

	/** A membership is missing or zero. */
	public static boolean isPresent(double membership) {
		return membership > 0;
	}

	/**
	 * Semantically, a missing thing is known to be not present in the
	 * interactome while an undefined one could be unprocessed.
	 */
	public static boolean isUndefined(double membership) {
		return membership < -1;
	}

}
