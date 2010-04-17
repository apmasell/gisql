package ca.wlu.gisql.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.db.DbSpecies;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Construction;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Represenation of a cluster of genes (under whatever definition of orthology
 * is supplied). Effectively, these are the “genes” under which we define
 * interactions despite not having a 1-to-1 correspondence with actual
 * {@link Accession}s, even though represent true genes.
 */
public class Gene implements Iterable<Accession>, Show<Set<Interactome>> {

	private static final Logger log = Logger.getLogger(Gene.class);

	private boolean dead = false;

	final Map<Gene, Interaction> edges = new HashMap<Gene, Interaction>();

	private final Map<Interactome, Map<BiologicalFunction, Double>> functions = new WeakHashMap<Interactome, Map<BiologicalFunction, Double>>();

	private final Set<Accession> ids = new HashSet<Accession>();

	private final Map<Interactome, Double> memberships = new WeakHashMap<Interactome, Double>();

	private final Set<DbSpecies> species = new HashSet<DbSpecies>();

	void add(Accession accession) {
		if (!ids.add(accession)) {
			throw new RuntimeException();
		}
		species.add(accession.getSpecies());
	}

	/**
	 * Associate a biological function with this gene. All functions exist with
	 * in a context, that is, the interactome that claims them.
	 */
	public void add(BiologicalFunction function, Interactome knower,
			double membership) {
		if (Membership.isUndefined(membership)) {
			membership = Membership.Missing;
		}
		Map<BiologicalFunction, Double> predictions = functions.get(knower);
		if (predictions == null) {
			predictions = new HashMap<BiologicalFunction, Double>();
			functions.put(knower, predictions);
		}
		predictions.put(function, membership);
	}

	/**
	 * Adds a collection of functions in the same manner as
	 * {@link #add(BiologicalFunction, Interactome, double)}.
	 */
	public void add(Iterable<BiologicalFunction> functions, Interactome knower,
			double membership) {
		for (BiologicalFunction function : functions) {
			add(function, knower, membership);
		}
	}

	protected final void checkState() {
		if (dead) {
			throw new IllegalStateException("Gene is dead.");
		}
	}

	void copyMembership(Gene gene) {
		for (Entry<Interactome, Double> entry : gene.memberships.entrySet()) {
			Double membership = memberships.get(entry.getKey());
			if (membership == null || Membership.isMissing(membership)) {
				membership = entry.getValue();
			} else if (entry.getKey().getConstruction() == Construction.Computed) {
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

	/**
	 * Determine the coreicity of this genes. The coreicity is defined by Gabo
	 * as the number of unique species represented by this gene.
	 */
	public int getCoreicity() {
		return species.size();
	}

	/**
	 * Inspect the biological function membership in this gene for a particular
	 * context.
	 */
	public Map<BiologicalFunction, Double> getFunctions(Interactome knower) {
		Map<BiologicalFunction, Double> predictions = functions.get(knower);
		if (predictions == null) {
			return Collections.emptyMap();
		}
		return predictions;
	}

	/** Find all the interactions in which this gene participates. */
	public Collection<Interaction> getInteractions() {
		return edges.values();
	}

	/**
	 * Find the interaction in which this gene and the supplied gene particpate.
	 * 
	 * @return The interaction in which both genes participate or null if the
	 *         genes do not interact.
	 */
	public Interaction getInteractionWith(Gene gene) {
		return edges.get(gene);
	}

	/**
	 * Determine the stored membership of this gene in a particular interactome.
	 * If the value has not been recorded, it will be
	 * {@link Membership#Undefined}.
	 */
	public double getMembership(Interactome interactome) {
		Double value = memberships.get(interactome);
		if (value == null) {
			return Membership.Undefined;
		} else {
			return value;
		}
	}

	/**
	 * Determine the membership of a biological function to this gene in some
	 * context.
	 */
	public double hasFunction(BiologicalFunction function, Interactome knower) {
		Map<BiologicalFunction, Double> predictions = functions.get(knower);
		if (predictions == null) {
			return Membership.Undefined;
		}
		return predictions.get(function);
	}

	public Iterator<Accession> iterator() {
		return ids.iterator();
	}

	/**
	 * Associate a membership value for the current gene in an interactome.
	 * There is no need to “delete” values as the memberships are stored as weak
	 * references and will be cleaned by the garbage collector.
	 */
	public void setMembership(Interactome interactome, double membership) {
		if (Membership.isUndefined(membership)) {
			membership = Membership.Missing;
		}
		memberships.put(interactome, membership);

	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		boolean first = true;
		if (dead) {
			print.print("DEAD:");
		}
		print.print("{");

		if (species.size() > 1) {
			print.print("coreicity:");
			print.print(species.size());
			first = false;
		}

		for (Accession accession : this) {
			if (print.getContext() == null
					|| print.getContext().contains(accession.getSpecies())) {
				if (!first) {
					print.print(", ");
				}
				print.print(accession);
				first = false;
			}
		}

		Map<BiologicalFunction, Double> relevantfunctions = new HashMap<BiologicalFunction, Double>();
		for (Entry<Interactome, Map<BiologicalFunction, Double>> entry : functions
				.entrySet()) {
			if (print.getContext() == null
					|| print.getContext().contains(entry.getKey())) {
				for (Entry<BiologicalFunction, Double> subentry : entry
						.getValue().entrySet()) {
					double old = 0;
					if (relevantfunctions.containsKey(subentry.getKey())) {
						old = relevantfunctions.get(subentry.getKey());
					}
					relevantfunctions.put(subentry.getKey(), Math.max(old,
							subentry.getValue()));
				}
			}
		}

		for (Entry<BiologicalFunction, Double> entry : relevantfunctions
				.entrySet()) {
			if (!first) {
				print.print(", ");
			}
			print.print(entry.getValue());
			print.print("/");
			print.print(entry.getKey());
		}

		print.print("}");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
