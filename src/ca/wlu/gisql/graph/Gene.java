package ca.wlu.gisql.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.db.DbSpecies;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;
import ca.wlu.gisql.util.Mergeable;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Gene implements Iterable<Accession>, Mergeable<Set<Interactome>>,
		Show<Set<Interactome>> {

	private static final Logger log = Logger.getLogger(Gene.class);

	private boolean dead = false;

	final Map<Gene, Interaction> edges = new HashMap<Gene, Interaction>();

	private final Set<Accession> ids = new HashSet<Accession>();

	private final Map<Interactome, Double> memberships = new WeakHashMap<Interactome, Double>();

	void add(Accession accession) {
		for (Accession existingaccession : this) {
			if (existingaccession.getSpecies() == accession.getSpecies())
				throw new IllegalArgumentException("Duplicate species in gene.");
		}
		if (!ids.add(accession))
			throw new RuntimeException();
	}

	public boolean canMerge(Mergeable<Set<Interactome>> other) {
		if (other instanceof Gene) {
			Gene gene = (Gene) other;
			Set<DbSpecies> knownSpecies = new HashSet<DbSpecies>();

			/* Determine if we can merge these genes. */
			for (Accession accession : this) {
				knownSpecies.add(accession.getSpecies());
			}
			for (Accession accession : gene) {
				if (knownSpecies.contains(accession.getSpecies())) {
					return false;
				}
			}
			return true;

		}
		return false;
	}

	protected final void checkState() {
		if (dead) {
			throw new IllegalStateException("Gene is dead.");
		}
	}

	void copyMembership(Gene gene) {
		for (Entry<Interactome, Double> entry : gene.memberships.entrySet()) {
			Double membership = memberships.get(entry.getKey());
			if (membership == null || GisQL.isMissing(membership)) {
				membership = entry.getValue();
			} else if (entry.getKey().getType() == Type.Computed) {
				membership = entry.getKey().calculateMembership(gene);
			} else {
				ShowableStringBuilder<Set<Interactome>> print = new ShowableStringBuilder<Set<Interactome>>(
						null);
				print.print("There is a duplicated value for the interactome ");
				print.print(entry.getKey());
				print.print(" where gene ");
				print.print(this);
				print.print(" has value ");
				print.print(membership);
				print.print(" and gene ");
				print.print(gene);
				print.print(" has value ");
				print.print(entry.getValue());
				log.warn(print);
				membership = Math.max(entry.getValue(), membership);
			}
			memberships.put(entry.getKey(), membership);
		}
		gene.memberships.clear();
	}

	protected final void dispose() {
		checkState();
		dead = true;
	}

	public Collection<Interaction> getInteractions() {
		return edges.values();
	}

	protected Interaction getInteractionWith(Gene gene) {
		return edges.get(gene);
	}

	public double getMembership(Interactome interactome) {
		Double value = memberships.get(interactome);
		if (value == null)
			return GisQL.Undefined;
		else
			return value;
	}

	public Iterator<Accession> iterator() {
		return ids.iterator();
	}

	public void setMembership(Interactome interactome, double membership) {
		if (GisQL.isUndefined(membership))
			membership = GisQL.Missing;
		memberships.put(interactome, membership);

	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		boolean first = true;
		if (dead)
			print.print("DEAD:");
		print.print("{");
		for (Accession accession : this) {
			if (print.getContext() == null
					|| print.getContext().contains(accession.getSpecies())) {
				if (!first)
					print.print(", ");
				print.print(accession);
				first = false;
			}
		}
		print.print("}");
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
