package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.vm.Machine;

public class Cons extends Function {
	private static final TypeVariable a = new TypeVariable();
	private static final ListType alist = new ListType(a);
	public static final Function function = new Cons();

	private Cons() {
		super("cons", "Adds an item to the beginning of a list", a, alist,
				alist);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Machine machine, Object... parameters) {
		List result = new ArrayList();
		result.add(parameters[0]);
		result.addAll((List) parameters[1]);
		return result;
	}

}
