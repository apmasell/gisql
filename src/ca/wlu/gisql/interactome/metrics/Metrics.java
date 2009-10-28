package ca.wlu.gisql.interactome.metrics;

import java.util.Set;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

/**
 * Classes implementing this interface can be used to build up statistics over
 * an interactome. The life-cycle semantics of a metric are determined by the
 * caller. When {@link Show} is invoked, the metric should render itself as it
 * should appear on the command line or in a file.
 */
public interface Metrics extends Show<Set<Interactome>> {
	public void countGene(double membership);

	public void countInteraction(double membership);
}
