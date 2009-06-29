package ca.wlu.gisql.graph;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Interaction implements Show<Set<Interactome>> {
	private Gene gene1;

	private Gene gene2;

	private final Map<Interactome, Double> memberships = new WeakHashMap<Interactome, Double>();

	Interaction(Gene gene1, Gene gene2) {
		if (gene1 == gene2)
			throw new IllegalArgumentException(
					"A gene cannot interact with itself.");
		this.gene1 = gene1;
		this.gene2 = gene2;
		gene1.edges.put(gene2, this);
		gene2.edges.put(gene1, this);
	}

	void copyMembership(Interaction interaction) {
		for (Entry<Interactome, Double> entry : interaction.memberships
				.entrySet()) {
			double membership;
			Double thisMembership = memberships.get(entry.getKey());
			if (thisMembership == null) {
				membership = entry.getValue();
			} else if (entry.getKey().getType() == Type.Computed) {
				membership = entry.getKey().calculateMembership(interaction);
			} else {
				membership = Math.max(entry.getValue(), thisMembership);
			}
			memberships.put(entry.getKey(), membership);
		}
	}

	public double getAverageMembership() {
		double sum = 0;
		for (double value : memberships.values())
			sum += value;
		return sum / memberships.size();
	}

	public Gene getGene1() {
		return gene1;
	}

	public Gene getGene2() {
		return gene2;
	}

	public double getMembership(Interactome interactome) {
		Double value = memberships.get(interactome);
		if (value == null)
			return GisQL.Undefined;
		else
			return value;
	}

	public Gene getOther(Gene gene) {
		if (gene == gene1)
			return gene2;
		if (gene == gene2)
			return gene1;
		return null;
	}

	protected void replace(Gene original, Gene replacement) {
		Gene partner;
		if (gene1 == original && gene2 != replacement) {
			gene1 = replacement;
			partner = gene2;
		} else if (gene2 == original && gene1 != replacement) {
			gene2 = replacement;
			partner = gene1;
		} else {
			throw new IllegalArgumentException(
					"Trying to replace a gene that is not in this interaction or using a replacement that already is.");
		}
		original.edges.remove(partner);
		replacement.edges.put(partner, this);
		partner.edges.remove(original);
		partner.edges.put(replacement, this);
	}

	public void setMembership(Interactome interactome, double membership) {
		if (GisQL.isUndefined(membership))
			membership = GisQL.Missing;
		memberships.put(interactome, membership);
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print("(");
		print.print(gene1);
		print.print(") ⇌ (");
		print.print(gene2);
		print.print(")");
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
