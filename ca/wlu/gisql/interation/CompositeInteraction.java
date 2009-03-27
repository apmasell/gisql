package ca.wlu.gisql.interation;

import ca.wlu.gisql.interactome.Interactome;

public class CompositeInteraction implements Interaction {
	private Interaction a;

	private Interaction b;

	double membership;

	private Interactome parent;

	public CompositeInteraction(Interactome parent, Interaction a,
			Interaction b, double membership) {
		this.parent = parent;
		this.a = a;
		this.b = b;
		this.membership = membership;
	}

	public long getGene1() {
		return a.getGene1();
	}

	public long getGene2() {
		return a.getGene2();
	}

	public double getMembership() {
		return membership;
	}

	public Interactome getParent() {
		return parent;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append("(");
		a.show(sb);
		sb.append(" ≈ ");
		b.show(sb);
		sb.append(") : ").append(membership);
		return sb;
	}

}
