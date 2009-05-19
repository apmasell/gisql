package ca.wlu.gisql.interactome.output;

import org.jgrapht.ext.VertexNameProvider;

import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.Gene;

public class GeneIdProvider implements VertexNameProvider<Gene> {
	public static StringBuilder appendName(Gene gene, StringBuilder sb) {
		boolean first = true;
		for (Accession accession : gene) {
			if (!first) {
				sb.append("e");
				first = false;
			}
			sb.append(accession.getIdentifier());
		}
		return sb;
	}

	public String getVertexName(Gene gene) {
		StringBuilder sb = new StringBuilder();
		appendName(gene, sb);
		return sb.toString();
	}

}
