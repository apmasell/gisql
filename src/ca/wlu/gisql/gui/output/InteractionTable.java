package ca.wlu.gisql.gui.output;

import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.CachedInteractome;

public class InteractionTable extends AbstractTable {

	public InteractionTable(CachedInteractome interactome) {
		super(interactome, new Class[] { String.class, String.class,
				Double.class, String.class }, new String[] { "Gene1", "Gene2",
				"Membership", "Description" });
	}

	public int getRowCount() {
		return interactome.getInteractions().size();
	}

	public final Object getValueAt(int rowIndex, int colIndex) {
		Interaction interaction = interactome.getInteractions().get(rowIndex);

		if (interaction == null)
			return null;

		switch (colIndex) {
		case 0:
			return interaction.getGene1().show(new StringBuilder()).toString();
		case 1:
			return interaction.getGene2().show(new StringBuilder()).toString();
		case 2:
			return interaction.getMembership(interactome);
		case 3:
			return interaction.show(new StringBuilder()).toString();
		default:
			return null;
		}
	}
}
