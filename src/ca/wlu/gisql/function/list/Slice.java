package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Slice extends Function {

	private static final Type list = new ListType(new TypeVariable());

	public Slice(ExpressionRunner runner) {
		super(
				runner,
				"slice",
				"Takes part of a list. If the end index is negative, the counting is done from the end of the list.",
				list, Type.NumberType, Type.NumberType, list);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Object... parameters) {

		/*
		 * Our lists are 1-based and inclusive, and Java's are 0-based,
		 * inclusive on the start and exclusive on the end. Negative indices on
		 * the end.
		 */
		List list = (List) parameters[0];
		int start = ((Long) parameters[1]).intValue();
		int end = ((Long) parameters[2]).intValue();

		/* Fix 0-base. */
		start--;

		/* Fix negative-based indicies. */
		if (end <= 0) {
			end += list.size();
		}

		if (start < 0) {
			runner.getEnvironment().assertWarning("Start index too small.");
			start = 0;
		} else if (start >= list.size()) {
			runner.getEnvironment().assertWarning("Start index too large.");
			start = list.size() - 1;
		}
		if (end <= start) {
			runner.getEnvironment().assertWarning("End index too small.");
			end = start + 1;
		} else if (end > list.size()) {
			runner.getEnvironment().assertWarning("End index too large.");
			end = list.size();
		}

		return list.subList(start, end);
	}

}
