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

	private final static Ubergraph self = new Ubergraph();

	public static Ubergraph getInstance() {
		return self;
	}

	private final MultiMap<Long, Gene> geneByGi = new MultiHashMap<Long, Gene>();
	private final Set<Gene> genes = new HashSet<Gene>();

	private final Set<Interaction> interactions = new HashSet<Interaction>();

	private final boolean safeMode = false;

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

	@SuppressWarnings("unchecked")
	public Collection<Gene> findGenes(long identifier) {
		Collection<Gene> matches = geneByGi.get(identifier);
		return (matches == null ? (Collection<Gene>) Collections.EMPTY_SET
				: matches);
	}

	public Iterable<Gene> genes() {
		return genes;
	}

	public Iterator<Interaction> iterator() {
		return interactions.iterator();
	}

	public boolean merge(Gene gene, Gene victim) {
		if (!gene.canMerge(victim))
			return false;

		/* Merge the genes. */
		gene.copyMembership(victim);

		for (Accession accession : victim) {
			geneByGi.remove(accession.getIdentifier(), victim);
			addOrtholog(gene, accession);
		}

		/* Merge any interactions. */
		for (Interaction interaction : new ArrayList<Interaction>(victim
				.getInteractions())) {
			Gene other = interaction.getOther(victim);

			Interaction duplicate = other.getInteractionWith(gene);

			if (duplicate == null) {
				interaction.replace(victim, gene);
			} else {
				/* This edge has been duplicated by merging. */
				duplicate.copyMembership(interaction);
				interactions.remove(interaction);
				other.edges.remove(victim);
			}
		}
		genes.remove(victim);
		victim.dispose();
		return true;
	}

	public Gene newGene(Accession accession) {
		Gene gene = (safeMode ? new CheckedGene() : new Gene());
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
