package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class SilentFunction extends Function {

	public SilentFunction(ExpressionRunner runner) {
		super(runner, "do", "Runs a query and supresses the results",
				Type.InteractomeType, Type.UnitType);
	}

	@Override
	public Object run(Object... parameters) {
		ProcessableInteractome processableInteractome = CachedInteractome.wrap(
				(Interactome) parameters[0], null);
		processableInteractome.process();
		return Unit.nil;
	}

}
