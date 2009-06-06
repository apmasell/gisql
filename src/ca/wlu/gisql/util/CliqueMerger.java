package ca.wlu.gisql.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class CliqueMerger<V extends Mergeable> {
	public interface Master<V> {

		boolean merge(V gene, V victim);
	}

	private static final Logger log = Logger.getLogger(CliqueMerger.class);

	private final List<V> items;

	private final Master<V> master;

	public CliqueMerger(Set<V> nodes, Master<V> master) {
		items = new ArrayList<V>(nodes);
		this.master = master;
	}

	public void merge() {
		SimpleGraph<V, DefaultEdge> compatibility = prepareGraph();

		BronKerboschCliqueFinder<V, DefaultEdge> cliques = new BronKerboschCliqueFinder<V, DefaultEdge>(
				compatibility);

		while (compatibility.edgeSet().size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Finding cliques on a graph where |E| = ");
			sb.append(compatibility.edgeSet().size());
			sb.append(" and V = ");
			for (V vertex : compatibility.vertexSet())
				vertex.show(sb).append(" ");

			log.info(sb);

			Set<V> mergeable = cliques.getBiggestMaximalCliques().iterator()
					.next();

			if (mergeable.size() < 2)
				return;

			V item = null;
			for (V victim : mergeable) {
				compatibility.removeVertex(victim);
				items.remove(victim);
				if (item == null) {
					item = victim;
				} else {
					if (!master.merge(item, victim)) {
						throw new RuntimeException(
								"Merging failed unexpectedly.");
					}

				}
			}
		}
	}

	private SimpleGraph<V, DefaultEdge> prepareGraph() {
		SimpleGraph<V, DefaultEdge> compatibility = new SimpleGraph<V, DefaultEdge>(
				DefaultEdge.class);

		for (V item : items)
			compatibility.addVertex(item);
		for (int i = 0; i < items.size(); i++) {
			for (int j = i + 1; j < items.size(); j++) {
				if (items.get(i).canMerge(items.get(j))) {
					compatibility.addEdge(items.get(i), items.get(j));
				}
			}
		}
		return compatibility;
	}
}
