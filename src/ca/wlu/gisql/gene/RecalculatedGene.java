package ca.wlu.gisql.gene;

import java.io.PrintStream;
import java.util.List;

import ca.wlu.gisql.interactome.Interactome;

public class RecalculatedGene implements Gene {

	private Gene gene;
	private double membership;

	public RecalculatedGene(Gene gene, double membership) {
		this.gene = gene;
		this.membership = membership;
	}

	public int countOrthologs(Interactome right) {
		return gene.countOrthologs(right);
	}

	public long getId() {
		return gene.getId();
	}

	public double getMembership() {
		return membership;
	}

	public String getName() {
		return gene.getName();
	}

	public int getNumberOfOrthologies() {
		return gene.getNumberOfOrthologies();
	}

	public void getSupplementaryIds(List<Long> ids) {
		gene.getSupplementaryIds(ids);
	}

	public PrintStream show(PrintStream print) {
		print.print(membership);
		print.print("/(");
		gene.show(print);
		print.print(")");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(membership);
		sb.append("/(");
		gene.show(sb);
		sb.append(")");
		return sb;
	}

	public String toString() {
		return show(new StringBuilder()).toString();
	}
}
