package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.vm.Machine;
import ca.wlu.gisql.vm.Program;

public class Map extends Function {

	private static final TypeVariable a = new TypeVariable();
	private static final TypeVariable b = new TypeVariable();
	public static final Function function = new Map();

	private Map() {
		super("map", "Applies a function to all members of a list",
				new ArrowType(a, b), new ListType(a), new ListType(b));
	}

	@Override
	public Object run(Machine machine, Object... params) {
		Program function = (Program) params[0];
		List<?> input = (List<?>) params[1];
		List<Object> output = new ArrayList<Object>();
		for (Object object : input) {
			output.add(function.run(machine, object));
		}
		return output;
	}

}
