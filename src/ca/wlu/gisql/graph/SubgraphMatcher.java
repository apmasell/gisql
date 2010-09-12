package ca.wlu.gisql.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;

/**
 * Find all subgraphs isomorphic to some supplied graph using a modified version
 * of the VF algorithm (Cordella, <i>et. al.</i>)
 */
public abstract class SubgraphMatcher<E> implements List<E> {

	private final String descriptor;
	private final List<SortedSet<Integer>> neighbours = new ArrayList<SortedSet<Integer>>();
	private final List<E> results = new ArrayList<E>();
	private final List<SortedSet<Integer>> unneighbours = new ArrayList<SortedSet<Integer>>();

	/**
	 * Creates a matcher over an interactome.
	 * 
	 * @param size
	 *            The number of nodes in the subgraph.
	 */
	public SubgraphMatcher(String descriptor, int size) {
		this.descriptor = descriptor;

		for (int count = 0; count < size; count++) {
			neighbours.add(new TreeSet<Integer>());
			unneighbours.add(new TreeSet<Integer>());
		}
	}

	public boolean add(E e) {
		return results.add(e);
	}

	public void add(int index, E element) {
		results.add(index, element);
	}

	public boolean addAll(Collection<? extends E> c) {
		return results.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		return results.addAll(index, c);
	}

	public void clear() {
		results.clear();
	}

	protected abstract E compute(Gene[] genes);

	/**
	 * Ensures two nodes are connected in the matched subgraph. The algorithm
	 * does not support arbitrary node choice, so the order of nodes is
	 * important. First, the subgraph <b>must</b> be connected or no matches
	 * will be found. Second, in the subgraph, node <i>nₓ</i> must be connected
	 * to nodes <i>n</i>₀ to <i>nₓ</i>₋₁ ∀ <i>x</i>.
	 */
	protected final void connect(int node1, int node2) {
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

	public boolean contains(Object o) {
		return results.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return results.containsAll(c);
	}

	/**
	 * Ensures two nodes are <b>not</b> connected in the matched subgraph.
	 * 
	 * @see SubgraphMatcher#connect(int, interactomeint)
	 */
	protected final void disconnect(int node1, int node2) {
		connect(node1, node2, unneighbours);
	}

	@Override
	public boolean equals(Object o) {
		return results.equals(o);
	}

	public E get(int index) {
		return results.get(index);
	}

	@Override
	public int hashCode() {
		return results.hashCode();
	}

	public int indexOf(Object o) {
		return results.indexOf(o);
	}

	public boolean isEmpty() {
		return results.isEmpty();
	}

	private boolean isFeasible(CachedInteractome interactome, Gene[] genes,
			int coverage, Gene gene) {
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

	protected abstract Boolean isValid(Gene[] genes);

	public Iterator<E> iterator() {
		return results.iterator();
	}

	public int lastIndexOf(Object o) {
		return results.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return results.listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return results.listIterator(index);
	}

	private void match(CachedInteractome interactome, Gene[] genes, int coverage) {
		if (coverage == genes.length) {
			if (isValid(genes)) {
				results.add(compute(genes));
			}
		} else {
			for (Gene candidate : pairs(interactome, genes, coverage)) {
				if (isFeasible(interactome, genes, coverage, candidate)) {
					genes[coverage] = candidate;
					match(interactome, genes, coverage + 1);
				}
			}
		}
	}

	/**
	 * Find all possible subgraphs.
	 * 
	 * @param interactome
	 *            The interactome to search through. Calling this multiple times
	 *            will erase the existing matches.
	 */
	public void match(Interactome interactome) {
		CachedInteractome cachedinteractome = CachedInteractome.wrap(
				interactome, null);
		if (cachedinteractome.process()) {
			results.clear();
			match(cachedinteractome, new Gene[neighbours.size()], 0);
		}
	}

	private Iterable<Gene> pairs(CachedInteractome interactome, Gene[] genes,
			int coverage) {
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

	public E remove(int index) {
		return results.remove(index);
	}

	public boolean remove(Object o) {
		return results.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return results.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return results.retainAll(c);
	}

	public E set(int index, E element) {
		return results.set(index, element);
	}

	public int size() {
		return results.size();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return results.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return results.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return results.toArray(a);
	}

	@Override
	public final String toString() {
		return descriptor;
	}
}
