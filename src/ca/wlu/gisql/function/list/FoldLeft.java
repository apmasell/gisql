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

	private static final TypeVariable b = new TypeVariable();

	public static final Function self = new FoldLeft();

	/* From Haskell: foldl :: (a -> b -> a) -> a -> [b] -> a */

	private FoldLeft() {
		super("foldl", "Left-recursive fold/reduce", new ArrowType(a, b, a), a,
				new ListType(b), a);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Machine machine, Object... parameters) {
		Program function = (Program) parameters[0];
		Object left = parameters[1];
		List input = (List) parameters[2];
		for (Object right : input) {
			left = function.run(machine, left, right);
		}
		return left;
	}
}
