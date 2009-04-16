package ca.wlu.gisql.interaction;

import java.io.PrintStream;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;

public class DbInteraction implements Interaction {

	private Gene gene1;

	private Gene gene2;

	private double membership;

	private Interactome parent;

	public DbInteraction(Interactome parent, Gene gene1, Gene gene2,
			double membership) {
		this.parent = parent;
		this.gene1 = gene1;
		this.gene2 = gene2;
		this.membership = membership;
	}

	public Gene getGene1() {
		return gene1;
	}

	public Gene getGene2() {
		return gene2;
	}

	public double getMembership() {
		return membership;
	}

	public Interactome getParent() {
		return parent;
	}

	public PrintStream show(PrintStream print) {
		print.print(membership);
		print.print("/(");
		gene1.show(print);
		print.print(") ⇌ (");
		gene2.show(print);
		print.print(")");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(membership).append("/(");
		gene1.show(sb).append(") ⇌ (");
		gene2.show(sb).append(")");
		return sb;
	}
}
