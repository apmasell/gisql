package ca.wlu.gisql.interactome.output;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class OutputFunction extends Function {
	public OutputFunction(ExpressionRunner runner) {
		super(runner, "output", "Redirect interactome data to a file",
				Type.InteractomeType, Type.FormatType, Type.StringType,
				Type.InteractomeType);
	}

	@Override
	public Object run(Object... parameters) {
		Interactome interactome = (Interactome) parameters[0];
		FileFormat format = (FileFormat) parameters[1];
		String filename = (String) parameters[2];
		return AbstractOutput.wrap(interactome, null, format, filename, true);
	}

}
