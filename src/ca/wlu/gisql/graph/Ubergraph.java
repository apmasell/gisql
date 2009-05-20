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

	private static Ubergraph self = new Ubergraph();

	public static Ubergraph getInstance() {
		return self;
	}

	private MultiMap<Long, Gene> genes = new MultiHashMap<Long, Gene>();

	private Set<Interaction> interactions = new HashSet<Interaction>();

	private Ubergraph() {
		super();
	}

	public Gene addOrtholog(Gene gene, Accession accession) {
		if (genes.containsValue(gene)) {
			gene.add(accession);
			genes.put(accession.getIdentifier(), gene);
			return gene;
		} else {
			throw new IllegalArgumentException("Where did this gene come from?");
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Gene> findGenes(long identifier) {
		Collection<Gene> matches = genes.get(identifier);
		return (matches == null ? (Collection<Gene>) Collections.EMPTY_SET
				: matches);
	}

	public Iterable<Gene> genes() {
		return genes.values();
	}

	public Iterator<Interaction> iterator() {
		return interactions.iterator();
	}

	public boolean merge(Gene gene1, Gene gene2) {
		Set<Integer> knownSpecies = new HashSet<Integer>();

		/* Determine if we can merge these genes. */
		for (Accession accession : gene1) {
			knownSpecies.add(accession.getSpecies());
		}
		for (Accession accession : gene2) {
			if (knownSpecies.contains(accession.getSpecies())) {
				return false;
			}
		}

		/* Merge the genes. */
		for (Accession accession : gene2) {
			genes.remove(accession.getIdentifier(), gene2);

			addOrtholog(gene1, accession);
			gene1.copyMembership(gene2);
		}

		/* Merge any interactions. */
		for (Interaction interaction : gene2.getInteractions()) {
			Gene other = interaction.getOther(gene2);

			Interaction duplicate = other.getInteractionWith(gene1);

			if (duplicate == null) {
				interactions.remove(interaction);
				interaction.replace(gene2, gene1);
			} else {
				/* This edge has been duplicated by merging. */
				duplicate.copyMembership(interaction);
			}

		}
		return true;
	}

	public Gene newGene(Accession accession) {
		Gene gene = new Gene();
		gene.add(accession);
		genes.put(accession.getIdentifier(), gene);
		return gene;
	}

	public Collection<Interaction> upsertInteraction(long identifier1,
			long identifier2) {
		Collection<Interaction> results = new ArrayList<Interaction>();

		for (Gene gene1 : genes.get(identifier1)) {
			for (Gene gene2 : genes.get(identifier2)) {
				Interaction interaction = gene1.getInteractionWith(gene2);
				if (interaction == null) {
					interaction = new Interaction(gene1, gene2);
					interactions.add(interaction);
				}
				results.add(interaction);
			}
		}

		return results;
	}

}
