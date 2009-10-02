package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.vm.Machine;
import ca.wlu.gisql.vm.Program;

public class FoldLeft extends Function {
	private static final TypeVariable a = new TypeVariable();
	public static final Function self = new FoldLeft();

	private FoldLeft() {
		super("foldl", "Left-recursive fold/reduce", new ArrowType(a, a, a),
				new ListType(a), a);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Machine machine, Object... parameters) {
		Program function = (Program) parameters[0];
		List input = (List) parameters[1];
		Object left = null;
		for (Object right : input) {
			if (left == null) {
				left = right;
			} else {
				left = function.run(machine, left, right);
			}
		}
		return left;
	}
}
