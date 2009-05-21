package ca.wlu.gisql.interactome.output;

import org.jgrapht.ext.EdgeNameProvider;

import ca.wlu.gisql.graph.Interaction;

class InteractionIdProvider implements EdgeNameProvider<Interaction> {

	public String getEdgeName(Interaction interaction) {
		StringBuilder sb = new StringBuilder();
		GeneIdProvider.appendName(interaction.getGene1(), sb);
		sb.append("_");
		GeneIdProvider.appendName(interaction.getGene2(), sb);
		return sb.toString();
	}

}
