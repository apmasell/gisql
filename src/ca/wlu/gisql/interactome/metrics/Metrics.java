package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface Metrics extends Show<Set<Interactome>> {
	public void countGene(double membership);

	public void countInteraction(double membership);
}
