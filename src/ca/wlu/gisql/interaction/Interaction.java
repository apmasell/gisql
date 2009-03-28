package ca.wlu.gisql.interaction;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface Interaction extends Show {
	public long getGene1();

	public long getGene2();

	public double getMembership();

	public Interactome getParent();
}
