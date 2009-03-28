package ca.wlu.gisql.interaction;

import ca.wlu.gisql.interactome.Interactome;

public class NebulonInteraction implements Interaction {

	private long gene1;

	private long gene2;

	private double membership;

	private Interactome parent;

	public NebulonInteraction(Interactome parent, long gene1, long gene2,
			double membership) {
		this.parent = parent;
		this.gene1 = gene1;
		this.gene2 = gene2;
		this.membership = membership;

	}

	public long getGene1() {
		return gene1;
	}

	public long getGene2() {
		return gene2;
	}

	public double getMembership() {
		return membership;
	}

	public Interactome getParent() {
		return parent;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(gene1).append(" ⇌ ").append(gene2).append(" : ").append(
				membership);
		return sb;
	}
}
