package ca.wlu.gisql.gui;

import org.jgrapht.ext.EdgeNameProvider;

import ca.wlu.gisql.interaction.Interaction;

public class InteractionNameProvider implements EdgeNameProvider<Interaction> {

    public String getEdgeName(Interaction interaction) {
	return interaction.show(new StringBuilder()).toString();
    }

}
