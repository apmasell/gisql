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

public class BigUnion extends Function {
	private static final List<Integer> empty = Collections.emptyList();

	public BigUnion(ExpressionRunner runner) {
		super(runner, "unionall",
				"Create the union of a list of interactomes.", new ListType(
						Type.InteractomeType), Type.InteractomeType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		List<Interactome> interactomes = (List<Interactome>) parameters[0];
		List<Integer> sums = new ArrayList<Integer>();

		for (int index = 0; index < interactomes.size(); index++) {
			sums.add(index);
		}
		return new ComputedInteractome(interactomes, Collections
				.singletonList(sums), Collections.singletonList(empty));
	}

}
