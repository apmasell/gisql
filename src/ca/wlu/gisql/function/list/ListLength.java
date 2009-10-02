package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.vm.Machine;

public class ListLength extends Function {

	public static final Function function = new ListLength();

	private ListLength() {
		super("length", "Returns the length of a list", new ListType(
				new TypeVariable()), Type.NumberType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Machine machine, Object... parameters) {
		return (long) ((List) parameters[0]).size();
	}

}
