package ca.wlu.gisql.interaction;

import java.io.PrintStream;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;

public class CompositeInteraction implements Interaction {
	private Interaction interaction;

	double membership;

	private Interaction orthoaction;

	private Interactome parent;

	public CompositeInteraction(Interactome parent, Interaction interaction,
			Interaction orthoaction, double membership) {
		this.parent = parent;
		this.interaction = interaction;
		this.orthoaction = orthoaction;
		this.membership = membership;
	}

	public Gene getGene1() {
		return interaction.getGene1();
	}

	public Gene getGene2() {
		return interaction.getGene2();
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
		interaction.show(print);
		print.print(" ≈ ");
		orthoaction.show(print);
		print.print(")");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(membership).append("/(");
		interaction.show(sb);
		sb.append(" ≈ ");
		orthoaction.show(sb);
		sb.append(")");
		return sb;
	}
}
