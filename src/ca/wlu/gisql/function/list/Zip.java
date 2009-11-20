package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Zip extends Function {
	private static final TypeVariable a = new TypeVariable();
	private static final TypeVariable b = new TypeVariable();
	private static final TypeVariable c = new TypeVariable();

	public Zip(ExpressionRunner runner) {
		super(runner, "zip", "Dyadic version of map (a.k.a zipWith)",
				new ArrowType(a, b, c), new ListType(a), new ListType(b),
				new ListType(c));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Object... parameters) {
		GenericFunction function = (GenericFunction) parameters[0];
		List leftlist = (List) parameters[1];
		List rightlist = (List) parameters[2];

		List output = new ArrayList();
		int upperbound = Math.min(leftlist.size(), rightlist.size());
		for (int index = 0; index < upperbound; index++) {
			output.add(function.run(leftlist.get(index), rightlist.get(index)));

		}
		return output;
	}

}
