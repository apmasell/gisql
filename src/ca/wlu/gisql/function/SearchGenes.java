package ca.wlu.gisql.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.runner.ExpressionRunner;

public class SearchGenes extends Function {

	public SearchGenes(ExpressionRunner runner) {
		super(runner, "findgenes", "Find genes based on a regular expression.",
				Type.StringType, new ListType(Type.GeneType));
	}

	private boolean matches(String regex, Accession accession) {
		if (Pattern.matches(regex, accession.getName())) {
			return true;
		}
		for (String alternativename : accession.getAlternativeNames()) {
			if (Pattern.matches(regex, alternativename)) {
				return true;

			}
		}
		return false;
	}

	@Override
	public Object run(Object... parameters) {
		String regex = (String) parameters[0];
		Set<Gene> genes = new HashSet<Gene>();
		for (Gene gene : Ubergraph.getInstance().genes()) {
			for (Accession accession : gene) {
				if (matches(regex, accession)) {
					genes.add(gene);
				}
			}
		}
		return new ArrayList<Gene>(genes);
	}
}
