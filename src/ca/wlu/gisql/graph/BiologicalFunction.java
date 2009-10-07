package ca.wlu.gisql.graph;

import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface BiologicalFunction extends Show<Set<Interactome>> {

	public double compare(BiologicalFunction other);

}
