package ca.wlu.gisql.function;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.Machine;

public class Range extends Function {
	public static final Function self = new Range();

	private Range() {
		super("range", "Create a list of numbers over a certain interval",
				Type.NumberType, Type.NumberType, new ListType(Type.NumberType));
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		long start = (Long) parameters[0];
		long end = (Long) parameters[1];
		if (end < start) {
			end = start;
		}
		List<Long> range = new ArrayList<Long>();
		while (start <= end) {
			range.add(start);
			start++;
		}
		return range;
	}

}
