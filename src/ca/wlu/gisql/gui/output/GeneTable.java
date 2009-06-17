package ca.wlu.gisql.gui.output;

import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.interactome.CachedInteractome;

public class GeneTable extends AbstractTable {

	public GeneTable(CachedInteractome interactome) {
		super(interactome, new Class[] { String.class, Double.class },
				new String[] { "Identifiers", "Membership" });
	}

	public int getRowCount() {
		return interactome.getGenes().size();
	}

	public Object getValueAt(int rowIndex, int colIndex) {
		Gene gene = interactome.getGenes().get(rowIndex);

		if (gene == null)
			return null;

		switch (colIndex) {
		case 0:
			return gene.toString();
		case 1:
			return gene.getMembership(interactome);
		default:
			return null;
		}
	}
}
