package ca.wlu.gisql.interactome;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * An interactome which will build a list of all genes and interactions which
 * have defined membership.
 */
public class CachedInteractome extends ProcessableInteractome {

	private static final Logger log = Logger.getLogger(CachedInteractome.class);

	public static CachedInteractome wrap(Interactome interactome, String name) {
		if (interactome == null) {
			return null;
		}

		if (interactome instanceof CachedInteractome) {
			CachedInteractome cachedInteractome = (CachedInteractome) interactome;
			if (name != null && cachedInteractome.name == null) {
				log.info("Renaming from " + cachedInteractome.name + " to "
						+ name);
				cachedInteractome.name = name;
			}
			return cachedInteractome;
		}

		return new CachedInteractome(interactome, name);
	}

	private ListOrderedSet<Gene> genes;

	private ListOrderedSet<Interaction> interactions;

	private String name;

	private final Interactome source;

	private CachedInteractome(Interactome source, String name) {
		super();
		this.source = source;
		this.name = name;
	}

	public double calculateMembership(Gene gene) {
		if (genes.contains(gene)) {
			return gene.getMembership(this);
		} else {
			double membership = source.calculateMembership(gene);
			if (!Membership.isMissing(membership)) {
				gene.setMembership(this, membership);
				genes.add(gene);
			}
			return membership;
		}
	}

	public double calculateMembership(Interaction interaction) {
		if (interactions.contains(interaction)) {
			return interaction.getMembership(this);
		} else {
			double membership = source.calculateMembership(interaction);
			if (!Membership.isMissing(membership)) {
				interaction.setMembership(this, membership);
				interactions.add(interaction);
				for (Gene gene : new Gene[] { interaction.getGene1(),
						interaction.getGene2() }) {
					if (Membership.isMissing(gene.getMembership(this))) {
						gene.setMembership(this, 0);
						genes.add(gene);
					}
				}
			}
			return membership;
		}
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		return source.collectAll(set);
	}

	public Construction getConstruction() {
		return source.getConstruction();
	}

	public final List<Gene> getGenes() {
		process();
		return genes.asList();
	}

	public final List<Interaction> getInteractions() {
		process();
		return interactions.asList();
	}

	public Precedence getPrecedence() {
		return source.getPrecedence();
	}

	public final double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	@Override
	public boolean postpare() {
		return super.postpare() && source.postpare();
	}

	public boolean prepare() {
		genes = new ListOrderedSet<Gene>();
		interactions = new ListOrderedSet<Interaction>();
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source);
	}
}
