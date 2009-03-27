package ca.wlu.gisql.interation;

import ca.wlu.gisql.interactome.Interactome;

public class ComplementaryInteraction implements Interaction {

	private Interaction i;

	public ComplementaryInteraction(Interaction i) {
		this.i = i;
	}

	public long getGene1() {
		return i.getGene1();
	}

	public long getGene2() {
		return i.getGene2();
	}

	public double getMembership() {
		return 1 - i.getMembership();
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("¬(");
		i.show(sb);
		sb.append(")");
		return sb;
	}

	public Interactome getParent() {
		return i.getParent();
	}

}
