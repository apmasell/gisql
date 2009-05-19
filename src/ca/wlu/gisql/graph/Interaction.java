package ca.wlu.gisql.graph;

import java.io.PrintStream;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;
import ca.wlu.gisql.util.Show;

public class Interaction implements Show {
	private Gene gene1;

	private Gene gene2;

	private Map<Interactome, Double> memberships = new WeakHashMap<Interactome, Double>();

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

	public Gene getGene1() {
		return gene1;
	}

	public Gene getGene2() {
		return gene2;
	}

	public double getMembership(Interactome interactome) {
		Double value = memberships.get(interactome);
		if (value == null)
			return Double.NaN;
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

	public void replace(Gene original, Gene replacement) {
		if (gene1 == original && gene2 != replacement) {
			gene1 = replacement;
			original.edges.remove(gene2);
			replacement.edges.put(gene2, this);
		} else if (gene2 == original && gene1 != replacement) {
			gene2 = replacement;
			original.edges.remove(gene1);
			replacement.edges.put(gene1, this);
		} else
			throw new IllegalArgumentException(
					"Trying to replace a gene that is not in this interaction or using a replacement that laready is.");
	}

	public void setMembership(Interactome interactome, double membership) {
		memberships.put(interactome, membership);
	}

	public PrintStream show(PrintStream print) {
		print.print("(");
		gene1.show(print);
		print.print(") ⇌ (");
		gene2.show(print);
		print.print(")");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("(");
		gene1.show(sb).append(") ⇌ (");
		gene2.show(sb).append(")");
		return sb;
	}
}
