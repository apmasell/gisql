package ca.wlu.gisql.interactome.output;

import org.jgrapht.ext.VertexNameProvider;

import ca.wlu.gisql.graph.Gene;

public class GeneNameProvider implements VertexNameProvider<Gene> {

	public String getVertexName(Gene gene) {
		return gene.show(new StringBuilder()).toString();
	}

}
