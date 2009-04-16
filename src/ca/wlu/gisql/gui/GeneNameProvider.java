package ca.wlu.gisql.gui;

import org.jgrapht.ext.VertexNameProvider;

import ca.wlu.gisql.gene.Gene;

public class GeneNameProvider implements VertexNameProvider<Gene> {

	public String getVertexName(Gene gene) {
		return gene.show(new StringBuilder()).toString();
	}

}
