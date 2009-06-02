package ca.wlu.gisql.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class CliqueMerger<V extends Mergeable> {
	public interface Master<V> {

		boolean merge(V gene, V victim);
	}

	private final List<V> items;

	private final Master<V> master;

	public CliqueMerger(Set<V> nodes, Master<V> master) {
		items = new ArrayList<V>(nodes);
		this.master = master;
	}

	public void merge() {

		while (true) {
			BronKerboschCliqueFinder<V, DefaultEdge> cliques = new BronKerboschCliqueFinder<V, DefaultEdge>(
					prepareGraph());
			for (Set<V> mergeable : cliques.getBiggestMaximalCliques()) {
				if (mergeable.size() < 2)
					return;

				Iterator<V> iterator = mergeable.iterator();
				V item = iterator.next();
				while (iterator.hasNext()) {
					V victim = iterator.next();
					if (!master.merge(item, victim))
						throw new RuntimeException(
								"Merging failed unexpectedly.");
					items.remove(victim);
				}
				break;
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
