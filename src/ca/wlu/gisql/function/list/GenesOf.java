package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class GenesOf extends Function {

	public GenesOf(ExpressionRunner runner) {
		super(runner, "genesof",
				"Create a list of all the genes present in an interactome.",
				Type.InteractomeType, new ListType(Type.GeneType));
	}

	private boolean populate(Interactome interactome, List<Gene> genes) {
		for (Gene gene : Ubergraph.getInstance().genes()) {
			if (Membership.isPresent(interactome.calculateMembership(gene))) {
				genes.add(gene);
			}
		}
		return true;
	}

	@Override
	public Object run(Object... parameters) {
		Interactome interactome = (Interactome) parameters[0];
		List<Gene> genes = new ArrayList<Gene>();
		if (interactome.prepare() && populate(interactome, genes)
				&& interactome.postpare()) {
			return genes;
		} else {
			return Collections.emptyList();
		}
	}

}
