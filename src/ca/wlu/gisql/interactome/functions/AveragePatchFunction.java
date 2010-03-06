package ca.wlu.gisql.interactome.functions;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class AveragePatchFunction extends Function {

	public AveragePatchFunction(ExpressionRunner runner) {
		super(
				runner,
				"avgblanks",
				"Fill in unknown interactions with the average value from the other species if both genes are present",
				Type.InteractomeType, Type.InteractomeType);
	}

	@Override
	public Object run(Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		return new Patch(source, null);
	}

}
