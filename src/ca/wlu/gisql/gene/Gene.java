package ca.wlu.gisql.gene;

import java.util.List;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface Gene extends Show {

	public int countOrthologs(Interactome right);

	public long getId();

	public double getMembership();

	public String getName();

	public int getNumberOfOrthologies();

	public void getSupplementaryIds(List<Long> ids);
}
