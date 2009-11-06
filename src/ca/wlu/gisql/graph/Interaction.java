package ca.wlu.gisql.graph;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Construction;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Represents an interaction between two genes. There is no directionality to
 * interactions and interactions are guaranteed to be singleton.
 */
public class Interaction implements Show<Set<Interactome>> {
	private final Gene gene1;

	private final Gene gene2;

	private final Map<Interactome, Double> memberships = new WeakHashMap<Interactome, Double>();

	Interaction(Gene gene1, Gene gene2) {
		if (gene1 == gene2) {
			throw new IllegalArgumentException(
					"A gene cannot interact with itself.");
		}
		this.gene1 = gene1;
		this.gene2 = gene2;
		gene1.edges.put(gene2, this);
		gene2.edges.put(gene1, this);
	}

	/**
	 * Calculates the memberships of all the species interactomes for this
	 * interaction.
	 */
	public double getAverageMembership() {
		double sum = 0;
		int count = 0;
		for (Entry<Interactome, Double> entry : memberships.entrySet()) {
			if (entry.getKey().getConstruction() == Construction.Species) {
				sum += entry.getValue();
				count++;
			}
		}
		return sum / count;
	}

	/** Gets one of the genes in this interaction. The order is arbitrary. */
	public Gene getGene1() {
		return gene1;
	}

	/** Gets one of the genes in this interaction. The order is arbitrary. */
	public Gene getGene2() {
		return gene2;
	}

	/**
	 * Determine the stored membership of this interaction in a particular
	 * interactome. If the value has not been recorded, it will be
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

	/** Returns the other gene in this interaction (i.e., the one not provided). */
	public Gene getOther(Gene gene) {
		if (gene == gene1) {
			return gene2;
		}
		if (gene == gene2) {
			return gene1;
		}
		return null;
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
		print.print("(");
		print.print(gene1);
		print.print(") ⇌ (");
		print.print(gene2);
		print.print(")");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
