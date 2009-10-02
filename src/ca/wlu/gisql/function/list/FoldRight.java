package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.vm.Machine;
import ca.wlu.gisql.vm.Program;

public class FoldRight extends Function {
	private static final TypeVariable a = new TypeVariable();
	public static final Function self = new FoldRight();

	private FoldRight() {
		super("foldr", "Right-recursive fold/reduce", new ArrowType(a, a, a),
				new ListType(a), a);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Machine machine, Object... parameters) {
		Program function = (Program) parameters[0];
		List input = (List) parameters[1];
		Object right = null;
		for (int index = input.size() - 1; index >= 0; index--) {
			Object left = input.get(index);
			if (right == null) {
				right = left;
			} else {
				right = function.run(machine, left, right);
			}
		}
		return right;
	}
}
