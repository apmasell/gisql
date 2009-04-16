package ca.wlu.gisql.interaction;

import java.io.PrintStream;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;

public class RecalculatedInteraction implements Interaction {

    private Interaction interaction;

    private double membership;

    public RecalculatedInteraction(Interaction interaction, double membership) {
	this.interaction = interaction;
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
	return interaction.getParent();
    }

    public PrintStream show(PrintStream print) {
	print.print(membership);
	print.print("/(");
	interaction.show(print);
	print.print(")");
	return print;
    }

    public StringBuilder show(StringBuilder sb) {
	sb.append(membership);
	sb.append("/(");
	interaction.show(sb);
	sb.append(")");
	return sb;
    }

}
