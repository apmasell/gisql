package ca.wlu.gisql.util;

import org.jgrapht.graph.MaskFunctor;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.Interaction;

public class LimitedMembershipFunctor implements MaskFunctor<Gene, Interaction> {

	private double lowerbound;

	private double upperbound;

	public LimitedMembershipFunctor(double lowerbound, double upperbound) {
		this.lowerbound = lowerbound;
		this.upperbound = upperbound;
	}

	public boolean isEdgeMasked(Interaction interaction) {
		return interaction.getMembership() >= lowerbound
				&& interaction.getMembership() <= upperbound;
	}

	public boolean isVertexMasked(Gene gene) {
		return gene.getMembership() >= lowerbound
				&& gene.getMembership() <= upperbound;
	}

}