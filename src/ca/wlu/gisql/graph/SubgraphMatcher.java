package ca.wlu.gisql.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;

/**
 * Find all subgraphs isomorphic to some supplied graph using a modified version
 * of the VF algorithm (Cordella, <i>et. al.</i>)
 */
public class SubgraphMatcher {

	private final CachedInteractome interactome;
	private final List<SortedSet<Integer>> neighbours = new ArrayList<SortedSet<Integer>>();
	private final List<SortedSet<Integer>> unneighbours = new ArrayList<SortedSet<Integer>>();

	/**
	 * Creates a matcher over an interactome.
	 * 
	 * @param size
	 *            The number of nodes in the subgraph.
	 */
	public SubgraphMatcher(Interactome interactome, int size) {
		this.interactome = CachedInteractome.wrap(interactome, null);
		for (int count = 0; count < size; count++) {
			neighbours.add(new TreeSet<Integer>());
			unneighbours.add(new TreeSet<Integer>());
		}
	}

	/**
	 * Ensures two nodes are connected in the matched subgraph. The algorithm
	 * does not support arbitrary node choice, so the order of nodes is
	 * important. First, the subgraph <b>must</b> be connected or no matches
	 * will be found. Second, in the subgraph, node <i>nₓ</i> must be connected
	 * to nodes <i>n</i>₀ to <i>nₓ</i>₋₁ ∀ <i>x</i>.
	 */
	public void connect(int node1, int node2) {
		connect(node1, node2, neighbours);
	}

	private void connect(int node1, int node2,
			List<SortedSet<Integer>> neighbours) {
		if (node1 >= 0 && node2 >= 0 && node1 < neighbours.size()
				&& node2 < neighbours.size() && node1 != node2) {
			if (node1 > node2) {
				neighbours.get(node1).add(node2);
			} else {
				neighbours.get(node2).add(node1);
			}
		}
	}

	/**
	 * Ensures two nodes are <b>not</b> connected in the matched subgraph.
	 * 
	 * @see SubgraphMatcher#connect(int, int)
	 */
	public void disconnect(int node1, int node2) {
		connect(node1, node2, unneighbours);
	}

	private boolean isFeasible(Gene[] genes, int coverage, Gene gene) {
		for (int neighbour : neighbours.get(coverage)) {
			Interaction interaction = genes[neighbour].getInteractionWith(gene);
			if (interaction == null
					|| !Membership.isPresent(interactome
							.calculateMembership(interaction))) {
				return false;
			}
		}
		for (int unneighbour : unneighbours.get(coverage)) {
			Interaction interaction = genes[unneighbour]
					.getInteractionWith(gene);
			if (interaction != null
					&& Membership.isPresent(interactome
							.calculateMembership(interaction))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Find all possible subgraphs.
	 * 
	 * @param function
	 *            When a match is found, this function is called with the nodes,
	 *            in order, as arguments. The result, from this function is
	 *            discarded.
	 */
	public void match(GenericFunction function) {
		if (interactome.process()) {
			match(function, new Gene[neighbours.size()], 0);
		}

	}

	private void match(GenericFunction function, Gene[] genes, int coverage) {
		if (coverage == genes.length) {
			function.run((Object[]) genes);
		} else {
			for (Gene candidate : pairs(genes, coverage)) {
				if (isFeasible(genes, coverage, candidate)) {
					genes[coverage] = candidate;
					match(function, genes, coverage + 1);
				}
			}
		}
	}

	private Iterable<Gene> pairs(Gene[] genes, int coverage) {
		if (coverage == 0) {
			return interactome.getGenes();
		}
		Set<Gene> candidates = new HashSet<Gene>();
		Set<Gene> known = new HashSet<Gene>();
		for (int index = 0; index < coverage; index++) {
			known.add(genes[index]);
		}

		for (int index = 0; index < coverage; index++) {
			for (Interaction interaction : genes[index].getInteractions()) {
				Gene other = interaction.getOther(genes[index]);
				if (!known.contains(other)
						&& Membership.isPresent(interactome
								.calculateMembership(interaction))
						&& Membership.isPresent(interactome
								.calculateMembership(other))) {
					candidates.add(other);
				}
			}
		}
		return candidates;
	}
}
