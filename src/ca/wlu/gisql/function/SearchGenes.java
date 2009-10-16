package ca.wlu.gisql.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.vm.Machine;

public class SearchGenes extends Function {
	public static final Function self = new SearchGenes();

	private SearchGenes() {
		super("findgenes", "Find genes based on a regular expression.",
				Type.StringType, new ListType(Type.GeneType));
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		String regex = (String) parameters[0];
		Set<Gene> genes = new HashSet<Gene>();
		for (Gene gene : Ubergraph.getInstance().genes()) {
			for (Accession accession : gene) {
				if (Pattern.matches(regex, accession.getName())) {
					genes.add(gene);
					break;
				}
			}
		}
		return new ArrayList<Gene>(genes);
	}
}
