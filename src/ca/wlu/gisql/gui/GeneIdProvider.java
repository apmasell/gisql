package ca.wlu.gisql.gui;

import org.jgrapht.ext.VertexNameProvider;

import ca.wlu.gisql.gene.Gene;

public class GeneIdProvider implements VertexNameProvider<Gene> {

    public String getVertexName(Gene gene) {
	return Long.toString(gene.getId());
    }

}
