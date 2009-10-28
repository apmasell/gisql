package ca.wlu.gisql.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

public class Ubergraph implements Iterable<Interaction> {

	private static Set<BiologicalFunction> functions = new HashSet<BiologicalFunction>();
	private static final Ubergraph self = new Ubergraph();

	public static void add(BiologicalFunction function) {
		functions.add(function);
	}

	public static Set<BiologicalFunction> getAllFunctions() {
		return functions;
	}

	public static Ubergraph getInstance() {
		return self;
	}

	private final MultiMap<Long, Gene> geneByGi = new MultiHashMap<Long, Gene>();

	private final Set<Gene> genes = new HashSet<Gene>();

	private final Set<Interaction> interactions = new HashSet<Interaction>();

	private Ubergraph() {
		super();
	}

	public Gene addOrtholog(Gene gene, Accession accession) {
		if (genes.contains(gene)) {
			gene.add(accession);
			geneByGi.put(accession.getIdentifier(), gene);
			return gene;
		} else {
			throw new IllegalArgumentException("Where did this gene come from?");
		}
	}

	public Collection<Gene> findGenes(long identifier) {
		Collection<Gene> matches = geneByGi.get(identifier);
		if (matches == null) {
			return Collections.emptySet();
		} else {
			return matches;
		}
	}

	public Iterable<Gene> genes() {
		return genes;
	}

	public Iterator<Interaction> iterator() {
		return interactions.iterator();
	}

	public Gene newGene(Accession accession) {
		Gene gene = new Gene();
		gene.add(accession);
		genes.add(gene);
		geneByGi.put(accession.getIdentifier(), gene);
		return gene;
	}

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
