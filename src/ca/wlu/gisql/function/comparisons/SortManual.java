package ca.wlu.gisql.function.comparisons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.runner.ExpressionRunner;

public class SortManual extends Function {

	private static Type[] makeType() {
		Type variable = new TypeVariable();
		Type list = new ListType(variable);
		return new Type[] { list,
				new ArrowType(variable, variable, Type.NumberType), list };
	}

	public SortManual(ExpressionRunner runner) {
		super(
				runner,
				"sortby",
				"Sorts a list of items with an arbitrary comparison. The ordering function must return 0 if the items are the same, < 0 if the second item belongs before the first, or > 0 if the first item belongs before the second.",
				makeType());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		return sort((List) parameters[0], (GenericFunction) parameters[1]);
	}

	private <T extends Comparable<T>> List<T> sort(List<T> source,
			final GenericFunction function) {
		List<T> list = new ArrayList<T>(source);
		Collections.sort(list, new Comparator<T>() {

			@Override
			public int compare(T left, T right) {
				return ((Long) function.run(left, right)).intValue();
			}
		});
		return list;

	}
}
