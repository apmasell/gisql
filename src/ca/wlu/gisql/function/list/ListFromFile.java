package ca.wlu.gisql.function.list;

import java.io.File;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class ListFromFile extends Function {

	public ListFromFile(ExpressionRunner runner) {
		super(runner, "read",
				"Reads interactome expressions from a file and makes a list",
				Type.StringType, new ListType(Type.InteractomeType));
	}

	@Override
	public Object run(Object... parameters) {
		String filename = (String) parameters[0];
		ListFromFileListener listener = new ListFromFileListener(runner
				.getListener());
		ExpressionRunner runner = new ExpressionRunner(this.runner
				.getEnvironment(), listener);
		runner.run(new File(filename), Type.InteractomeType);
		return listener.getList();
	}
}
