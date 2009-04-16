package ca.wlu.gisql.gene;

import java.io.PrintStream;
import java.util.List;

import ca.wlu.gisql.interactome.Interactome;

public class CompositeGene implements Gene {

	private Gene gene;

	private double membership;

	private Gene ortholog;

	private int orthologies;

	public CompositeGene(Gene gene, Gene ortholog, double membership,
			int newOrthologies) {
		this.gene = gene;
		this.ortholog = ortholog;
		this.membership = membership;
		this.orthologies = newOrthologies + gene.getNumberOfOrthologies()
				+ ortholog.getNumberOfOrthologies();
	}

	public int countOrthologs(Interactome right) {
		return gene.countOrthologs(right) + ortholog.countOrthologs(right);
	}

	public long getId() {
		return gene.getId();
	}

	public double getMembership() {

		return membership;
	}

	public String getName() {
		// TODO Can we choose the better name?
		String name = gene.getName();
		if (name == null || name.trim().length() == 0) {
			name = ortholog.getName();
		}
		return name;
	}

	public int getNumberOfOrthologies() {
		return orthologies;
	}

	public void getSupplementaryIds(List<Long> ids) {
		gene.getSupplementaryIds(ids);
		ortholog.getSupplementaryIds(ids);
	}

	public PrintStream show(PrintStream print) {
		print.print(membership);
		print.print("/(");
		gene.show(print);
		print.print(" ≈ ");
		ortholog.show(print);
		print.print(")");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(membership).append("/(");
		gene.show(sb);
		sb.append(" ≈ ");
		ortholog.show(sb);
		sb.append(")");
		return sb;
	}

	public String toString() {
		return show(new StringBuilder()).toString();
	}

}
