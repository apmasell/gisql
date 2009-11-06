package ca.wlu.gisql.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 * Super-structure that holds all interactions and genes in the system. This
 * provides the only interface by which to create genes and interactions and,
 * therefore, enforces all constraints.
 */
public class Ubergraph implements Iterable<Interaction> {

	private static Set<BiologicalFunction> functions = new HashSet<BiologicalFunction>();
	private static final Ubergraph self = new Ubergraph();

	/** Register a biological function so that it can be iterated over later. */
	public static void add(BiologicalFunction function) {
		functions.add(function);
	}

	/** View all registered biological functions. */
	public static Set<BiologicalFunction> getAllFunctions() {
		return functions;
	}

	/** Return the singleton instance of the Ubergraph. */
	public static Ubergraph getInstance() {
		return self;
	}

	private final MultiMap<Long, Gene> geneByGi = new MultiHashMap<Long, Gene>();

	private final Set<Gene> genes = new HashSet<Gene>();

	private final Set<Interaction> interactions = new HashSet<Interaction>();

	private Ubergraph() {
		super();
	}

	/**
	 * Registers an accession as an “ortholog” of a gene. This effectively means
	 * that this “real” gene belongs to a particular gene group.
	 */
	public Gene addOrtholog(Gene gene, Accession accession) {
		if (genes.contains(gene)) {
			gene.add(accession);
			geneByGi.put(accession.getIdentifier(), gene);
			return gene;
		} else {
			throw new IllegalArgumentException("Where did this gene come from?");
		}
	}

	/** Find all genes to which a partiuclar gene identifier are attached. */
	public Collection<Gene> findGenes(long identifier) {
		Collection<Gene> matches = geneByGi.get(identifier);
		if (matches == null) {
			return Collections.emptySet();
		} else {
			return matches;
		}
	}

	/** Get all of the genes in the system. */
	public Iterable<Gene> genes() {
		return genes;
	}

	public Iterator<Interaction> iterator() {
		return interactions.iterator();
	}

	/** Create a new gene (group) with at least one “real” gene in it. */
	public Gene newGene(Accession accession) {
		Gene gene = new Gene();
		gene.add(accession);
		genes.add(gene);
		geneByGi.put(accession.getIdentifier(), gene);
		return gene;
	}

	/**
	 * Get the interaction between two genes, or create it if it doesn't yet
	 * exist.
	 */
	public Collection<Interaction> upsertInteraction(long identifier1,
			long identifier2) {
		Collection<Interaction> results = new ArrayList<Interaction>();

		for (Gene gene1 : geneByGi.get(identifier1)) {
			for (Gene gene2 : geneByGi.get(identifier2)) {
				if (gene1 != gene2) {
					Interaction interaction = gene1.getInteractionWith(gene2);
					if (interaction == null) {
						interaction = new Interaction(gene1, gene2);
						interactions.add(interaction);
					}
					results.add(interaction);
				}
			}
		}

		return results;
	}
}
