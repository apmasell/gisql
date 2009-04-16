package ca.wlu.gisql.interaction;

import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface Interaction extends Show {
	public Gene getGene1();

	public Gene getGene2();

	public double getMembership();

	public Interactome getParent();
}
