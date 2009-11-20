package ca.wlu.gisql.interactome.patch;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class PatchFunction extends Function {

	public PatchFunction(ExpressionRunner runner) {
		super(
				runner,
				"blanks",
				"Fill in unknown interactions with a value if both genes are present",
				Type.InteractomeType, Type.MembershipType, Type.InteractomeType);
	}

	@Override
	public Object run(Object... parameters) {
		Interactome source = (Interactome) parameters[0];
		double membership = (Double) parameters[1];
		return new Patch(source, membership);
	}

}
