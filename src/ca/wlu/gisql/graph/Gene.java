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

	private final Map<BiologicalFunction, Double> knownFunctions = new HashMap<BiologicalFunction, Double>();

	private final Map<Interactome, Double> memberships = new WeakHashMap<Interactome, Double>();

	private final Map<Interactome, Map<BiologicalFunction, Double>> predictedFunctions = new WeakHashMap<Interactome, Map<BiologicalFunction, Double>>();

	private final Set<DbSpecies> species = new HashSet<DbSpecies>();

	void add(Accession accession) {
		if (!ids.add(accession)) {
			throw new RuntimeException();
		}
		species.add(accession.getSpecies());
	}

	public void add(BiologicalFunction function, Interactome knower,
			double membership) {
		if (Membership.isUndefined(membership)) {
			membership = Membership.Missing;
		}
		if (knower == null) {
			for (Map<BiologicalFunction, Double> predictions : predictedFunctions
					.values()) {
				predictions.remove(function);
			}
			knownFunctions.put(function, membership);
		} else {
			if (knownFunctions.containsKey(function)) {
				return;
			}
			Map<BiologicalFunction, Double> predictions = predictedFunctions
					.get(knower);
			if (predictions == null) {
				predictions = new HashMap<BiologicalFunction, Double>();
				predictedFunctions.put(knower, predictions);
			}
			predictions.put(function, membership);
		}
	}

	public void add(Iterable<BiologicalFunction> functions, Interactome knower,
			double membership) {
		for (BiologicalFunction function : functions) {
			add(function, knower, membership);
		}
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

	public int getCoreicity() {
		return species.size();
	}

	public Map<BiologicalFunction, Double> getFunctions(Interactome knower) {
		if (knower == null) {
			return knownFunctions;
		}
		Map<BiologicalFunction, Double> predictions = predictedFunctions
				.get(knower);
		if (predictions == null) {
			return Collections.emptyMap();
		}
		return predictions;
	}

	public Collection<Interaction> getInteractions() {
		return edges.values();
	}

	protected Interaction getInteractionWith(Gene gene) {
		return edges.get(gene);
	}

	public double getMembership(Interactome interactome) {
		Double value = memberships.get(interactome);
		if (value == null) {
			return Membership.Undefined;
		} else {
			return value;
		}
	}

	public double hasFunction(BiologicalFunction function, Interactome knower) {
		if (knower == null) {
			Double membership = knownFunctions.get(function);
			if (membership == null) {
				return Membership.Undefined;
			} else {
				return membership;
			}
		}
		Map<BiologicalFunction, Double> predictions = predictedFunctions
				.get(knower);
		if (predictions == null) {
			return Membership.Undefined;
		}
		return predictions.get(function);
	}

	public Iterator<Accession> iterator() {
		return ids.iterator();
	}

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

		for (Entry<BiologicalFunction, Double> entry : knownFunctions
				.entrySet()) {
			if (!first) {
				print.print(", ");
			}
			print.print(entry.getValue());
			print.print("/");
			print.print(entry.getKey());
		}

		Map<BiologicalFunction, Double> relevantPredictions = new HashMap<BiologicalFunction, Double>();
		for (Entry<Interactome, Map<BiologicalFunction, Double>> entry : predictedFunctions
				.entrySet()) {
			if (print.getContext() == null
					|| print.getContext().contains(entry.getKey())) {
				for (Entry<BiologicalFunction, Double> subentry : entry
						.getValue().entrySet()) {
					double old = 0;
					if (relevantPredictions.containsKey(subentry.getKey())) {
						old = relevantPredictions.get(subentry.getKey());
					}
					relevantPredictions.put(subentry.getKey(), Math.max(old,
							subentry.getValue()));
				}
			}
		}

		for (Entry<BiologicalFunction, Double> entry : relevantPredictions
				.entrySet()) {
			if (!first) {
				print.print(", ¿");
			}
			print.print(entry.getValue());
			print.print("/");
			print.print(entry.getKey());
			print.print("?");
		}

		print.print("}");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
