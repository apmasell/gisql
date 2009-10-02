package ca.wlu.gisql.environment.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.Machine;

public final class DefinedFunction extends Function {
	public static final Function self = new DefinedFunction();

	private DefinedFunction() {
		super("ls", "List defined variables", Type.UnitType, new ListType(
				Type.StringType));
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		List<String> definedVariables = new ArrayList<String>();
		for (Entry<String, AstNode> item : machine.getEnvironment()) {
			definedVariables.add(item.getKey());
		}
		return definedVariables;
	}

}
