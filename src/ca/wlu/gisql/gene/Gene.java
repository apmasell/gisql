package ca.wlu.gisql.gene;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;

public interface Gene extends Show {

    public long getId();

    public double getMembership();
    
    public String getName();

    public int getNumberOfOrthologies();

    public int countOrthologs(Interactome right);

}
