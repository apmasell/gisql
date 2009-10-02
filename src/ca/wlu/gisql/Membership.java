package ca.wlu.gisql;

import java.util.HashSet;
import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Construction;

public class Membership {

	public static final double Missing = -1;
	public static final double Undefined = -2;

	public static Set<Interactome> collectAll(Interactome root) {
		return root.collectAll(new HashSet<Interactome>());
	}

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

	public static boolean isPresent(double membership) {
		return membership > 0;
	}

	/*
	 * Semantically, a missing thing is known to be not present in the
	 * interactome while an undefined one could be unprocessed.
	 */
	public static boolean isUndefined(double membership) {
		return membership < -1;
	}

}
