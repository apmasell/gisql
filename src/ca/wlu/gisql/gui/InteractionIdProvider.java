package ca.wlu.gisql.gui;

import org.jgrapht.ext.EdgeNameProvider;

import ca.wlu.gisql.interaction.Interaction;

public class InteractionIdProvider implements EdgeNameProvider<Interaction> {

	public String getEdgeName(Interaction interaction) {
		StringBuilder sb = new StringBuilder();
		sb.append(interaction.getGene1().getId());
		sb.append("_");
		sb.append(interaction.getGene2().getId());
		return sb.toString();
	}

}
