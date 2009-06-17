package ca.wlu.gisql.interactome.output;

import org.jgrapht.ext.EdgeNameProvider;

import ca.wlu.gisql.graph.Interaction;

class InteractionNameProvider implements EdgeNameProvider<Interaction> {

	public String getEdgeName(Interaction interaction) {
		return interaction.toString();
	}

}
