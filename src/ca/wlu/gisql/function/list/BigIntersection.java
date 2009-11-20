package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.logic.ComputedInteractome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class BigIntersection extends Function {
	private static final List<Integer> empty = Collections.emptyList();

	public BigIntersection(ExpressionRunner runner) {
		super(runner, "intersectall",
				"Create the intersection of a list of interactomes.",
				new ListType(Type.InteractomeType), Type.InteractomeType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		List<Interactome> interactomes = (List<Interactome>) parameters[0];
		List<List<Integer>> productOfSums = new ArrayList<List<Integer>>();
		List<List<Integer>> productOfSumsNegated = new ArrayList<List<Integer>>();
		for (int index = 0; index < interactomes.size(); index++) {
			productOfSums.add(Collections.singletonList(index));
			productOfSumsNegated.add(empty);
		}
		return new ComputedInteractome(interactomes, productOfSums,
				productOfSumsNegated);
	}

}
