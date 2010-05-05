package ca.wlu.gisql.function.comparisons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class SortAutomatic extends Function {

	private static Type[] makeType() {
		Type list = new ListType(new TypeVariable(TypeClass.Comparable));
		return new Type[] { list, list };
	}

	public SortAutomatic(ExpressionRunner runner) {
		super(runner, "sort", "Sorts a list of comparable items.", makeType());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		return sort((List) parameters[0]);
	}

	private <T extends Comparable<T>> List<T> sort(List<T> source) {
		List<T> list = new ArrayList<T>(source);
		Collections.sort(list);
		return list;

	}
}
