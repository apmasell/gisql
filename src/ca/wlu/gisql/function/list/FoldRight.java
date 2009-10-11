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

	private static final TypeVariable b = new TypeVariable();

	public static final Function self = new FoldRight();

	/* From Haskell: foldr :: (a -> b -> b) -> b -> [a] -> b */

	private FoldRight() {
		super("foldr", "Right-recursive fold/reduce", new ArrowType(a, b, b),
				b, new ListType(a), b);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Machine machine, Object... parameters) {
		Program function = (Program) parameters[0];
		Object right = parameters[1];
		List input = (List) parameters[2];
		for (int index = input.size() - 1; index >= 0; index--) {
			Object left = input.get(index);
			right = function.run(machine, left, right);
		}
		return right;
	}
}
