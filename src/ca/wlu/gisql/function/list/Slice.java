package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.Machine;

public class Slice extends Function {

	public static final Function self = new Slice();

	private Slice() {
		super(
				"slice",
				"Takes part of a list. If the end index is negative, the counting is done from the end of the list.",
				Type.InteractomeType, Type.NumberType, Type.NumberType,
				Type.InteractomeType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Machine machine, Object... parameters) {

		/*
		 * Our lists are 1-based and inclusive, and Java's are 0-based,
		 * inclusive on the start and exclusive on the end. Negative indicies on
		 * the end.
		 */
		int start = (Integer) parameters[0];
		int end = (Integer) parameters[1];
		List list = (List) parameters[2];

		/* Fix 0-base. */
		start--;

		/* Fix negative-based indicies. */
		if (end <= 0) {
			end += list.size();
		}

		if (start < 0) {
			machine.getEnvironment().assertWarning("Start index too small.");
			start = 0;
		} else if (start >= list.size()) {
			machine.getEnvironment().assertWarning("Start index too large.");
			start = list.size() - 1;
		}
		if (end <= start) {
			machine.getEnvironment().assertWarning("End index too small.");
			end = start + 1;
		} else if (end > list.size()) {
			machine.getEnvironment().assertWarning("End index too large.");
			end = list.size();
		}

		return list.subList(start, end);
	}

}
