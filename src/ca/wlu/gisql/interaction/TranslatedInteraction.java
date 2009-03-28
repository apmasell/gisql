package ca.wlu.gisql.interaction;

import ca.wlu.gisql.interactome.Interactome;

public class TranslatedInteraction implements Interaction {

	private long g1;

	private long g2;

	private Interactome i1;

	private Interaction j2;

	public TranslatedInteraction(Interactome i1, Interaction j2, long g1,
			long g2) {
		this.i1 = i1;
		this.j2 = j2;
		this.g1 = Math.min(g1, g2);
		this.g2 = Math.max(g1, g2);
	}

	public long getGene1() {
		return g1;
	}

	public long getGene2() {
		return g2;
	}

	public double getMembership() {
		return j2.getMembership();
	}

	public Interactome getParent() {
		return i1;
	}

	public StringBuilder show(StringBuilder sb) {
		j2.show(sb).append(" → ").append(g1).append(" ⇌ ").append(g2).append(
				"[");
		i1.show(sb).append("]");
		return sb;
	}
}
