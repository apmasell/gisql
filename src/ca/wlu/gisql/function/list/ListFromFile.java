package ca.wlu.gisql.function.list;

import java.io.File;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.vm.Machine;

public class ListFromFile extends Function {
	public static final Function function = new ListFromFile(
			Type.InteractomeType);

	// TODO dependent type
	private final Type dependenttype;

	public ListFromFile(Type dependenttype) {
		super("read",
				"Reads interactome expressions from a file and makes a list",
				Type.StringType, new ListType(dependenttype));
		this.dependenttype = dependenttype;
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		String filename = (String) parameters[0];
		ListFromFileListener listener = new ListFromFileListener(machine
				.getListener());
		ExpressionRunner runner = new ExpressionRunner(
				machine.getEnvironment(), listener);
		runner.run(new File(filename), dependenttype);
		return listener.getList();
	}
}
