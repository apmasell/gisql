package ca.wlu.gisql.graph;

import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

/**
 * Implementors of this class represent some kind of biological activity (e.g.,
 * GO terms).
 */
public interface BiologicalFunction extends Show<Set<Interactome>> {

	/**
	 * Compare this biological function against another and return a score of
	 * similarity ∈ [0,1].
	 */
	public double compare(BiologicalFunction other);

}
